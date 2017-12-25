package net.ssdtic.suli

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MainFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return SyllabusFragment()
            1 -> return ClassroomDivideFragment()
            2 -> return NoticeFragment()
            3 -> return MaterialFragment()
            4 -> return SettingFragment()
        }
        return null
    }

    override fun getCount(): Int {
        return 5
    }
}
