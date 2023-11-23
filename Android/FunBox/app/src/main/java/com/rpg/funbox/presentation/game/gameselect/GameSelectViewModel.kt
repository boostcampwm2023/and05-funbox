package com.rpg.funbox.presentation.game.gameselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.presentation.game.gameselect.GameSelectUiState.Companion.QUESTION_CARD_TYPE
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameSelectViewModel : ViewModel(), OnGameClickListener {

    private val _gameSelectUiEvent = MutableSharedFlow<GameSelectUiEvent>()
    val gameSelectUiEvent = _gameSelectUiEvent.asSharedFlow()

    private val _gameList = MutableStateFlow<List<GameSelectUiState>>(listOf())
    val gameList = _gameList.asStateFlow()

    init {
        addGameContent()
    }

    override fun onClick(game: GameSelectUiState.GameCard) {
        viewModelScope.launch {
            when (game.gameType) {
                QUESTION_CARD_TYPE -> {
                    _gameSelectUiEvent.emit(GameSelectUiEvent.GameSelectSuccess)
                }
            }
        }
    }

    private fun addGameContent() {
        _gameList.value += getQuestionCard()
        _gameList.value += getToBeDetermined()
        viewModelScope.launch {
            _gameSelectUiEvent.emit(GameSelectUiEvent.GameListSubmit)
        }
    }

    private fun getQuestionCard(): GameSelectUiState.GameCard {
        return GameSelectUiState.GameCard(
            gameType = QUESTION_CARD_TYPE,
            questionCountList = listOf(1, 5, 10, 20)
        )
    }

    private fun getToBeDetermined(): GameSelectUiState.ToBeDetermined {
        return GameSelectUiState.ToBeDetermined()
    }
}