package com.example.funbox.presentation.map

import androidx.lifecycle.ViewModel
import com.example.funbox.data.User
import com.example.funbox.data.UserDetail
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MapViewModel : ViewModel() {
    private val _myMessage = MutableStateFlow("")
    val myMessage: StateFlow<String> = _myMessage
    private val locX : MutableStateFlow<Double?>  = MutableStateFlow(null)
    private val locY : MutableStateFlow<Double?>  = MutableStateFlow(null)

    private val _users = MutableStateFlow(listOf<User>())
    val users: StateFlow<List<User>> = _users

    private val _userDetail= MutableStateFlow(UserDetail("","",""))
    val userDetail : StateFlow<UserDetail> = _userDetail

    fun setMessage(message: String) {
        _myMessage.update {
            message
        }
    }

    fun mapApi() {
        _users.update {
            listOf(
                User(
                    1,
                    LatLng(37.5670135, 126.9783740),
                    "A",
                    false

                ),

                User(
                    2,
                    LatLng(37.5600000, 126.9700000),
                    "B",
                    true
                )
            )
        }
    }

    fun userDetailApi(id:Int){
        _userDetail.update {
            UserDetail("안녕하세요","https://drive.google.com/file/d/1P6Va6qkB39gnE-gbfbPLCSxIFwAeM8Ul/view?usp=drive_link", "B")
        }
    }
}