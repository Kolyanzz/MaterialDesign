package com.kolumbo.materialdesign.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.kolumbo.materialdesign.databinding.ActivityMainBinding
import com.kolumbo.materialdesign.recyclerview.RecyclerViewActivity
import com.kolumbo.materialdesign.view_model.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val model: MainActivityViewModel by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(model.themeId)

        setContentView(binding.root)

        binding.viewPagerMain.adapter = ViewPagerAdapter(supportFragmentManager)
        binding.viewPagerMain.setCurrentItem(TODAY_FRAGMENT)

    }
}