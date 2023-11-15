package com.example.funbox.presentation.map

import androidx.lifecycle.ViewModel
<<<<<<< Updated upstream
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
=======


class MapViewModel : ViewModel() {

>>>>>>> Stashed changes
}