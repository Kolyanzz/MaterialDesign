package com.kolumbo.materialdesign.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.kolumbo.materialdesign.model.ViewPagerData

private const val YESTERDAY_FRAGMENT = 0
const val TODAY_FRAGMENT = 1
private const val CALENDAR_FRAGMENT = 2

class ViewPagerAdapter(
    private val fragmentManager: FragmentManager,
    private val fragmentsData: List<Pair<String, Fragment>> = ViewPagerData().pairs
) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment = fragmentsData[position].second

    override fun getPageTitle(position: Int): CharSequence? {

        return fragmentsData[position].first

    }

    override fun getCount(): Int {
        return fragmentsData.size
    }
}