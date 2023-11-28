package com.rpg.funbox.presentation.map

import android.graphics.ImageDecoder
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.data.dto.User
import com.rpg.funbox.data.dto.UserDetail
import com.rpg.funbox.presentation.login.nickname.NicknameUiEvent
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val _myMessage = MutableStateFlow("")
    val myMessage: StateFlow<String> = _myMessage
    private val locX : MutableStateFlow<Double?>  = MutableStateFlow(null)
    private val locY : MutableStateFlow<Double?>  = MutableStateFlow(null)

    private val _users = MutableStateFlow(listOf<User>())
    val users: StateFlow<List<User>> = _users

    private val _userDetail= MutableStateFlow(UserDetail(0,"","",""))
    val userDetail : StateFlow<UserDetail> = _userDetail

    private val _mapUiEvent = MutableSharedFlow<MapUiEvent>()
    val mapUiEvent = _mapUiEvent.asSharedFlow()

    private val _visibility= MutableStateFlow(false)
    val visibility : StateFlow<Boolean> = _visibility

    fun startMessageDialog() {
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.MessageOpen)
        }
    }

    fun toGame(){
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.ToGame)
        }
    }

    fun getGame(){
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.GetGame)
        }
    }

    fun toSetting(){
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.ToSetting)
        }
    }

    fun setXY(x:Double,y:Double){
        locX.update {
            x
        }
        locY.update {
            y
        }
    }

    fun mapApi() {
        _users.update {
            listOf(
                User(
                    200,
                    1,
                    LatLng(37.5670135, 126.9783740),
                    "A",
                    false

                ),

                User(
                    200,
                    2,
                    LatLng(37.5600000, 126.9700000),
                    "B",
                    true
                ),

                User(
                    200,
                    3,
                    LatLng(37.2435914,127.0730043),
                    "C",
                    true
                )
            )
        }
    }

    fun userDetailApi(id:Int){
        _userDetail.update {
            UserDetail( id,"안녕하세요","https://drive.google.com/file/d/1P6Va6qkB39gnE-gbfbPLCSxIFwAeM8Ul/view?usp=drive_link", "B")
        }
    }

    fun buttonVisible(){
        _visibility.update { true }
    }

    fun buttonGone(){
        _visibility.update { false }
    }
}