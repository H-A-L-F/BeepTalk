package com.example.beeptalk.lib

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.databinding.CardCommentThreadBinding
import com.example.beeptalk.models.Thread
import com.example.beeptalk.models.ThreadComment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ThreadCommentRVAdapter(
    private var comments : ArrayList<ThreadComment>,
    private val recyclerViewInterface : RecyclerViewInterface,
    private val uname: String,
    private val uid: String
    ): RecyclerView.Adapter<ThreadCommentRVAdapter.ViewHolder>() {

    class ViewHolder(val binding: CardCommentThreadBinding, val recyclerViewInterface: RecyclerViewInterface): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if(bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(bindingAdapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardCommentThreadBinding.inflate(LayoutInflater.from(parent.context), parent, false), recyclerViewInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        holder.binding.apply {
            tvReply.text = comment.replyTo
            tvCommentBody.text = comment.body
            tvTotalVotes.text = comment.getTotalVotes().toString()
        }

        holder.binding.btnUpvote.setOnClickListener {
            if(comment.upvote.contains(uid)) return@setOnClickListener
            holder.binding.tvTotalVotes.text = comment.getTotalVotes().toString()
            var db = FirebaseFirestore.getInstance()
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.id!!)
                .update("upvote", FieldValue.arrayUnion(uid))
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.id!!)
                .update("downvote", FieldValue.arrayRemove(uid))
        }

        holder.binding.btnDownvote.setOnClickListener {
            if(comment.downvote.contains(uid)) return@setOnClickListener
            holder.binding.tvTotalVotes.text = comment.getTotalVotes().toString()
            var db = FirebaseFirestore.getInstance()
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.id!!)
                .update("downvote", FieldValue.arrayUnion(uid))
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.id!!)
                .update("upvote", FieldValue.arrayRemove(uid))
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    public fun setComments(comments : ArrayList<ThreadComment>) {
        this.comments = comments
    }
}