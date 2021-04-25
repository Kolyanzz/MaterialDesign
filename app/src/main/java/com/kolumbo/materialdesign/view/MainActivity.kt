package com.kolumbo.materialdesign.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kolumbo.materialdesign.R
import com.kolumbo.materialdesign.databinding.ActivityMainBinding
import com.kolumbo.materialdesign.view_model.MainActivityViewModel

class MainActivity : AppCompatActivity(), CurrentDayPhotoFragment.ThemeCallback {

    private lateinit var binding: ActivityMainBinding

    private val model: MainActivityViewModel by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(model.themeId)

        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, CurrentDayPhotoFragment.getInstance()).commitNow()

    }

    override fun setMyTheme(themeId: Int) {
        model.themeId = themeId
    }

}