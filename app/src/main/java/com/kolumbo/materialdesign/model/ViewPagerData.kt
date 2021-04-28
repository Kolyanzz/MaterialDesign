package com.kolumbo.materialdesign.model

import androidx.fragment.app.Fragment
import com.kolumbo.materialdesign.view.CalendarPhotoFragment
import com.kolumbo.materialdesign.view.CurrentDayPhotoFragment
import com.kolumbo.materialdesign.view.YesterdayPhotoFragment

data class ViewPagerData(
    val pairs: List<Pair<String, Fragment>> = listOf(
        Pair("Вчера", YesterdayPhotoFragment.getInstance()),
        Pair("Сегодня", CurrentDayPhotoFragment.getInstance()),
        Pair("Выбрать день", CalendarPhotoFragment.getInstance())
    )
)