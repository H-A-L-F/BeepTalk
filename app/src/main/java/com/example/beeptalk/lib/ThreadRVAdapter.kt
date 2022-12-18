package com.example.beeptalk.lib

import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.R
import com.example.beeptalk.databinding.CardThreadBinding
import com.example.beeptalk.helper.getRelativeString
import com.example.beeptalk.models.Notification
import com.example.beeptalk.models.Thread
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ThreadRVAdapter(
    private val threads: ArrayList<Thread>,
    private val recyclerViewInterface: RecyclerViewInterface,
    private val recyclerViewEditInterface: RecyclerViewEditInterface,
    private val uname: String,
    private val uid: String
) : RecyclerView.Adapter<ThreadRVAdapter.ViewHolder>() {

    private lateinit var sharedPreferences: SharedPreferences

    class ViewHolder(
        val binding: CardThreadBinding,
        val recyclerViewInterface: RecyclerViewInterface,
        val recyclerViewEditInterface: RecyclerViewEditInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(bindingAdapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardThreadBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), recyclerViewInterface, recyclerViewEditInterface
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thread: Thread = threads[position]

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(thread.uid!!).get()
            .addOnSuccessListener {
                val data = it.data ?: return@addOnSuccessListener
                holder.binding.tvUsername.text = data["username"] as String
                Picasso.get().load(data["profilePicture"] as String)
                    .into(holder.binding.avUser)
            }

        holder.binding.btnDownvote.setImageResource(R.drawable.ic_downvote)
        holder.binding.btnUpvote.setImageResource(R.drawable.ic_upvote)

        if (thread.upvote.contains(uid)) {
            holder.binding.btnUpvote.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_up_24)
        }
        if (thread.downvote.contains(uid)) {
            holder.binding.btnDownvote.setImageResource(R.drawable.ic_baseline_keyboard_double_arrow_down_24)
        }

        db.collection("threads").document(thread.id!!).addSnapshotListener { snapshot, _ ->
            val currThread = snapshot?.toObject(Thread::class.java)
            if (currThread != null) {

                holder.binding.tvCreatedAt.text = getRelativeString(currThread.createdAt)
                holder.binding.tvThreadBody.text = currThread.body
                holder.binding.tvTotalVotes.text = (currThread.upvote.size - currThread.downvote.size).toString()
            }
        }

        if (thread.uid == uid) {
            holder.binding.btnEdit.visibility = View.VISIBLE
            holder.binding.btnEdit.setOnClickListener {
                if (holder.bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    recyclerViewEditInterface.onItemEdit(holder.bindingAdapterPosition)
                }
            }
        } else holder.binding.btnEdit.visibility = View.GONE

        holder.binding.btnUpvote.setOnClickListener {
            if (thread.upvote.contains(uid)) return@setOnClickListener
            db.collection("threads").document(thread.id!!)
                .update("upvote", FieldValue.arrayUnion(uid))
            db.collection("threads").document(thread.id!!)
                .update("downvote", FieldValue.arrayRemove(uid))

            val notification = Notification(thread.uid, uid, "upvoteThread")
            db.collection("users").document(thread.uid).collection("notifications")
                .add(notification)

            holder.binding.btnDownvote.setImageResource(R.drawable.ic_downvote)
        }

        holder.binding.btnDownvote.setOnClickListener {
            if (thread.downvote.contains(uid)) return@setOnClickListener
            db.collection("threads").document(thread.id!!)
                .update("downvote", FieldValue.arrayUnion(uid))
            db.collection("threads").document(thread.id!!)
                .update("upvote", FieldValue.arrayRemove(uid))

            holder.binding.btnUpvote.setImageResource(R.drawable.ic_upvote)
        }
    }

    override fun getItemCount(): Int {
        return threads.size
    }
}