package com.rpg.funbox.presentation.game.gameselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rpg.funbox.databinding.ItemGameListBinding
import com.rpg.funbox.databinding.ToBeDeterminedListBinding
import com.rpg.funbox.presentation.game.gameselect.GameSelectUiState.Companion.QUESTION_CARD_TYPE
import com.rpg.funbox.presentation.game.gameselect.GameSelectUiState.Companion.TO_BE_DETERMINED

class GameListAdapter(private val onGameClickListener: OnGameClickListener) :
    ListAdapter<GameSelectUiState, RecyclerView.ViewHolder>(gameSelectUiStateDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            QUESTION_CARD_TYPE -> {
                GameCardViewHolder(
                    ItemGameListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                ToBeDeterminedViewHolder(
                    ToBeDeterminedListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            QUESTION_CARD_TYPE -> {
                (holder as GameCardViewHolder).bind(
                    currentList[position] as GameSelectUiState.GameCard,
                    onGameClickListener
                )
            }
            TO_BE_DETERMINED -> {
                (holder as ToBeDeterminedViewHolder).bind(
                    currentList[position] as GameSelectUiState.ToBeDetermined
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            currentList[position] is GameSelectUiState.GameCard -> QUESTION_CARD_TYPE
            else -> TO_BE_DETERMINED
        }
    }

    companion object {
        val gameSelectUiStateDiffUtil = object : DiffUtil.ItemCallback<GameSelectUiState>() {
            override fun areItemsTheSame(
                oldItem: GameSelectUiState,
                newItem: GameSelectUiState
            ): Boolean = (oldItem.id == newItem.id)

            override fun areContentsTheSame(
                oldItem: GameSelectUiState,
                newItem: GameSelectUiState
            ): Boolean = (oldItem == newItem)
        }
    }

    class GameCardViewHolder(private val binding: ItemGameListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GameSelectUiState.GameCard, onGameClickListener: OnGameClickListener) {
            binding.gameCard = item
            binding.onClickListener = onGameClickListener
        }
    }

    class ToBeDeterminedViewHolder(private val binding: ToBeDeterminedListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GameSelectUiState.ToBeDetermined) {
            binding.tbd = item
        }
    }
}