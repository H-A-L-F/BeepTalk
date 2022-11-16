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

class ThreadCommentRVAdapter(private var comments : ArrayList<ThreadComment>, private val recyclerViewInterface : RecyclerViewInterface): RecyclerView.Adapter<ThreadCommentRVAdapter.ViewHolder>() {

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
        holder.binding.tvTotalVotes.text = comment.getTotalVotes().toString()
        holder.binding.tvCommentBody.text = comment.body

        holder.binding.btnUpvote.setOnClickListener {
            comment.upvote++
            holder.binding.tvTotalVotes.text = comment.getTotalVotes().toString()
            var db = FirebaseFirestore.getInstance()
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.id!!).update("upvote", FieldValue.increment(1))
        }

        holder.binding.btnDownvote.setOnClickListener {
            comment.downvote++
            holder.binding.tvTotalVotes.text = comment.getTotalVotes().toString()
            var db = FirebaseFirestore.getInstance()
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.id!!).update("downvote", FieldValue.increment(1))
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    public fun setComments(comments : ArrayList<ThreadComment>) {
        this.comments = comments
    }
}