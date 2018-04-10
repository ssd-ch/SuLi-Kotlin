package net.ssdtic.suli

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import io.realm.Realm


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Realm.init(this)
        Realm.setDefaultConfiguration(RealmManagement.config)

        setContentView(R.layout.activity_main)

        val viewPager = findViewById<NonSwipeableViewPager>(R.id.pager_main) as NonSwipeableViewPager

        viewPager.adapter = MainFragmentPagerAdapter(supportFragmentManager)
        val tabLayout = findViewById<TabLayout>(R.id.layout_main_tab) as TabLayout
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.setCurrentItem(tab.position, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        UpdateAllData.start({ println("MainActivity : complete") }, { println("MainActivity : error") })

    }
}
