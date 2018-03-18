package net.ssdtic.suli

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm

class NoticeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Obtain a Realm instance
        val realm = Realm.getDefaultInstance()

        realm.beginTransaction()

        //... オブジェクトの追加や更新 ...

        realm.commitTransaction()

        GetCancelInfo.start({
            Log.d("Info", "complete1")
            Log.d("Info", "complete2")
        }, { message -> Log.d("Info", "message: $message") })

        return inflater!!.inflate(R.layout.fragment_notice, null)
    }

}
