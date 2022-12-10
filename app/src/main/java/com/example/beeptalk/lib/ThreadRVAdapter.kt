package com.example.beeptalk.lib

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.databinding.CardThreadBinding
import com.example.beeptalk.models.Notification
import java.util.*
import com.example.beeptalk.models.Thread
import com.example.beeptalk.pages.ThreadDetailPage
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList

class ThreadRVAdapter(
    private val threads : ArrayList<Thread>,
    private val recyclerViewInterface : RecyclerViewInterface,
    private val uname: String,
    private val uid: String
    ): RecyclerView.Adapter<ThreadRVAdapter.ViewHolder>() {

    private lateinit var sharedPreferences : SharedPreferences

    class ViewHolder(val binding: CardThreadBinding, val recyclerViewInterface: RecyclerViewInterface): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener(View.OnClickListener {

            })

            binding.root.setOnClickListener {
                if(bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(bindingAdapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardThreadBinding.inflate(LayoutInflater.from(parent.context), parent, false), recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thread : Thread = threads[position]
        holder.binding.tvCreatedAt.text = thread.createdAt.toString()
        holder.binding.tvThreadBody.text = thread.body
        holder.binding.tvTotalVotes.text = thread.getTotalVotes().toString()

        if(thread.uid == uid) holder.binding.btnEdit.visibility = View.VISIBLE
        else holder.binding.btnEdit.visibility = View.GONE

        holder.binding.btnUpvote.setOnClickListener {
            if(thread.upvote.contains(uid)) return@setOnClickListener
            thread.upvote.add(uid)
            thread.downvote.remove(uid)
            holder.binding.tvTotalVotes.text = thread.getTotalVotes().toString()
            val db = FirebaseFirestore.getInstance()
            db.collection("threads").document(thread.id!!).update("upvote", FieldValue.arrayUnion(uid))
            db.collection("threads").document(thread.id!!).update("downvote", FieldValue.arrayRemove(uid))

            val notification = Notification(uid, uname, "Upvoted your thread")

            db.collection("notifications").document(uid)
                .collection("activities").add(notification)
        }

        holder.binding.btnDownvote.setOnClickListener {
            if(thread.downvote.contains(uid)) return@setOnClickListener
            thread.upvote.remove(uid)
            thread.downvote.add(uid)
            holder.binding.tvTotalVotes.text = thread.getTotalVotes().toString()
            val db = FirebaseFirestore.getInstance()
            db.collection("threads").document(thread.id!!).update("downvote", FieldValue.arrayUnion(uid))
            db.collection("threads").document(thread.id!!).update("upvote", FieldValue.arrayRemove(uid))
        }
    }

    override fun getItemCount(): Int {
        return threads.size
    }
}