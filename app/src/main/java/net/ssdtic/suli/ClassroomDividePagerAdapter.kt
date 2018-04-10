package net.ssdtic.suli

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ClassroomDividePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> Fragment()
        1 -> Fragment()
        2 -> Fragment()
        3 -> Fragment()
        4 -> Fragment()
        else -> null
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "MON"
        1 -> "THU"
        2 -> "WED"
        3 -> "THA"
        4 -> "FRI"
        else -> ""
    }

    override fun getCount(): Int = 5

}