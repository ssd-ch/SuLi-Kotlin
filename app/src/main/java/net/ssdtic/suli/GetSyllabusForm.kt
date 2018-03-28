package net.ssdtic.suli

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.Result
import io.realm.Realm
import org.jsoup.Jsoup
import java.nio.charset.Charset


class GetSyllabusForm {
    companion object {

        private val syllabusFormUrl = MyApplication.getContext()?.getString(R.string.address_syllabus_form)
                ?: ""

        private var opt: Request? = null

        fun start(completeHandler: () -> Unit, errorHandler: (String) -> Unit) {

            println("GetSyllabusForm : start task")

            try {
                opt = Fuel.get(syllabusFormUrl).response { request, response, result ->

                    when (result) {

                        is Result.Failure -> {
                            val err = result.error
                            print("GetSyllabusForm : failed task. ${err.localizedMessage}")
                            errorHandler(err.localizedMessage)
                        }

                        is Result.Success -> {

                            val resultStr = String(response.data, Charset.forName("Shift-JIS"))

                            Jsoup.parse(resultStr).select("table").first().let { doc ->

                                //Realmに接続
                                val realm = Realm.getDefaultInstance()

                                //SyllabusFormのすべてのオブジェクトを取得
                                val syllabusForm = realm.where(SyllabusForm::class.java).findAll()

                                //トランザクションを開始
                                realm.executeTransaction {

                                    //取得したすべてのオブジェクトを削除
                                    syllabusForm.deleteAllFromRealm()

                                    //年度
                                    doc.select("[name=nendo]").first().attr("value").let { year ->
                                        //書き込むデータを用意する
                                        val writeData = realm.createObject(SyllabusForm::class.java, 0)
                                        writeData.form = "nendo"
                                        writeData.display = year.replace("\r\n|\n", "")
                                        writeData.value = year
                                        //データをRealmに書き込む
                                        realm.copyFromRealm(writeData)
                                        return@let
                                    }

                                    //学部
                                    val options1= doc.select("[name=j_s_cd] option")
                                    for ((i, node) in options1.withIndex()) {
                                        val writeData = realm.createObject(SyllabusForm::class.java, "1%03d".format(i).toInt())
                                        writeData.form = "j_s_cd"
                                        writeData.display = node.text().replace("\r\n|\n","")
                                        writeData.value = node.attr("value")
                                        //データをRealmに書き込む
                                        realm.copyFromRealm(writeData)
                                    }

                                    //科目分類
                                    val options2= doc.select("[name=kamokud_cd] option")
                                    for ((i, node) in options2.withIndex()) {
                                        val writeData = realm.createObject(SyllabusForm::class.java, "2%03d".format(i).toInt())
                                        writeData.form = "kamokud_cd"
                                        writeData.display = node.text().replace("\r\n|\n","")
                                        writeData.value = node.attr("value")
                                        //データをRealmに書き込む
                                        realm.copyFromRealm(writeData)
                                    }

                                    //曜日
                                    val options3= doc.select("[name=yobi] option")
                                    for ((i, node) in options3.withIndex()) {
                                        val writeData = realm.createObject(SyllabusForm::class.java, "3%03d".format(i).toInt())
                                        writeData.form = "yobi"
                                        writeData.display = node.text().replace("\r\n|\n","")
                                        writeData.value = node.attr("value")
                                        //データをRealmに書き込む
                                        realm.copyFromRealm(writeData)
                                    }

                                    //時限
                                    val options4= doc.select("[name=jigen] option")
                                    for ((i, node) in options4.withIndex()) {
                                        val writeData = realm.createObject(SyllabusForm::class.java, "4%03d".format(i).toInt())
                                        writeData.form = "jigen"
                                        writeData.display = node.text().replace("\r\n|\n","")
                                        writeData.value = node.attr("value")
                                        //データをRealmに書き込む
                                        realm.copyFromRealm(writeData)
                                    }

                                }

                                //処理が完了
                                println("GetSyllabusForm : all task complete")
                                completeHandler()
                            }

                        }

                    }
                }

            } catch (error: Exception) {
                println("got an error creating the request: $error")
                errorHandler(error.localizedMessage)
            }

        }

        fun cancel() {
            opt?.cancel()
        }
    }
}