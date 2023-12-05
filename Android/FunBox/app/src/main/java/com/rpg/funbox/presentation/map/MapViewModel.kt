package com.rpg.funbox.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.data.dto.User
import com.rpg.funbox.data.dto.UserDetail
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.rpg.funbox.data.dto.UserLocation
import com.rpg.funbox.data.repository.UsersLocationRepository
import com.rpg.funbox.data.repository.UsersLocationRepositoryImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class MapViewModel : ViewModel() {

    private val usersLocationRepository: UsersLocationRepository = UsersLocationRepositoryImpl()

    private val _myMessage = MutableStateFlow("")
    val myMessage = _myMessage.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(listOf())
    val users = _users.asStateFlow()

    private val _usersLocations = MutableStateFlow<List<UserLocation>?>(null)
    val usersLocations = _usersLocations.asStateFlow()

    private val _myLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val myLocation = _myLocation.asStateFlow()

    private val _userDetail = MutableStateFlow(UserDetail(0, "", "", ""))
    val userDetail: StateFlow<UserDetail> = _userDetail

    private val _mapUiEvent = MutableSharedFlow<MapUiEvent>()
    val mapUiEvent = _mapUiEvent.asSharedFlow()

    private val _visibility = MutableStateFlow(false)
    val visibility: StateFlow<Boolean> = _visibility

    fun startMessageDialog() {
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.MessageOpen)
        }
    }

    fun setToggle() {
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.Toggle)
        }
    }

    fun toGame(){
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.ToGame)
        }
    }

    fun gameStart(){
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.GameStart)
        }
    }

    fun rejectGame(){
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.RejectGame)
        }
    }

    fun getGame() {
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.GetGame)
        }
    }

    fun toSetting() {
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.ToSetting)
        }
    }

    fun setXY(x: Double, y: Double) {
        _myLocation.value = Pair(x, y)
    }

    fun deleteUserMapPin() {
        _users.value.forEach { user ->
            Timber.d("${user.id}: ${user.mapPin.toString()}")
            user.mapPin?.map = null
        }
    }

    fun setUsersLocations(locX: Double, locY: Double) {
        viewModelScope.launch {
            Timber.d("유저 위치 불러옴")
            _usersLocations.value = usersLocationRepository.getUsersLocation(locX, locY)
            _usersLocations.value?.let { list ->
                val newUsers = mutableListOf<User>()
                list.forEach { location ->
                    if ((location.locX != null) && (location.locY != null)) {
                        newUsers.add(
                            User(
                                200,
                                location.id,
                                LatLng(location.locX, location.locY),
                                location.username,
                                false
                            )
                        )
                    } else {
                        newUsers.add(
                            User(
                                200,
                                location.id,
                                LatLng(37.6500000, 126.7800000),
                                location.username,
                                false
                            )
                        )
                    }
                }
                _users.value = newUsers
            }
        }
    }

    fun userDetailApi(id: Int, name: String) {
        _userDetail.update {
            UserDetail(
                id,
                "안녕하세요",
                "https://drive.google.com/file/d/1P6Va6qkB39gnE-gbfbPLCSxIFwAeM8Ul/view?usp=drive_link",
                name
            )
        }
    }

    fun buttonVisible() {
        _visibility.update { true }
    }

    fun buttonGone() {
        _visibility.update { false }
    }
}