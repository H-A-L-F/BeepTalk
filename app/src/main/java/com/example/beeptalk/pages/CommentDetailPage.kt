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
import com.example.beeptalk.parcel.ThreadCommentID
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