package com.rpg.funbox.presentation.game.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.rpg.funbox.databinding.ItemChatBinding
import com.rpg.funbox.databinding.ItemChatOtherBinding

class ChatAdapter : ListAdapter<MessageItem, ViewHolder>(diffUtil) {
    class ChatViewHolder(private val binding: ItemChatBinding) :
        ViewHolder(binding.root) {
        fun bind(item: MessageItem) {
            binding.tvChatText.text = item.message
        }
    }

    class OtherChatViewHolder(private val binding: ItemChatOtherBinding) :
        ViewHolder(binding.root) {
        fun bind(item: MessageItem) {
            binding.tvChatText.text = item.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MY_CHAT) {
            ChatViewHolder(
                ItemChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            OtherChatViewHolder(
                ItemChatOtherBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == MY_CHAT) {
            (holder as ChatViewHolder).bind(currentList[position])
        } else {
            (holder as OtherChatViewHolder).bind(currentList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].type == 0) {
            MY_CHAT
        } else {
            OTHER_CHAT
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MessageItem>() {
            override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
                return false
            }
        }


        private const val MY_CHAT = 0
        private const val OTHER_CHAT = 1
    }

}