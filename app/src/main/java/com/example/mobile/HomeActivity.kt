package com.example.mobile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mobile.Network.CallAPIJD
import com.example.mobile.Network.IServicesJD
import com.example.mobile.databinding.ActivityHomeBinding
import com.example.mobile.helper.HTTP
import com.example.mobile.helper.Singleton

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onBackPressed() {
        Toast.makeText(this, "Not permitted to go back", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_reservation, R.id.navigation_examen_selection
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemExit -> {
                AlertDialog.Builder(this)
                    .setMessage("Are you sure I want to logout?")
                    .setPositiveButton("Ok") { _, _ ->
                        run {
                            CallAPIJD.StartQuery("api/LoginHistories/${Singleton.User.Id}",
                                CallAPIJD.Companion.method.DELETE,
                                "",
                                object : IServicesJD {
                                    override fun Finish(response: String, status: Int) {
                                        this@HomeActivity.runOnUiThread {
                                            if (status == HTTP.OK) {
                                                Toast.makeText(
                                                    this@HomeActivity,
                                                    "logout cleaned",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }

                                    override fun Error(response: String, status: Int) {
                                        this@HomeActivity.runOnUiThread {
                                            Toast.makeText(
                                                this@HomeActivity,
                                                "action canceled",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }


                                    }
                                }
                            )
                            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                        }
                    }
                    .setNegativeButton("no") { _, _ ->
                        run {
                            Toast.makeText(this, "action canceled", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .create()
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }
}