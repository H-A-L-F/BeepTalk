package com.example.beeptalk.lib

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.databinding.CardThreadBinding
import com.example.beeptalk.databinding.VideoContainerBinding
import com.example.beeptalk.models.Post

class PostRVAdapter(
    private val posts: ArrayList<Post>,
    private val recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<PostRVAdapter.ViewHolder>() {
    class ViewHolder(
        val binding: VideoContainerBinding,
        val recyclerViewInterface: RecyclerViewInterface
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
            VideoContainerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), recyclerViewInterface
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post : Post = posts[position]
        holder.binding.titleTV.text = post.title
        holder.binding.descTV.text = post.desc

//        val uri : Uri = Uri.parse(post.videoUrl)
//
//        val mediaController: MediaController = MediaController()
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}