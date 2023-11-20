package com.example.funbox.presentation.game.gameselect

import java.util.UUID

sealed class GameSelectUiState(val id: String = UUID.randomUUID().toString()) {

    data class GameCard(
        val gameType: Int,
        val questionCountList: List<Int> = emptyList()
    ) : GameSelectUiState()

    data class ToBeDetermined(
        val gameType: Int = TO_BE_DETERMINED
    ) : GameSelectUiState()

    companion object {
        const val TO_BE_DETERMINED = 0
        const val QUESTION_CARD_TYPE = 1
        const val OTHER_GAMES = 2
    }
}