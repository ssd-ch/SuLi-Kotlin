package net.ssdtic.suli

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.Result
import io.realm.Realm
import org.jsoup.Jsoup


class GetBuildingList {

    companion object {

        private val buildingUrl = MyApplication.getContext()?.getString(R.string.address_buildingList)
                ?: ""

        private var opt: Request? = null

        fun start(completeHandler: () -> Unit, errorHandler: (String) -> Unit) {

            println("GetBuildingList : start task")

            try {
                opt = Fuel.get(buildingUrl).response { request, response, result ->

                    when (result) {

                        is Result.Failure -> {
                            val err = result.error
                            print("GetBuildingList : failed task. ${err.localizedMessage}")
                            errorHandler(err.localizedMessage)
                        }

                        is Result.Success -> {

                            Jsoup.parse(response.toString()).select(".body li a").let { doc ->

                                //Realmに接続
                                val realm = Realm.getDefaultInstance()

                                //Buildingのすべてのオブジェクトを取得
                                val buildings = realm.where(Building::class.java).findAll()

                                //トランザクションを開始
                                realm.executeTransaction {
                                    //取得したすべてのオブジェクトを削除
                                    buildings.deleteAllFromRealm()

                                    for (i in 0 until doc.size) {
                                        //書き込むデータを作成
                                        val writeData = realm.createObject(Building::class.java, i)
                                        writeData.building_name = doc[i].text().replace("教室配当表_", "").replace("_", " ")
                                        writeData.url = doc[i].attr("abs:href")
                                        writeData.color = "#000000"
                                        //データをRealmに書き込む
                                        realm.copyToRealm(writeData)
                                    }
                                }

                                println("GetBuildingList : all task complete")
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