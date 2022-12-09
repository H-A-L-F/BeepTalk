package com.example.beeptalk.lib

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.databinding.CardLikeNotificationBinding
import com.example.beeptalk.databinding.CardThreadBinding
import com.example.beeptalk.models.Notification
import com.example.beeptalk.models.Thread
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class NotificationActivitiesRVAdapter(
    private val notifications: ArrayList<Notification>,
    private val recyclerViewInterface: RecyclerViewInterface
): RecyclerView.Adapter<NotificationActivitiesRVAdapter.ViewHolder>() {

    class ViewHolder(val binding: CardLikeNotificationBinding, val recyclerViewInterface: RecyclerViewInterface): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if(bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(bindingAdapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return NotificationActivitiesRVAdapter.ViewHolder(
            CardLikeNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), recyclerViewInterface
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification : Notification = notifications[position]
        holder.binding.apply {
            unameTv.text = notification.username
            descTv.text = notification.desc
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}