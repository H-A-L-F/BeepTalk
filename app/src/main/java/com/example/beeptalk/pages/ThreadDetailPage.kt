package com.example.beeptalk.pages

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeptalk.databinding.ActivityThreadDetailPageBinding
import com.example.beeptalk.lib.RecyclerViewInterface
import com.example.beeptalk.lib.ThreadCommentRVAdapter
import com.example.beeptalk.lib.ThreadRVAdapter
import com.example.beeptalk.models.Notification
import com.example.beeptalk.models.Thread
import com.example.beeptalk.models.ThreadComment
import com.example.beeptalk.parcel.ThreadCommentID
import com.example.beeptalk.parcel.ThreadID
import com.google.firebase.firestore.FirebaseFirestore

class ThreadDetailPage : AppCompatActivity(), RecyclerViewInterface {

    private lateinit var binding: ActivityThreadDetailPageBinding
    private lateinit var comments : ArrayList<ThreadComment>
    private lateinit var threadCommentRVAdapter: ThreadCommentRVAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var sp: SharedPreferences

    private lateinit var thread: ThreadID
    private var uid: String = "default"
    private var uname: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThreadDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        thread = intent.getParcelableExtra("thread")!!

        binding.tvCreatedAt.text = thread.createdAt.toString()
        binding.tvThreadBody.text = thread.body
        binding.tvTotalVotes.text = (thread.upvote - thread.downvote).toString()

        db = FirebaseFirestore.getInstance()

        sp = getSharedPreferences("current_user", Context.MODE_PRIVATE)
        uid = sp.getString("uid", "default")!!
        uname = sp.getString("username", "default")!!

        binding.btnPostComment.setOnClickListener {
            val body = binding.etCommentBody.text.toString()
            val threadId = thread.id

            val threadComment = ThreadComment(threadId = threadId, body = body, uid = uid)

            db.collection("threads").document(thread.id)
                .collection("comments")
                .add(threadComment).addOnSuccessListener {
                    binding.etCommentBody.text.clear()

                    Toast.makeText(this, "Posted comment", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Comment failed", Toast.LENGTH_SHORT).show()
                }

            val notification = Notification(uid, uname, "Commented on your thread")

            db.collection("notifications").document(uid!!)
                .collection("activities").add(notification)
        }

        binding.rvThreadComment.layoutManager = LinearLayoutManager(this)
        binding.rvThreadComment.setHasFixedSize(true)

        comments = arrayListOf()

        threadCommentRVAdapter = ThreadCommentRVAdapter(comments, this, uname, uid)

        binding.rvThreadComment.adapter = threadCommentRVAdapter

        subscribeThreadComments(thread.id)
//        getThreadComments(thread.id)
    }

    private fun subscribeThreadComments(threadId : String) {
        db.collection("threads").document(threadId).collection("comments")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                querySnapshot?.let {
                    comments = arrayListOf()
                    for (document in querySnapshot.documents) {
                        var curr = document.toObject(ThreadComment::class.java)
                        curr?.id = document.id.toString()
                        curr?.let { it1 -> comments.add(it1) }
                    }
                    threadCommentRVAdapter.setComments(comments)

                    threadCommentRVAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun getThreadComments(threadId : String) {
        db.collection("threads").document(threadId).collection("comments")
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
        val curr = comments[position]
        val id = curr.id
        val threadId = thread.id
        val commUid = curr.uid
        val body =  curr.body
        val upvote = curr.upvote
        val downvote = curr.downvote
        val replyTo = curr.replyTo

        val commentItem: ThreadCommentID = ThreadCommentID(id!!, threadId, commUid!!, body!!, replyTo, upvote, downvote)

        intent = Intent(this, CommentDetailPage::class.java)
        intent.putExtra("thread", thread)
        intent.putExtra("comment", commentItem)
        startActivity(intent)
    }
}