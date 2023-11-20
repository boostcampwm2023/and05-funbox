package com.example.funbox.presentation.game.gameselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class QuestionGameViewModel : ViewModel() {

    private val _spinnerEntry = MutableStateFlow(emptyList<Int>())
    val spinnerEntry : StateFlow<List<Int>?> = _spinnerEntry

    val quizQuestionCount = MutableStateFlow<Int>(0)

    fun setSpinnerEntry(entry: List<Int>) {
        _spinnerEntry.value = entry
    }
}