package com.dr.coursemate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dr.coursemate.utils.Utils.Companion.loadNewsAndCurrentAffairs
import com.dr.coursemate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationVisibilityControls()
        setUpBottomNavigation()

        backgroundDataLoading()

    }

    private fun backgroundDataLoading() {
        Handler(Looper.getMainLooper())
            .post { loadNewsAndCurrentAffairs(this) }
    }

    private fun setUpBottomNavigation() {
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun bottomNavigationVisibilityControls() {

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                R.id.myLibrary -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                R.id.theFeed -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                R.id.account -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }
    }

}