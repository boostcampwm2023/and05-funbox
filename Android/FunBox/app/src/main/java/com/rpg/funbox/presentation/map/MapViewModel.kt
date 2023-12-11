package com.rpg.funbox.presentation.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.rpg.funbox.data.dto.User
import com.rpg.funbox.data.dto.UserDetail
import com.rpg.funbox.data.dto.UserInfoResponse
import com.rpg.funbox.data.dto.UserLocation
import com.rpg.funbox.data.repository.UserRepository
import com.rpg.funbox.data.repository.UserRepositoryImpl
import com.rpg.funbox.data.repository.UsersLocationRepository
import com.rpg.funbox.data.repository.UsersLocationRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URL

class MapViewModel : ViewModel() {

    private val usersLocationRepository: UsersLocationRepository = UsersLocationRepositoryImpl()
    private val userRepository: UserRepository = UserRepositoryImpl()

    private val _myMessage = MutableStateFlow("")
    val myMessage = _myMessage

    private val _otherUser = MutableStateFlow<UserInfoResponse?>(null)
    val otherUser = _otherUser.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(listOf())
    val users = _users.asStateFlow()

    private val _usersUpdate = MutableStateFlow<Boolean>(false)
    val usersUpdate = _usersUpdate.asStateFlow()

    private val _usersLocations = MutableStateFlow<List<UserLocation>?>(null)
    val usersLocations = _usersLocations.asStateFlow()

    private val _myLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val myLocation = _myLocation.asStateFlow()

    private val _userDetail = MutableStateFlow<UserDetail?>(null)
    val userDetail: StateFlow<UserDetail?> = _userDetail

    private val _mapUiEvent = MutableSharedFlow<MapUiEvent>()
    val mapUiEvent = _mapUiEvent.asSharedFlow()

    private val _visibility = MutableStateFlow(false)
    val visibility: StateFlow<Boolean> = _visibility

    private val _applyGameFromServerData = MutableStateFlow<ApplyGameFromServerData?>(null)
    val applyGameFromServerData = _applyGameFromServerData.asStateFlow()

    private val _userDetailTable = MutableStateFlow<HashMap<Int, UserDetail>>(hashMapOf())
    val userDetailTable = _userDetailTable.asStateFlow()

    private val _userProfileTable = MutableStateFlow<HashMap<Int, Bitmap?>>(hashMapOf())
    val userProfileTable = _userProfileTable.asStateFlow()

    fun setLocationPermitted() {
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.LocationPermitted)
        }
    }

    fun setOtherUser(userId: Int) {
        viewModelScope.launch {
            userRepository.getSpecificUserInfo(userId = userId)?.let { specificUserInfo ->
                _otherUser.value = specificUserInfo
            }
        }
    }

    fun setApplyGameData(applyGameFromServerData: ApplyGameFromServerData) {
        _applyGameFromServerData.value = applyGameFromServerData
    }

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

    fun toGame() {
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.ToGame)
        }
    }

    fun gameStart() {
        viewModelScope.launch {
            _mapUiEvent.emit(MapUiEvent.GameStart)
        }
    }

    fun rejectGame() {
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

    fun setUsersLocations(locX: Double, locY: Double) {
        viewModelScope.launch {
            Timber.d("유저 위치 불러옴")
            _usersLocations.value =
                usersLocationRepository.getUsersLocation(locX, locY).userLocations
            _usersLocations.value?.let { list ->
                val idList = list.map { it.id }
                val newUsers = _users.value.filter { user ->
                    user.id in idList
                }.toMutableList()
                deleteOldPlayer(idList)
                list.forEach { location ->
                    if ((location.locX != null) && (location.locY != null)) {
                        newUsers.find { it.id == location.id }?.let {
                            val idx = newUsers.indexOf(it)
                            newUsers[idx].loc = LatLng(location.locX, location.locY)
                            Timber.d("User ID: ${location.id}")
                        } ?: run {
                            userDetailApi(location.id)
                            newUsers.add(
                                User(
                                    200,
                                    location.id,
                                    LatLng(location.locX, location.locY),
                                    location.username,
                                    location.isMsgInAnHour,
                                )
                            )
                        }

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
                //newUsers.forEach {Timber.d( it.mapPin?.infoWindow.toString()) }

                _users.update { newUsers.toList() }
                _usersUpdate.update { !it }
            }
        }
    }

    private fun deleteOldPlayer(idList: List<Int>) {
        users.value.forEach { user ->
            if (user.id !in idList) {
                user.mapPin?.map = null
                user.mapPin = null
            }
        }
    }

    fun userDetailApi(id: Int) {
        viewModelScope.launch {
            val response = userRepository.getSpecificUserInfo(id)
            if (id !in _userDetailTable.value) {
//                Log.d("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",response!!.profileUrl.toString())
                response?.let { res ->
                    _userDetailTable.value.set(
                        id, UserDetail(
                            id,
                            res.message.toString(),
                            res.profileUrl.toString(),
                            res.userName.toString()
                        )
                    )
                    responseProfile(res.profileUrl, id)
                }

            } else {
                _userDetailTable.value[id]?.let {
                    response?.let{res->
                        if(it.msg != res.message){
                            it.msg = res.message?: ""
                        }

                        if(it.profile != res.profileUrl){
                            responseProfile(res.profileUrl,id)
                        }
                    }
                }
            }
        }
    }

    private fun responseProfile(profileUrl: String?, id: Int) {
        profileUrl?.let {
            viewModelScope.launch {
                var bitmap: Bitmap
                runBlocking {
                    val url =
                        URL("https://kr.object.ncloudstorage.com/funbox-profiles/" + profileUrl)
                    bitmap = withContext(Dispatchers.IO) {
                        BitmapFactory.decodeStream(
                            url.openConnection().getInputStream()
                        )
                    }
                }
                _userProfileTable.value[id] = bitmap
            }
        } ?: run {
            _userProfileTable.value[id] = null
        }
    }

    fun buttonVisible() {
        _visibility.update { true }
    }

    fun buttonGone() {
        _visibility.update { false }
    }

    fun submitMessage() {
        viewModelScope.launch {
            if (userRepository.patchUserMessage(message = _myMessage.value)) {
                _mapUiEvent.emit(MapUiEvent.MessageSubmit)
                _myMessage.value = ""
            } else {
                _mapUiEvent.emit(MapUiEvent.NetworkErrorEvent())
            }
        }
    }

    fun getDetail(id: Int): UserDetail? {
        return _userDetailTable.value.get(id)
    }

    fun getProfile(id: Int): Bitmap? {
        return _userProfileTable.value.get(id)
    }
}