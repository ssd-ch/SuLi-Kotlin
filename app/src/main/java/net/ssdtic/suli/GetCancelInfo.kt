package net.ssdtic.suli

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result

class GetCancelInfo {

    companion object {

        private val cancelInfoUrl = MyApplication.getContext()?.applicationContext?.getString(R.string.address_cancelInfo)

        fun start(completeHandler: () -> Unit, errorHandler: (String) -> Unit) {

            Log.d("Info", "GetCancelInfo : start task $cancelInfoUrl")

            getData(1, emptyArray(), completeHandler, errorHandler)
        }

        fun cancel() {
            Log.d("Info", "GetCancelInfo : cancel task")
        }

        private fun getData(page: Int, data: Array<CancelInfo>, completeHandler: () -> Unit, errorHandler: (String) -> Unit) {

            var writeData : Array<CancelInfo> = data

            Log.d("Info", "GetCancelInfo : No.$page data init")

            Fuel.get(cancelInfoUrl!!, listOf("abspage" to page)).response { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        // レスポンスボディを表示
                        println("非同期処理の結果：" + String(response.data))
                    }
                    is Result.Failure -> {
                        println("通信に失敗しました。")
                    }
                }
            }

            completeHandler()
            Log.d("Info", "run error handler")
            errorHandler("test")

        }
    }
}