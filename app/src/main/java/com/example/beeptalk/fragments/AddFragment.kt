package com.example.beeptalk.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.beeptalk.R
import com.example.beeptalk.databinding.FragmentAddBinding
import com.example.beeptalk.models.Video
import com.example.beeptalk.pages.LoginPage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddFragment : Fragment() {

    private lateinit var binding : FragmentAddBinding
    private lateinit var videoView : VideoView
    private lateinit var caption : String

    // PICK VIDEO
    private val VIDEO_PICK_GALLERY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private val REQUEST_CODE = 1

    private lateinit var cameraPermissions: Array<String>
    private var videoUri: Uri? = null

//    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(layoutInflater, container, false)

        videoView = binding.videoV

        // INIT CAMERA PERMISSION
        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        progressBar = ProgressBar(context)
//        progressBar.
//        progressBar.setMessage("Uploading video...")
//        progressBar.setCanceledOnTouchOutside(false)

        binding.apply {
            postBtn.setOnClickListener {
                Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show()
                caption = videoTitleEt.text.toString().trim()
                if(TextUtils.isEmpty(caption)) Toast.makeText(context, "Please input title", Toast.LENGTH_SHORT).show()
                else if(videoUri == null) Toast.makeText(context, "Please choose a video    ", Toast.LENGTH_SHORT).show()
                else uploadVideoToFirebase()
            }

            chooseBtn.setOnClickListener {
                videoPickDialog()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun uploadVideoToFirebase() {
        Toast.makeText(context, "Start uploading", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(context, "Video uploaded", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
//                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setVideoToView() {
        val mediaController = MediaController(context)
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

        val builder = AlertDialog.Builder(context)
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
            requireActivity(),
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }

    private fun checkCameraPermissions(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val result2 = ContextCompat.checkSelfPermission(
            requireContext(),
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
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}