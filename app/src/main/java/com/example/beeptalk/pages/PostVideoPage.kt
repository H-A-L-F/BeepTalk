package com.example.beeptalk.pages

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.beeptalk.databinding.ActivityPostVideoPageBinding
import com.example.beeptalk.models.Video
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PostVideoPage : AppCompatActivity() {

    private lateinit var binding : ActivityPostVideoPageBinding
    private lateinit var db : FirebaseFirestore

    private lateinit var videoView : VideoView
    private lateinit var caption : String

    // PICK VIDEO
    private val VIDEO_PICK_GALLERY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private val REQUEST_CODE = 1

    private lateinit var cameraPermissions: Array<String>
    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostVideoPageBinding.inflate(layoutInflater)
        super.setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        videoView = binding.videoV

        // INIT CAMERA PERMISSION
        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        binding.postBtn.setOnClickListener {
            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
            caption = binding.videoTitleEt.text.toString().trim()
            if(TextUtils.isEmpty(caption)) Toast.makeText(this, "Please input title", Toast.LENGTH_SHORT).show()
            else if(videoUri == null) Toast.makeText(this, "Please choose a video    ", Toast.LENGTH_SHORT).show()
            else uploadVideoToFirebase()
        }

        binding.chooseBtn.setOnClickListener {
            videoPickDialog()
        }
    }

    private fun uploadVideoToFirebase() {
        Toast.makeText(this, "Start uploading", Toast.LENGTH_SHORT).show()
//        progressBar.setVisibility(View.VISIBLE);

        val timestamp = "" + System.currentTimeMillis()
        val filePathAndName = "Videos/video_$timestamp"
        val storageRef = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageRef.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadUri = uriTask.result
                if(uriTask.isSuccessful) {
                    val video = Video(id = "$timestamp", uid = "test", caption, "$timestamp", "$downloadUri")

                    val colRef = FirebaseFirestore.getInstance().collection("videos").add(video)
                        .addOnSuccessListener {
//                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Video uploaded", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
//                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setVideoToView() {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(videoUri)
        videoView.requestFocus()
        videoView.setOnPreparedListener {
            videoView.pause()
        }
    }

    private fun videoPickDialog() {
        val options = arrayOf("Camera", "Gallery")

        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Pick video from")
            .setItems(options) {dialogInterface, i ->
                if(i == 0) {
                    if(!checkCameraPermissions()) requestCameraPermissions()
                    else videoPickCamera()
                } else {
                    videoPickGallery()
                }
            }
            .show()
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(
            this,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }

    private fun checkCameraPermissions(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val result2 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return result1 && result2
    }

    private fun videoPickGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose video"),
            VIDEO_PICK_GALLERY_CODE
        )
    }

    private fun videoPickCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if(grantResults.size > 0) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if(cameraAccepted && storageAccepted) videoPickCamera()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == AppCompatActivity.RESULT_OK) {
            if(requestCode == VIDEO_PICK_CAMERA_CODE) {
                videoUri == data!!.data
                setVideoToView()
            } else if(requestCode == VIDEO_PICK_GALLERY_CODE) {
                videoUri == data!!.data
                setVideoToView()
            }
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}