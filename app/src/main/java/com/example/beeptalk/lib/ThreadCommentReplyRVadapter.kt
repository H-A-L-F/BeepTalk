package com.example.beeptalk.lib

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.databinding.CardCommentThreadBinding
import com.example.beeptalk.models.ThreadComment
import com.example.beeptalk.models.ThreadCommentReply
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ThreadCommentReplyRVadapter(
    private var comments: ArrayList<ThreadCommentReply>,
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

        val dateFormat = SimpleDateFormat("MMM dd yyyy", Locale.US)
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(comment.uid!!).get()
            .addOnSuccessListener {
                val data = it.data ?: return@addOnSuccessListener
                holder.binding.tvUsername.text = data["username"] as String
                Picasso.get().load(data["profilePicture"] as String)
                    .into(holder.binding.avUser)
            }

        db.collection("users").document(comment.replyTo).get()
            .addOnSuccessListener {
                val data = it.data ?: return@addOnSuccessListener
                holder.binding.tvReply.text = "Reply to @" + data["username"] as String
            }

        db.collection("threads").document(comment.threadId!!)
            .collection("comments").document(comment.commentId!!)
            .collection("comments").document(comment.id!!).get()
            .addOnSuccessListener {
                val data = it.data ?: return@addOnSuccessListener
                holder.binding.tvCommentBody.text = data["body"] as String
                val up = data["upvote"] as List<*>
                val down = data["downvote"] as List<*>
                holder.binding.tvTotalVotes.text = (up.size - down.size).toString()
            }

//        holder.binding.apply {
//            tvReply.text = comment.replyTo
//            tvTotalVotes.text = comment.getTotalVotes().toString()
//            tvCommentBody.text = comment.body
//        }

        holder.binding.btnUpvote.setOnClickListener {
            if(comment.upvote.contains(uid)) return@setOnClickListener
            holder.binding.tvTotalVotes.text = comment.getTotalVotes().toString()
            var db = FirebaseFirestore.getInstance()
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.commentId!!)
                .collection("comments").document(comment.id!!)
                .update("upvote", FieldValue.arrayUnion(uid))
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.commentId!!)
                .collection("comments").document(comment.id!!)
                .update("downvote", FieldValue.arrayRemove(uid))
        }

        holder.binding.btnDownvote.setOnClickListener {
            if(comment.downvote.contains(uid)) return@setOnClickListener
            holder.binding.tvTotalVotes.text = comment.getTotalVotes().toString()
            var db = FirebaseFirestore.getInstance()
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.commentId!!)
                .collection("comments").document(comment.id!!)
                .update("upvote", FieldValue.arrayRemove(uid))
            db.collection("threads").document(comment.threadId!!)
                .collection("comments").document(comment.commentId!!)
                .collection("comments").document(comment.id!!)
                .update("downvote", FieldValue.arrayUnion(uid))
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    public fun setComments(comments : ArrayList<ThreadCommentReply>) {
        this.comments = comments
    }
}