package com.example.beeptalk.lib

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.databinding.CardFollowNotificationBinding
import com.example.beeptalk.models.Notification

class NotificationFollowersRVAdapter(
    private val notifications: ArrayList<Notification>,
    private val recyclerViewInterface: RecyclerViewInterface
): RecyclerView.Adapter<NotificationFollowersRVAdapter.ViewHolder>() {

    class ViewHolder(val binding: CardFollowNotificationBinding, val recyclerViewInterface: RecyclerViewInterface): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if(bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(bindingAdapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return NotificationFollowersRVAdapter.ViewHolder(CardFollowNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
            ),recyclerViewInterface
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]

        holder.binding.apply {
            unameTv.text = notification.username
            descTv.text = notification.desc
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}