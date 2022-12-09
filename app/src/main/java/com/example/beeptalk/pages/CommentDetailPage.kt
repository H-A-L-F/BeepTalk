package com.example.beeptalk.pages

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeptalk.databinding.ActivityCommentDetailPageBinding
import com.example.beeptalk.databinding.ActivityThreadDetailPageBinding
import com.example.beeptalk.lib.RecyclerViewInterface
import com.example.beeptalk.lib.ThreadCommentRVAdapter
import com.example.beeptalk.models.Notification
import com.example.beeptalk.models.ThreadComment
import com.example.beeptalk.parcel.ThreadID
import com.google.firebase.firestore.FirebaseFirestore

class CommentDetailPage : AppCompatActivity(), RecyclerViewInterface {

    private lateinit var binding : ActivityCommentDetailPageBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var sp: SharedPreferences

    private lateinit var comments : ArrayList<ThreadComment>
    private lateinit var threadCommentRVAdapter: ThreadCommentRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val thread : ThreadID = intent.getParcelableExtra("thread")!!
        val comment: ThreadID = intent.getParcelableExtra("comment")!!

        binding.apply {
            tvCreatedAt.text = thread.createdAt.toString()
            tvCommentBody.text = thread.body
            tvTotalVotes.text = (thread.upvote.size - thread.downvote.size).toString()
        }

        db = FirebaseFirestore.getInstance()

        sp = getSharedPreferences("current_user", Context.MODE_PRIVATE)
        val uid = sp.getString("uid", "default")
        val uname = sp.getString("username", "default")

        binding.btnPostComment.setOnClickListener {
            val body = binding.etCommentBody.text.toString()
            val threadId = thread.id + "/comments/" + comment.id

            val threadComment = ThreadComment(threadId = threadId, body = body)

            db.collection("threads").document(thread.id).collection("comments").document(comment.id).collection("comments")
                .add(threadComment).addOnSuccessListener {
                    binding.etCommentBody.text.clear()

                    Toast.makeText(this, "Posted comment", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Comment failed", Toast.LENGTH_SHORT).show()
                }

            val notification = Notification(uid, uname, "Commented on your Comment")

            db.collection("notifications").document(uid!!)
                .collection("activities").add(notification)
        }

        binding.rvThreadComment.layoutManager = LinearLayoutManager(this)
        binding.rvThreadComment.setHasFixedSize(true)

        comments = arrayListOf()

        threadCommentRVAdapter = ThreadCommentRVAdapter(comments, this)

        binding.rvThreadComment.adapter = threadCommentRVAdapter

        getThreadComments(thread.id, comment.id)
    }

    private fun getThreadComments(threadId : String, commentId: String) {
        db.collection("threads").document(threadId).collection("comments").document(commentId).collection("comments")
            .get().addOnSuccessListener {
                for (document in it.documents) {
                    var curr = document.toObject(ThreadComment::class.java)
                    curr?.id = document.id.toString()
                    curr?.let { it1 -> comments.add(it1) }
                }
                threadCommentRVAdapter.setComments(comments)

                threadCommentRVAdapter.notifyDataSetChanged()
            }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}