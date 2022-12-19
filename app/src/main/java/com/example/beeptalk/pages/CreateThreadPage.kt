package com.example.beeptalk.pages

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.beeptalk.databinding.ActivityCreateThreadPageBinding
import com.example.beeptalk.models.Post
import com.example.beeptalk.models.Thread
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateThreadPage : AppCompatActivity() {
    private lateinit var binding: ActivityCreateThreadPageBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateThreadPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getStringExtra("postId")
        db = FirebaseFirestore.getInstance()

        if (postId == null) {
            binding.postView.visibility = View.GONE
        } else {
            binding.postView.visibility = View.VISIBLE
            db.collection("posts").document(postId).addSnapshotListener {
                snapshot, _ ->
                val curr = snapshot?.toObject(Post::class.java)

                if(curr != null) {
                    val videoUrl = curr.videoUrl
                    Glide.with(this)
                        .asBitmap()
                        .load(videoUrl)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                            ) {
                                binding.postView.setImageBitmap(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })
                }
            }
        }

        binding.btnPost.setOnClickListener {
            val threadBody = binding.etThreadBody.text.toString()


            if (threadBody.isNotEmpty()) {
                val thread =
                    Thread(
                        body = threadBody,
                        uid = FirebaseAuth.getInstance().currentUser?.uid,
                        stitch = postId
                    )


                db.collection("threads").add(thread).addOnSuccessListener {
                    binding.etThreadBody.text.clear()
                    Toast.makeText(this, "Posted thread", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Post failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
            }


        }

    }
}