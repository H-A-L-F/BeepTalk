package com.example.beeptalk.pages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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

    // PICK VIDEO
    private val VIDEO_PICK_GALLERY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private val REQUEST_CODE = 1

    private lateinit var cameraPermissions: Array<String>
    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainPageBinding.inflate(layoutInflater)
        super.setContentView(binding.root)

        replaceFragment(homeFragment)

        binding.btmNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    replaceFragment(homeFragment)
                }
                R.id.nav_thread -> {
                    replaceFragment(threadFragment)
                }
                R.id.nav_add -> {
                    replaceFragment(addFragment)
                }
                R.id.nav_notification -> {
                    replaceFragment(notificationFragment)
                }
                R.id.nav_profile -> {
                    replaceFragment(profileFragment)
                }
            }
            true
        }

            // INIT CAMERA PERMISSION
            cameraPermissions = arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            replaceFragment(homeFragment)

        }

//    private fun setVideoToView() {
//        val mediaController = MediaController(this)
//        mediaController.setAnchorView(videoView)
//        videoView.setMediaController(mediaController)
//        videoView.setVideoURI(videoUri)
//        videoView.requestFocus()
//        videoView.setOnPreparedListener {
//            videoView.pause()
//        }
//    }
//
//    private fun videoPickDialog() {
//        val options = arrayOf("Camera", "Gallery")
//
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Pick video from")
//            .setItems(options) {dialogInterface, i ->
//                if(i == 0) {
//                    if(!checkCameraPermissions()) requestCameraPermissions()
//                    else videoPickCamera()
//                } else {
//                    videoPickGallery()
//                }
//            }
//            .show()
//    }
//
//    private fun requestCameraPermissions() {
//        ActivityCompat.requestPermissions(
//            this,
//            cameraPermissions,
//            CAMERA_REQUEST_CODE
//        )
//    }
//
//    private fun checkCameraPermissions(): Boolean {
//        val result1 = ContextCompat.checkSelfPermission(
//            this,
//            android.Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//
//        val result2 = ContextCompat.checkSelfPermission(
//            this,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//
//        return result1 && result2
//    }
//
//    private fun videoPickGallery() {
//        val intent = Intent()
//        intent.type = "video/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//
//        startActivityForResult(
//            Intent.createChooser(intent, "Choose video"),
//            VIDEO_PICK_GALLERY_CODE
//        )
//    }
//
//    private fun videoPickCamera() {
//        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when(requestCode) {
//            CAMERA_REQUEST_CODE -> {
//                if(grantResults.size > 0) {
//                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
//                    if(cameraAccepted && storageAccepted) videoPickCamera()
//                } else {
//                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if(resultCode == RESULT_OK) {
//            if(requestCode == VIDEO_PICK_CAMERA_CODE) {
//                videoUri == data!!.data
//                setVideoToView()
//            } else if(requestCode == VIDEO_PICK_GALLERY_CODE) {
//                videoUri == data!!.data
//                setVideoToView()
//            }
//        } else {
//            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

//    private fun openGalleryForVideo() {
//        val intent = Intent()
//        intent.type = "video/*"
//        intent.action = Intent.ACTION_PICK
//        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
//            if (data?.data != null) {
//                val uriPathHelper = URIPathHelper()
//                val videoFullPath = uriPathHelper.getPath(this, data?.data!!) // Use this video path according to your logic
//                // if you want to play video just after picking it to check is it working
//                if (videoFullPath != null) {
//                    playVideoInDevicePlayer(videoFullPath);
//                }
//            }
//        }
//    }
//
//    fun playVideoInDevicePlayer(videoPath: String) {
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoPath))
//        intent.setDataAndType(Uri.parse(videoPath), "video/mp4")
//        startActivity(intent)
//    }



    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_wrapper, fragment)
        fragmentTransaction.commit()
    }

}