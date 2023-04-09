package com.integrationTestExample


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.integrationTestExample.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

/***
 * Only activity class used in the application
 * botton navigation bar is controlled the other screens from here
 * android jetpack nav safe args used for navigation
 *  and all the fragment communications is from nav controller actions.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * Android 12 splash api is is used location themes v31
         */
        installSplashScreen()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // hiding the action bar
        this.supportActionBar!!.hide()
        //initializing bottom navigation view
        val navView: BottomNavigationView = binding.navView
        //nav controller initialization
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        //fragments adding to bottom
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_interaction,
                R.id.navigation_scan,
                R.id.navigation_edit,
                R.id.navigation_search
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // bottom icon click listener for re creating the fragments
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_home)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_interaction_home -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_interaction_home)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_scan -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_scan)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_search -> {

                    navController.popBackStack()
                    navController.navigate(R.id.navigation_search)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_edit -> {
                    navController.popBackStack()
                    navController.navigate(R.id.navigation_edit)
                    return@setOnItemSelectedListener true
                }

            }
            false
        }
    }

    override fun onSupportNavigateUp() =
        findNavController(R.id.nav_host_fragment_activity_main).navigateUp()

}