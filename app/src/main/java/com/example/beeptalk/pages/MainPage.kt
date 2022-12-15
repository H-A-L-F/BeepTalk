package com.example.beeptalk.pages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.beeptalk.R
import com.example.beeptalk.databinding.ActivityMainPageBinding
import com.example.beeptalk.fragments.*
import com.example.beeptalk.helper.URIPathHelper
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

    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        super.setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()

//        val googleSignInClient = GoogleSignIn.getClient(this, gso)
//        googleSignInClient.signOut().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                FirebaseAuth.getInstance().signOut() // very important if you are using firebase.
//                val loginIntent = Intent(applicationContext, LoginPage::class.java)
//                loginIntent.flags =
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // clear previous task (optional)
//                startActivity(loginIntent)
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

    private fun openGalleryForVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            if (data?.data != null) {
                val uriPathHelper = URIPathHelper()
                val videoFullPath = uriPathHelper.getPath(this, data?.data!!) // Use this video path according to your logic
                // if you want to play video just after picking it to check is it working
                if (videoFullPath != null) {
                    playVideoInDevicePlayer(videoFullPath);
                }
            }
        }
    }

    fun playVideoInDevicePlayer(videoPath: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoPath))
        intent.setDataAndType(Uri.parse(videoPath), "video/mp4")
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_wrapper, fragment)
        fragmentTransaction.commit()
    }

}