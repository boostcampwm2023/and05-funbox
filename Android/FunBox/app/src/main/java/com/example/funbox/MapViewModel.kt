package com.example.funbox

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MapViewModel :ViewModel(){
    private val _myMessage = MutableStateFlow("")
    val myMessage: StateFlow<String> = _myMessage

    fun setMessage(message: String){
        _myMessage.update {
            message
        }
    }
}