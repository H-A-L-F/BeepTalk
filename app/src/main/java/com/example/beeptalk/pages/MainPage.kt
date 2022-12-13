package com.example.beeptalk.pages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.beeptalk.R
import com.example.beeptalk.databinding.ActivityMainPageBinding
import com.example.beeptalk.fragments.*

class MainPage : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding

    private val homeFragment = HomeFragment()
    private val threadFragment = ThreadFragment()
    private val addFragment = AddFragment()
    private val notificationFragment = NotificationFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        super.setContentView(binding.root)

        replaceFragment(homeFragment)

        binding.btmNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
//                    val fragmentManager = supportFragmentManager
//                    val fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.remove(addFragment)
//                    fragmentTransaction.remove(threadFragment)
//                    fragmentTransaction.remove(notificationFragment)
//                    fragmentTransaction.remove(profileFragment)
//                    fragmentTransaction.commit()
                    replaceFragment(homeFragment)
                }
                R.id.nav_thread -> {
//                    val fragmentManager = supportFragmentManager
//                    val fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.remove(addFragment)
//                    fragmentTransaction.remove(threadFragment)
//                    fragmentTransaction.remove(notificationFragment)
//                    fragmentTransaction.remove(profileFragment)
//                    fragmentTransaction.commit()
                    replaceFragment(threadFragment)
                }
                R.id.nav_add -> {
//                    val fragmentManager = supportFragmentManager
//                    val fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.remove(addFragment)
//                    fragmentTransaction.remove(threadFragment)
//                    fragmentTransaction.remove(notificationFragment)
//                    fragmentTransaction.remove(profileFragment)
//                    fragmentTransaction.commit()
                    replaceFragment(addFragment)
                }
                R.id.nav_notification -> {
//                    val fragmentManager = supportFragmentManager
//                    val fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.remove(addFragment)
//                    fragmentTransaction.remove(threadFragment)
//                    fragmentTransaction.remove(notificationFragment)
//                    fragmentTransaction.remove(profileFragment)
//                    fragmentTransaction.commit()
                    replaceFragment(notificationFragment)
                }
                R.id.nav_profile -> {
//                    val fragmentManager = supportFragmentManager
//                    val fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.remove(addFragment)
//                    fragmentTransaction.remove(threadFragment)
//                    fragmentTransaction.remove(notificationFragment)
//                    fragmentTransaction.remove(profileFragment)
//                    fragmentTransaction.commit()
                    replaceFragment(profileFragment)
                }
            }

            true
        }


    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_wrapper, fragment)
        fragmentTransaction.commit()
    }

}