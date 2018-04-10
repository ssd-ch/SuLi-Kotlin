package net.ssdtic.suli

import org.jetbrains.anko.*
import org.jetbrains.anko.design.tabLayout
import org.jetbrains.anko.support.v4.viewPager

class ClassroomDivideFragmentUI : AnkoComponent<ClassroomDivideFragment> {

    override fun createView(ui: AnkoContext<ClassroomDivideFragment>) = with(ui) {

        verticalLayout {

            val tabLayout = tabLayout {

            }.lparams(width = matchParent, height = wrapContent)

            val viewPager = viewPager {
                id = R.id.ClassroomDividePager
                offscreenPageLimit = 2
            }.lparams(width = matchParent, height = matchParent)

            viewPager.adapter = ClassroomDividePagerAdapter(owner.childFragmentManager)

            tabLayout.setupWithViewPager(viewPager)

        }
    }
}