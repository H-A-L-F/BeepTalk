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
import com.example.beeptalk.models.ThreadCommentReply
import com.example.beeptalk.parcel.ThreadCommentID
import com.example.beeptalk.parcel.ThreadID
import com.google.firebase.firestore.FirebaseFirestore

class CommentDetailPage : AppCompatActivity(), RecyclerViewInterface {

    private lateinit var binding : ActivityCommentDetailPageBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var sp: SharedPreferences

    private lateinit var comments : ArrayList<ThreadComment>
    private lateinit var threadCommentRVAdapter: ThreadCommentRVAdapter

    private lateinit var currThread: ThreadID
    private lateinit var currComm: ThreadCommentID

    private var uid: String = "default"
    private var uname: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currThread = intent.getParcelableExtra("thread")!!
        currComm = intent.getParcelableExtra("comment")!!

       binding.apply {
            replyToTv.text = currComm.replyTo
            commentBodyTv.text = currComm.body
            totalVotesTv.text = (currComm.upvote.size - currComm.downvote.size).toString()
       }

        db = FirebaseFirestore.getInstance()

        sp = getSharedPreferences("current_user", Context.MODE_PRIVATE)
        uid = sp.getString("uid", "default")!!
        uname = sp.getString("username", "default")!!

        binding.btnPostComment.setOnClickListener {
            val body = binding.commentBodyEt.text.toString()
            val threadId = currThread.id
            val commId = currComm.id

            val threadCommentReply = ThreadCommentReply(threadId = threadId, commentId = commId, body = body, uid = uid)

            db.collection("threads").document(threadId)
                .collection("comments").document(commId)
                .collection("comments")
                .add(threadCommentReply).addOnSuccessListener {
                    binding.commentBodyEt.text.clear()

                    Toast.makeText(this, "Posted comment", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Comment failed", Toast.LENGTH_SHORT).show()
                }

            val notification = Notification(uid, uname, "Replied on your comment")

            db.collection("notifications").document(uid!!)
                .collection("activities").add(notification)
        }
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