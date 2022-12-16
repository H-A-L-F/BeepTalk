package com.example.beeptalk.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.beeptalk.databinding.FragmentAddBinding
import com.example.beeptalk.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var videoView: VideoView
    private lateinit var caption: String

    private var videoUri: Uri? = null

//    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBinding.inflate(layoutInflater, container, false)

        videoView = binding.videoV

        // INIT CAMERA PERMISSION
//        cameraPermissions = arrayOf(
//            android.Manifest.permission.CAMERA,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//        progressBar = ProgressBar(context)
//        progressBar.
//        progressBar.setMessage("Uploading video...")
//        progressBar.setCanceledOnTouchOutside(false)

        binding.apply {
            postBtn.setOnClickListener {
                caption = videoTitleEt.text.toString()
                if (caption.isEmpty()) Toast.makeText(
                    context,
                    "Please input title",
                    Toast.LENGTH_SHORT
                ).show()
                else if (videoUri == null) Toast.makeText(
                    context,
                    "Please choose a video",
                    Toast.LENGTH_SHORT
                ).show()
                else uploadVideoToFirebase()
            }

            chooseBtn.setOnClickListener {
                pickVideo()
            }

        }

        return binding.root
    }

    private fun uploadVideoToFirebase(
    ) {
        Toast.makeText(context, "Start uploading", Toast.LENGTH_SHORT).show()
//        progressBar.setVisibility(View.VISIBLE);

        val timestamp = "" + System.currentTimeMillis()
        val filePathAndName = "videos/video_$timestamp"
        val storageRef = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageRef.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { videoUrl ->
                    val post = Post(
                        videoUrl = videoUrl.toString(),
                        userId = FirebaseAuth.getInstance().currentUser?.uid,
                        caption = caption
                    )
                    FirebaseFirestore.getInstance().collection("posts").add(post).addOnSuccessListener {
                        Toast.makeText(context, "Post created!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pickVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        pickVideoFromGallery.launch(intent)
    }

    private var pickVideoFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                videoUri = result.data!!.data
                val mediaController = MediaController(context)
                mediaController.setAnchorView(videoView)
                videoView.setMediaController(mediaController)
                videoView.setVideoURI(result.data!!.data)
                videoView.requestFocus()
                videoView.setOnPreparedListener {
                    videoView.start()
                }

                videoView.setOnCompletionListener {
                    videoView.start()
                }

            }
        }

}