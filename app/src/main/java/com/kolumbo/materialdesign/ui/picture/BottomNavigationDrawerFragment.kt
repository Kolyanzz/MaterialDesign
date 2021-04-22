package com.kolumbo.materialdesign.ui.picture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.kolumbo.materialdesign.R

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {

    private lateinit var navigationView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_navigation_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationView = view.findViewById(R.id.navigation_view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_one -> Toast.makeText(
                    context, R.string.screen_1,
                    Toast.LENGTH_SHORT
                ).show()
                R.id.navigation_two -> Toast.makeText(
                    context, R.string.screen_2,
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }
    }
}