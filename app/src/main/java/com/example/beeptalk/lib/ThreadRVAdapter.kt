package com.example.beeptalk.lib

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.databinding.ActivityThreadPageBinding
import com.example.beeptalk.databinding.CardThreadBinding
import java.util.*
import com.example.beeptalk.models.Thread
import kotlin.collections.ArrayList

class ThreadRVAdapter(private val threads : ArrayList<Thread>): RecyclerView.Adapter<ThreadRVAdapter.ViewHolder>() {

    class ViewHolder(val binding: CardThreadBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardThreadBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thread : Thread = threads[position]
        holder.binding.tvCreatedAt.text = thread.createdAt.toString()
        holder.binding.tvThreadBody.text = thread.body
        holder.binding.tvTotalVotes.text = thread.getTotalVotes().toString()

        holder.binding.cardThread.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return threads.size
    }
}