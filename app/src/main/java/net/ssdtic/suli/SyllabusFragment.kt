package net.ssdtic.suli

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SyllabusFragment : Fragment() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        GetBuildingList.start({
            Log.d("Info", "complete1")
            Log.d("Info", "complete2")
        }, { message -> Log.d("Info", "message: $message") })

        return inflater!!.inflate(R.layout.fragment_syllabus, null)
    }

}
