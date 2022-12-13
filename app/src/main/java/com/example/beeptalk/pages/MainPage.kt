package com.example.beeptalk.pages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.beeptalk.R
import com.example.beeptalk.databinding.ActivityMainPageBinding
import com.example.beeptalk.fragments.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

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

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
//        FirebaseAuth.getInstance().signOut() // very important if you are using firebase.
//        val loginIntent = Intent(applicationContext, LoginPage::class.java)
//        loginIntent.flags =
//            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // clear previous task (optional)
//        startActivity(loginIntent)
//        val googleSignInClient = GoogleSignIn.getClient(this, gso)
//        googleSignInClient.signOut().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                finish()
//            }
//        }

        replaceFragment(homeFragment)

        binding.btmNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> replaceFragment(homeFragment)
                R.id.nav_thread -> replaceFragment(threadFragment)
                R.id.nav_add -> replaceFragment(addFragment)
                R.id.nav_notification -> replaceFragment(notificationFragment)
                R.id.nav_profile -> replaceFragment(profileFragment)
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