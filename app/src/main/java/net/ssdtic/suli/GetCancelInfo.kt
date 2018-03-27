package net.ssdtic.suli

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.Result
import io.realm.Realm
import org.jsoup.Jsoup


class GetCancelInfo {

    companion object {

        private val cancelInfoUrl = MyApplication.getContext()?.getString(R.string.address_cancelInfo) ?: ""

        private var opt :Request? = null

        fun start(completeHandler: () -> Unit, errorHandler: (String) -> Unit) {

            println("GetCancelInfo : start task")

            getData(1, emptyArray(), { writeData ->

                //Realmに接続
                val realm = Realm.getDefaultInstance()
                //CancelInfoのすべてのオブジェクトを取得
                val cancelInfo = realm.where(CancelInfo::class.java).findAll()

                realm.executeTransaction {
                    //取得したすべてのオブジェクトを削除
                    cancelInfo.deleteAllFromRealm()
                    println("GetCancelInfo : all cancelInfo data delete")

                    for ((i,data) in writeData.withIndex()) {
                        //マルチスレッドで処理したのでIDが競合するのを防ぐためにここで設定する
                        data.id = i + 1
                        //データを書き込む
                        realm.copyToRealm(data)
                    }
                    println("GetCancelInfo : all data is written")
                }

                println("GetCancelInfo : all task complete")
                completeHandler()
            }, { message ->
                print("GetCancelInfo : failed task")
                errorHandler(message)
            })
        }

        private fun getData(page: Int, data: Array<CancelInfo>, completeHandler: (Array<CancelInfo>) -> Unit, errorHandler: (String) -> Unit) {

            var writeData : Array<CancelInfo> = data

            try {
                println("GetCancelInfo : No.$page data init")

                opt = Fuel.get(cancelInfoUrl, listOf("abspage" to page)).response { request, response, result ->

                    when (result) {

                        is Result.Failure -> {
                            val err = result.error
                            println("GetCancelInfo : No.$page data failed ${err.localizedMessage}")
                            errorHandler(err.localizedMessage)
                        }

                        is Result.Success -> {

                            Jsoup.parse(response.toString()).select(".table_data tr").let { doc ->

                                if (doc.size >= 2) {
                                    for (i in 1 until doc.size) {
                                        val tdNodes = doc[i].select("td")

                                        // 書き込むデータを作成
                                        val data = CancelInfo()

                                        data.date = tdNodes[0].text()
                                        data.classification = tdNodes[1].text()
                                        data.time = tdNodes[2].text()
                                        data.department = tdNodes[3].text()
                                        data.classname = tdNodes[4].text()
                                        data.person = tdNodes[5].text()
                                        data.place = tdNodes[6].text()
                                        data.note = tdNodes[7].text()

                                        writeData.plus(data)
                                    }

                                }

                                println("GetCancelInfo : No.$page data complete")
                            }

                            Jsoup.parse(response.toString()).select(".prevnextpage")?.let { doc ->

                                if (doc.size < (if (page == 1) 1 else 2)) {
                                    //これ以上読み込むページがない(処理が完了)
                                    completeHandler(writeData)
                                }
                                else {
                                    //次のページを読み込む
                                    getData(page + 1, writeData, completeHandler, errorHandler)
                                }
                            } ?: run {
                                errorHandler("GetCancelInfo: can't get next page flag")
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