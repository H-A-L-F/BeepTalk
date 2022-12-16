package com.example.beeptalk.pages

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeptalk.databinding.ActivityPostCommentPageBinding
import com.example.beeptalk.lib.PostCommentRVAdapter
import com.example.beeptalk.lib.RecyclerViewInterface
import com.example.beeptalk.lib.ThreadCommentRVAdapter
import com.example.beeptalk.models.PostComment
import com.example.beeptalk.models.ThreadComment
import com.example.beeptalk.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PostCommentPage : AppCompatActivity() {

    private lateinit var binding: ActivityPostCommentPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var postCommentRVAdapter: PostCommentRVAdapter
    private lateinit var postComments: ArrayList<PostComment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostCommentPageBinding.inflate(layoutInflater)
        super.setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        postComments = arrayListOf()
        val postId = intent.getStringExtra("postId")

        firebaseAuth.currentUser?.let {
            firebaseFirestore.collection("users").document(it.uid).addSnapshotListener { value, _ ->
                if (value != null) {
                    val user = value.toObject(User::class.java)
                    if (user != null) {
                        Picasso.get()
                            .load(user.profilePicture)
                            .into(binding.profilePicture)
                    };
                }
            }
        }

        postCommentRVAdapter = PostCommentRVAdapter(this, binding, postComments)
        binding.commentsRV.layoutManager = LinearLayoutManager(this)
        binding.commentsRV.setHasFixedSize(true)
        binding.commentsRV.adapter = postCommentRVAdapter

        binding.replyingToLayout.visibility = View.GONE

        binding.cancelReplyBtn.setOnClickListener {
            binding.usernameReplyToTV.text = ""
            binding.replyingToLayout.visibility = View.GONE
        }

        binding.commentBtn.setOnClickListener {

            val body = binding.commentET.text.toString()

            if(body.isNotEmpty()) {
                val threadComment = PostComment(postId = postId, body = body, userId = firebaseAuth.currentUser?.uid)

                if (postId != null) {
                    firebaseFirestore.collection("posts").document(postId)
                        .collection("comments")
                        .add(threadComment).addOnSuccessListener {
                            binding.commentET.text.clear()

                            Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Comment added failed", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
            }

        }

        if (postId != null) {
            getAllComments(postId)
        }

    }

    private fun getAllComments(postId: String) {
        firebaseFirestore.collection("posts").document(postId).collection("comments")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                querySnapshot?.let {
                    postComments.clear()
                    for (document in querySnapshot.documents) {
                        val curr = document.toObject(PostComment::class.java)
                        curr?.id = document.id.toString()
                        curr?.let { it1 -> postComments.add(it1) }
                    }
                    postCommentRVAdapter.notifyDataSetChanged()
                }
            }
    }

}