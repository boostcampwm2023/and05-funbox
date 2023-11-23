package com.rpg.funbox.presentation.game.gameselect

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QuestionGameViewModel : ViewModel() {

    private val _spinnerEntry = MutableStateFlow(emptyList<Int>())
    val spinnerEntry : StateFlow<List<Int>?> = _spinnerEntry

    val quizQuestionCount = MutableStateFlow<Int>(0)

    fun setSpinnerEntry(entry: List<Int>) {
        _spinnerEntry.value = entry
    }
}