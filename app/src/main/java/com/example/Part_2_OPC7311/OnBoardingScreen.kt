package com.example.Part_2_OPC7311

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.Part_2_OPC7311.adapter.OnBoardingViewPagerAdapter
import com.example.Part_2_OPC7311.model.OnBoardingData
import com.google.android.material.tabs.TabLayout

class OnBoardingScreen : AppCompatActivity() {
    var onBoardingViewPagerAdapter: OnBoardingViewPagerAdapter? = null
    var tabLayout: TabLayout? = null
    var onBoardingViewPager: ViewPager? = null
    var next: TextView? = null
    var position = 0
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_adapter)

        tabLayout = findViewById(R.id.tab_indicator)
        next = findViewById(R.id.next)

        // add data to model class

        val onBoardingData: MutableList<OnBoardingData> = ArrayList()
        onBoardingData.add(
            OnBoardingData(
                "Time_Ranger",
                "Time tracking application that makes it\n easy to keep projects and teams on track.",

                R.drawable.logo7
            )
        )
        /*
        onBoardingData.add(
            OnBoardingData(
                "Increase profitability",
                "Accurately track time and easily see where\n their money is going.",
                R.drawable.logo7
            )
        )
        onBoardingData.add(
            OnBoardingData(
                "Increase productivity",
                "Allows teams to stay on task with ongoing projects.\n See where any potential hiccups are in the day-to-day,\n and use that insight to create strategies to help your\n team be more productive in the workplace.",
                R.drawable.logo7
            )
        )

      onBoardingData.add(
            OnBoardingData(
                "Manage projects better",
                "Time tracking gives a line-of-sight to how long it\n takes your team to get tasks done.",
                R.drawable.logo7
            )
        )
*/
        setOnBoardingViewPagerAdapter(onBoardingData)
        position = onBoardingViewPager!!.currentItem

        //
        next?.setOnClickListener {
            if (position < onBoardingData.size) {
                position++
                onBoardingViewPager!!.currentItem = position
            }
            if (position == onBoardingData.size) {
                savePrefData()
                val i = Intent(applicationContext, MainActivity::class.java)
                startActivity(i)
            }
        }

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if (tab.position == onBoardingData.size - 1) {
                    next!!.text = "Get Started"
                } else {
                    next!!.text = "Next"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>) {
        onBoardingViewPager = findViewById(R.id.screemPager)
        onBoardingViewPagerAdapter = OnBoardingViewPagerAdapter(this, onBoardingData)
        onBoardingViewPager!!.adapter = onBoardingViewPagerAdapter

        // set tab layout
        tabLayout?.setupWithViewPager(onBoardingViewPager)

    }

    // add shared pref and store boolean (check if u are first time running app or not)
    private fun savePrefData() {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean("isFirstTimeRun", true)
        editor.apply()
    }

    private fun restorePrefData(): Boolean {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)

        return sharedPreferences!!.getBoolean("isFirstTimeRun", false)
    }

}