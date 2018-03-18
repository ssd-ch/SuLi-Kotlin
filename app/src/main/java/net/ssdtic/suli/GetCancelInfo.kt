package net.ssdtic.suli

import android.util.Log

class GetCancelInfo {

    companion object {

        private val cancelInfoUrl = MyApplication.getContext()?.getString(R.string.address_cancelInfo)

        fun start(completeHandler: () -> Unit, errorHandler: (String) -> Unit) {

            Log.d("Info", "GetCancelInfo : start task $cancelInfoUrl")

            getData(1, null, completeHandler, errorHandler)
        }

        fun cancel() {
            Log.d("Info", "GetCancelInfo : cancel task")
        }

        private fun getData(page: Int, data: Array<Int>?, completeHandler: () -> Unit, errorHandler: (String) -> Unit) {

            Log.d("Info", "run complete handler")
            completeHandler()
            Log.d("Info", "run error handler")
            errorHandler("test")

        }
    }
}