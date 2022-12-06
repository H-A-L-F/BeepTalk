package com.example.beeptalk.lib

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.databinding.CardThreadBinding
import java.util.*
import com.example.beeptalk.models.Thread
import com.example.beeptalk.pages.ThreadDetailPage
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList

class ThreadRVAdapter(private val threads : ArrayList<Thread>, private val recyclerViewInterface : RecyclerViewInterface): RecyclerView.Adapter<ThreadRVAdapter.ViewHolder>() {

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

        holder.binding.btnUpvote.setOnClickListener {
            when {
                thread.upDownFlag < 1 -> {
                    thread.upvote++
                    thread.upDownFlag++;
                    holder.binding.tvTotalVotes.text = thread.getTotalVotes().toString()
                    var db = FirebaseFirestore.getInstance()
                    db.collection("threads").document(thread.id!!).update("upvote", FieldValue.increment(1))
                }
            }
        }

        holder.binding.btnDownvote.setOnClickListener {
            when {
                thread.upDownFlag > -1 -> {
                    thread.downvote++
                    thread.upDownFlag--;
                    holder.binding.tvTotalVotes.text = thread.getTotalVotes().toString()
                    var db = FirebaseFirestore.getInstance()
                    db.collection("threads").document(thread.id!!).update("downvote", FieldValue.increment(1))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return threads.size
    }
}