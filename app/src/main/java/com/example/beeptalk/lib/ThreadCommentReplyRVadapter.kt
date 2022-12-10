package com.example.beeptalk.lib

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.databinding.CardCommentThreadBinding
import com.example.beeptalk.models.ThreadComment
import com.example.beeptalk.models.ThreadCommentReply
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ThreadCommentReplyRVadapter(
    private val comments: ArrayList<ThreadCommentReply>,
    private val recyclerViewInterface : RecyclerViewInterface,
    private val uname: String,
    private val uid: String
): RecyclerView.Adapter<ThreadCommentReplyRVadapter.ViewHolder>() {

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
        return ThreadCommentReplyRVadapter.ViewHolder(
            CardCommentThreadBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            ), recyclerViewInterface
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        holder.binding.apply {
            tvReply.text = comment.replyTo
            tvTotalVotes.text = comment.getTotalVotes().toString()
            tvCommentBody.text = comment.body
        }

        holder.binding.btnUpvote.setOnClickListener {
            if(comment.upvote.contains(uid)) return@setOnClickListener
            var db = FirebaseFirestore.getInstance()
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.id!!)
                .collection("comments").document(comment.commentId!!)
                .update("upvote", FieldValue.increment(1))
            holder.binding.tvTotalVotes.text = comment.getTotalVotes().toString()
        }

        holder.binding.btnDownvote.setOnClickListener {
            if(comment.downvote.contains(uid)) return@setOnClickListener
            var db = FirebaseFirestore.getInstance()
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.id!!)
                .collection("comments").document(comment.commentId!!)
                .update("upvote", FieldValue.increment(1))
            holder.binding.tvTotalVotes.text = comment.getTotalVotes().toString()
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}