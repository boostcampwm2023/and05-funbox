package com.example.funbox.presentation.map

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MapViewModel : ViewModel(){
    private val _myMessage = MutableStateFlow("")
    val myMessage: StateFlow<String> = _myMessage
    private val locX : MutableStateFlow<Double?>  = MutableStateFlow(null)
    private val locY : MutableStateFlow<Double?>  = MutableStateFlow(null)

    fun setMessage(message: String){
        _myMessage.update {
            message
        }
    }
}