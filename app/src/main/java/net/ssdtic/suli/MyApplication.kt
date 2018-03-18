package net.ssdtic.suli

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    companion object {
        var mContext: Context? = null

        fun getContext(): Context? = mContext
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
    }
}