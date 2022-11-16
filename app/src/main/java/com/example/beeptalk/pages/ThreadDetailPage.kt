package com.example.beeptalk.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.beeptalk.databinding.ActivityThreadDetailPageBinding
import com.example.beeptalk.models.Thread
import com.example.beeptalk.models.ThreadComment
import com.example.beeptalk.parcel.ThreadID
import com.google.firebase.firestore.FirebaseFirestore

class ThreadDetailPage : AppCompatActivity() {

    private lateinit var binding: ActivityThreadDetailPageBinding
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThreadDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val thread : ThreadID = intent.getParcelableExtra("thread")!!

        binding.tvCreatedAt.text = thread.createdAt.toString()
        binding.tvThreadBody.text = thread.body
        binding.tvTotalVotes.text = (thread.upvote - thread.downvote).toString()

        db = FirebaseFirestore.getInstance()

        binding.btnPostComment.setOnClickListener {
            val body = binding.etCommentBody.text.toString()
            val threadId = thread.id

            val threadComment = ThreadComment(threadId = threadId, body = body)

            db.collection("threads").document(thread.id).collection("comments")
                .add(threadComment).addOnSuccessListener {
                    binding.etCommentBody.text.clear()

                    Toast.makeText(this, "Posted comment", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Comment failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}