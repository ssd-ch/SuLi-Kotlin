package net.ssdtic.suli

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.Result
import io.realm.Realm
import kotlinx.coroutines.experimental.*
import org.jsoup.Jsoup


class GetClassroomDivide {

    companion object {

        private var opt = emptyArray<Request>()

        fun start(completeHandler: () -> Unit, errorHandler: (String) -> Unit) {

            println("GetClassroomDivide : start task")

            //リクエストを初期化
            opt = emptyArray()

            //建物別のリンクを取得
            GetBuildingList.start({

                launch {

                    //Realmに接続
                    val realm = Realm.getDefaultInstance()

                    //ClassroomDivideのすべてのオブジェクトを取得
                    val objects = realm.where(ClassroomDivide::class.java).findAll()
                    //トランザクションを開始
                    realm.executeTransaction {
                        //取得したすべてのオブジェクトを削除
                        objects.deleteAllFromRealm()
                        println("GetClassroomDivide : all ClassroomDivide data delete")
                    }

                    //Buildingのすべてのオブジェクトを取得
                    val buildings = realm.where(Building::class.java).findAll()

                    //すべてのページの配当表を取得
                    for (building in buildings) {
                        println("GetClassroomDivide : No.${building.id} data init")
                        scrapingClassroomDivide(building.id, building.url).await()
                    }

                    //すべてのスレッドの処理が完了
                    completeHandler()
                }


            }, { message ->
                println("GetClassroomDivide : failed task.")
                errorHandler(message)
            })
        }

        private fun scrapingClassroomDivide(building_id: Int, url: String) = async(CommonPool) {

            try {

                opt += Fuel.get(url).response { _, response, result ->

                    when (result) {

                        is Result.Failure -> {
                            val err = result.error
                            println("GetClassroomDivide : failed task. ${err.localizedMessage}")
                        }

                        is Result.Success -> {

                            Jsoup.parse(response.toString()).select(".body tr").let { doc ->

                                //Realmに接続
                                val realm = Realm.getDefaultInstance()

                                //トランザクションを開始
                                realm.executeTransaction {

                                    //色の取得
                                    doc[0].select("td").first().attr("style").let { style ->
                                        Regex("#[0-9a-fA-F]{3,6}").find(style)?.value.let {
                                            realm.where(Building::class.java).equalTo("id", building_id).findFirst().color = it!!
                                        }
                                    }


                                    //各教室の取得
                                    var places = emptyArray<String>()
                                    var placeCnt = 0
                                    for (td in doc[0].select("td").drop(1)) {
                                        for (i in 0 until (if (td.attr("colspan") == "") 1 else td.attr("colspan").toInt())) {
                                            if (td.attr("rowspan") == "") {
                                                places += td.text() + " " + doc[1].select("td")[placeCnt].text()
                                                placeCnt += 1
                                            } else {
                                                places += arrayOf(td.text())
                                            }
                                        }
                                    }
                                    for (i in 0 until places.size) {
                                        places[i] = places[i].replace("\r\n|\n".toRegex(), "")
                                        places[i] = places[i].replace("[_　]".toRegex(), " ")
                                        places[i] = StringUtil.fullWidthNumberToHalfWidthNumber(places[i])
                                    }

                                    for (d in 0 until 5) { //月から金のループ
                                        val point = d * 5 + 2 //trタグの位置
                                        for (i in 0 until 5) { //1から5コマのループ

                                            val tdData = doc[point + i].select("td")
                                            for (pn in 0 until places.size) { //場所のループ
                                                var text = tdData[if (i == 0) pn + 2 else pn + 1].text() //一番最初は曜日名が入るので一つずらす
                                                var lecInfo = arrayOf("", "", "", "")
                                                var color = "#000000"

                                                if (text == "${Typography.nbsp}" || text == "" || text == "\r\n" || text == "\n") { //&nbsp;,"",改行のみ
                                                    text = ""
                                                } else {
                                                    text = text.replace("\r\n|\n".toRegex(), "")
                                                    lecInfo = extractionLecture(text)

                                                    val span = tdData[if (i == 0) pn + 2 else pn + 1].select("span")
                                                    if (!span.isEmpty()) {
                                                        Regex("#[0-9a-fA-F]{3,6}").find(span.first().html())?.let {
                                                            color = it.value
                                                        }
                                                    }
                                                }

                                                val id = "%02d".format(building_id) + "%02d".format(pn) + "$d$i"

                                                //書き込むデータを作成
                                                val resultData = realm.createObject(ClassroomDivide::class.java, id)
                                                resultData.building_id = building_id
                                                resultData.place = places[pn]
                                                resultData.weekday = d
                                                resultData.time = i + 1
                                                resultData.cell_text = text
                                                resultData.cell_color = color
                                                resultData.classname = lecInfo[0]
                                                resultData.person = lecInfo[1]
                                                resultData.department = lecInfo[2]
                                                resultData.class_code = lecInfo[3]

                                                //データをRealmに書き込む
                                                realm.copyToRealm(resultData)
                                            }
                                        }
                                    }

                                    if (doc.size > 27) {
                                        var placeText = ""
                                        var dayCache = ""
                                        var pn = doc[2].select("td").size - 2

                                        for (i in 27 until doc.size) {
                                            val tdNode = doc[i].select("td")
                                            val tdText = arrayOf(tdNode[0].text(), tdNode[1].text(), tdNode[2].text().replace("\r\n|\n".toRegex(), ""))
                                            if (Regex("|${Typography.nbsp}").matches(tdText[0]) && Regex("|${Typography.nbsp}").matches(tdText[1])) {
                                                placeText = StringUtil.fullWidthNumberToHalfWidthNumber(tdText[2].replace(" ", ""))
                                                placeText = placeText.replace("[_　]".toRegex(), " ").replace("\r\n|\n".toRegex(), "")
                                            } else {
                                                tdText[0] = tdText[0].replace("[ 　\u00A0]".toRegex(), "")
                                                tdText[1] = tdText[1].replace("[ 　\u00A0]".toRegex(), "")
                                                if (tdText[0] == "") {
                                                    tdText[0] = dayCache
                                                }
                                                dayCache = tdText[0]
                                                val day = Regex(tdText[0]).find("月火水木金土日")?.range?.first
                                                        ?: -1
                                                val time = StringUtil.matcherSubString(tdText[1], "\\..*").replace("\\.".toRegex(), "").toInt() / 2
                                                val id = "%02d".format(building_id) + "%02d".format(pn) + "$day$time"
                                                pn += 1
                                                val lecInfo = extractionLecture(tdText[2])

                                                //書き込むデータを作成
                                                val resultData = realm.createObject(ClassroomDivide::class.java, id)
                                                resultData.building_id = building_id
                                                resultData.place = placeText
                                                resultData.weekday = day
                                                resultData.time = time
                                                resultData.cell_text = tdText[2]
                                                resultData.cell_color = "#000000"
                                                resultData.classname = lecInfo[0]
                                                resultData.person = lecInfo[1]
                                                resultData.department = lecInfo[2]
                                                resultData.class_code = lecInfo[3]

                                                //println("${resultData.id} : ${resultData.place} - ${resultData.cell_text}")

                                                //データをRealmに書き込む
                                                realm.copyToRealm(resultData)
                                            }
                                        }

                                    }

                                }

                                //Realmを閉じる
                                realm.close()

                                println("GetClassroomDivide : No.$building_id data complete")
                            }

                        }

                    }
                }

            } catch (error: Exception) {
                println("got an error creating the request: $error")
            }

        }

        private fun extractionLecture(str: String): Array<String> {

            val result = arrayOf("", "", "", "")

            val d_code = "LSEHMSAFCＬＳＥＨＭＳＡＦＣ○*"

            var text = str.replace("、", " ")

            //授業名
            if (Regex(".*『.*』.*").matches(text)) {
                result[0] = StringUtil.matcherSubString(text, "『.*』").replace("[『』]".toRegex(), "")
                text = text.replace("『.*』".toRegex(), "")
            }

            //時間割コード
            if (Regex(".*[A-Z0-9]{6}.*").matches(text)) {
                result[3] = StringUtil.matcherSubString(text, "[A-Z0-9]{6,}")
                text = text.replace("[A-Z0-9/]{6,}".toRegex(), "")
            }

            //担当者情報
            if (Regex(".*[$d_code].*").matches(text)) {
                val t = StringUtil.matcherSubString(text, "[$d_code]{1,2}[^$d_code]*")
                result[2] = StringUtil.matcherSubString(t, "[$d_code]{1,2}")
                result[1] = t.replace("[$d_code]".toRegex(), "")
            }

            for (i in 0 until result.size) {
                result[i] = result[i].replace("[　 ]".toRegex(), "")
            }

            //println("授業名:${result[0]} 時間割コード:${result[3]} 担当者名:${result[1]} 担当者所属:${result[2]}")

            return result
        }

        fun cancel() {
            GetBuildingList.cancel()
            for (request in opt) {
                request.cancel()
            }
        }
    }
}