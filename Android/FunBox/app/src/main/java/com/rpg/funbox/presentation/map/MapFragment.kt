package com.rpg.funbox.presentation.map

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentMapBinding
import com.rpg.funbox.presentation.BaseFragment
import io.socket.client.Socket
import com.rpg.funbox.presentation.game.GameActivity
import com.rpg.funbox.presentation.checkPermission
import com.rpg.funbox.presentation.login.AccessPermission
import io.socket.client.IO
import io.socket.client.Socket.EVENT_CONNECT_ERROR
import io.socket.engineio.client.EngineIOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate


class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {

    private var isFabOpen = false

    private val viewModel: MapViewModel by activityViewModels()
    lateinit var mSocket: Socket
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationSource: FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 5000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val requestMultiPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Timber.d("권한 허용1")
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                    submitUserLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Timber.d("권한 허용2")
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                    submitUserLocation()
                } else -> {
                    requireActivity().finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (!hasPermission()) {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                PERMISSIONS,
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        Timber.d("First X: ${locationSource.lastLocation?.latitude}, First Y: ${locationSource.lastLocation?.longitude}")
        requestMultiPermissions.launch(AccessPermission.locationPermissionList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
      
        socketConnect()
        
        collectLatestFlow(viewModel.mapUiEvent) { handleUiEvent(it) }

        binding.floatingActionButton.setOnClickListener {
            toggleFab()
        }

        viewModel.mapApi()
        submitUserLocation()
        initMapView()

    }

    private fun socketConnect() {
        mSocket = SocketApplication.get()
        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT) {
            // 소켓 서버에 연결이 성공하면 호출됨
            Log.d("Connect", "SOCKET CONNECT")
            applyGame()
        }.on(Socket.EVENT_DISCONNECT) { args ->
            // 소켓 서버 연결이 끊어질 경우에 호출됨
            Log.d("Connect", "SOCKET DISCONNECT")
        }.on(Socket.EVENT_CONNECT_ERROR) { args ->
            // 소켓 서버 연결 시 오류가 발생할 경우에 호출됨
            if (args[0] is EngineIOException) {
                Log.d("Disconect", "SOCKET ERROR")
            }
        }
            .on("gameApply"){
                val applyGameServerData  = Gson().fromJson(it[0].toString(), ApplyGameFromServerData::class.java)
                applyGameServerData.userId
                // 수락, 거절 다이얼로그 띄워야함.

                val accept = true
                // 수락시
                if (accept){
                    acceptGame(applyGameServerData.roomId)
                }
                else{
                    rejectGame(applyGameServerData.roomId)
                }


                Log.d("XXXXXXXXXXXXXXXGAMEAPPLY",applyGameServerData.toString())
            }
            .on("location"){
                Log.d("LOCATION",it[0].toString())
            }
            .on("gameApplyAnswer"){
                val json = Gson().fromJson(it[0].toString(), GameApplyAnswerFromServerData::class.java)
                Log.d("gameApplyAnswer",json.answer)
                when (json.answer){
                    "OFFLINE"->{
                        // 네트워크 연결끊김
                    }
                    "ACCEPT"->{
                        // 게임화면
                    }
                    "REJECT"->{
                        // 메인 화면
                    }
                }
            }
            .on("quiz"){
                val json = Gson().fromJson(it[0].toString(), QuizFromServer::class.java)
                Log.d("퀴즈",json.quiz)
                Log.d("타겟",json.target.toString())

                if (json.target != 35){//내id)
                    // UI에서 퀴즈 띄워주기
                }
                else{
                    // 답입력창 띄워주기
                    val answer = "답입니다."
                    sendQuizAnswer(json.roomId,answer)
                }
            }
            .on("quizAnswer"){
                val json = Gson().fromJson(it[0].toString(), QuizAnswerFromServer::class.java)
                Log.d("quizAnswer",json.answer)

                // UI에서 맞는지 체크
                val isCorrect = true
                verifyAnswer(json.roomId,isCorrect)

            }
            .on("score"){
                val json = Gson().fromJson(it[0].toString(), ScoreFromServer::class.java)
                Log.d("score",json.first().toString())
                Log.d("score",json.last().toString())
            }
            .on("lostConnection"){
                Log.d("lostConnection",it.toString())
            }
            .on("error"){
                Log.e("ERROR",it[0].toString())
            }



    }

    private fun verifyAnswer(roomId: String, isCorrect: Boolean) {
        val json = Gson().toJson(VerifyAnswerToServer(isCorrect,roomId))
        mSocket.emit("verifyAnswer",JSONObject(json))
    }

    private fun sendQuizAnswer(roomId: String, answer: String) {
        val json = Gson().toJson(QuizAnswerToServer(answer,roomId))
        mSocket.emit("quizAnswer",JSONObject(json))
    }

    private fun acceptGame(roomId: String) {
        val json = Gson().toJson(GameApplyAnswerToServerData(roomId,"ACCEPT"))
        mSocket.emit("gameApplyAnswer",JSONObject(json))
    }

    private fun rejectGame(roomId: String) {
        val json = Gson().toJson(GameApplyAnswerToServerData(roomId,"REJECT"))
        mSocket.emit("gameApplyAnswer",JSONObject(json))
    }

    private fun applyGame() {
        val json = Gson().toJson(ApplyGameToServerData(37))
        mSocket.emit("gameApply",JSONObject(json))
    }

    override fun onStart() {
        super.onStart()
        viewModel.buttonGone()
        isFabOpen = false
    }

    @UiThread
    override fun onMapReady(map: NaverMap) {
        this.naverMap = map.apply {
            locationSource = this@MapFragment.locationSource
            locationTrackingMode = LocationTrackingMode.Face
            uiSettings.isLocationButtonEnabled = true
            minZoom = 13.0
            maxZoom = 17.0
            uiSettings.isZoomControlEnabled = false
            extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
            addOnLocationChangeListener { location ->
                val cameraUpdate =
                    CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
                Timber.d("X: ${location.latitude}, Y :${location.longitude}")
                naverMap.moveCamera(cameraUpdate)
                viewModel.setXY(location.latitude, location.longitude)
                // viewModel.setUsersLocations(location.latitude, location.longitude)
            }
        }

        naverMap.locationOverlay.apply {
            isVisible = true
            iconHeight = 120
            iconWidth = 120
            icon = OverlayImage.fromResource(R.drawable.navi_icon)
        }
        val hasMsg = InfoWindow()
        hasMsg.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return "● ● ●"
            }
        }

        val infoWindow = InfoWindow()

        map.setOnMapClickListener { _, _ ->
            viewModel.buttonGone()
            viewModel.users.value.forEach {
                it.mapPin?.infoWindow?.close()
                if (it.isMsg) {
                    it.mapPin?.let { mapPin -> hasMsg.open(mapPin) }
                }
            }
        }

        viewModel.users.value.map { user ->
            var adapter = MapProfileAdapter(requireContext(), viewModel.userDetail.value, null)
            val marker = Marker().apply {
                // Timber.d("User: ${user.id}")
                position = user.loc
                iconTintColor = Color.YELLOW
                this.map = naverMap
                captionText = user.name.toString()
                captionTextSize = 20F
                if (user.isMsg) {
                    hasMsg.open(this)
                }
                setOnClickListener { _ ->
                    viewModel.userDetailApi(user.id)
                    infoWindow.adapter = adapter
                    runBlocking {
                        val test = viewModel.userDetail.value.profile
                        val image: Bitmap = try {
                            withContext(Dispatchers.IO) {
                                Glide.with(requireContext())
                                    .asBitmap()
                                    .load(test)
                                    .apply(RequestOptions().override(100, 100))
                                    .submit()
                                    .get()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.IO) {
                                Glide.with(requireContext())
                                    .asBitmap()
                                    .load(R.drawable.close_24)
                                    .apply(RequestOptions().override(100, 100))
                                    .submit()
                                    .get()
                            }
                        }

                        adapter =
                            MapProfileAdapter(requireContext(), viewModel.userDetail.value, image)
                    }


                    requireActivity().runOnUiThread {
                        Handler(Looper.getMainLooper()).postDelayed({
                            infoWindow.adapter = adapter
                            infoWindow.open(this)
                        }, 500)
                    }


                    if (!this.hasInfoWindow() || this.infoWindow == hasMsg) {
                        viewModel.buttonVisible()
                        viewModel.users.value.forEach {
                            if (it.isMsg) {
                                it.mapPin?.let { mapPin -> hasMsg.open(mapPin) }
                            }
                        }
                        infoWindow.open(this)
                    } else {
                        viewModel.buttonGone()
                        this.infoWindow?.close()
                        if (user.isMsg) {
                            hasMsg.open(this)
                        }
                    }

                    true
                }

            }

            user.mapPin = marker
        }
    }

    private fun submitUserLocation() {
        if (requireActivity().checkPermission(AccessPermission.locationPermissionList)) {
            Timer().scheduleAtFixedRate(0, 3000) {
                lifecycleScope.launch {
                    val tmp = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                    viewModel.setUsersLocations(tmp.latitude, tmp.longitude)
                    initMapView()
                }
            }
        }
    }

    private fun initMapView() {
        val fm = parentFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun hasPermission(): Boolean {
        for (permission in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun sendXYtoApi(latitude: Double, longitude: Double) {
        //LoadUserService.create().search(latitude,longitude).execute()
    }

    private fun toggleFab() {
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.floatingSetting, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.floatingWrite, "translationY", 0f).apply { start() }
            binding.floatingActionButton.setImageResource(R.drawable.add_24)
        } else {
            ObjectAnimator.ofFloat(binding.floatingSetting, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(binding.floatingWrite, "translationY", -400f).apply { start() }
            binding.floatingActionButton.setImageResource(R.drawable.close_24)
        }

        isFabOpen = !isFabOpen
    }

    private fun handleUiEvent(event: MapUiEvent) = when (event) {
        is MapUiEvent.MessageOpen -> {
            MessageDialog().show(parentFragmentManager, "messageDialog")
        }

        is MapUiEvent.ToGame -> {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra("otherId", viewModel.userDetail.value.id)
            startActivity(intent)
        }

        is MapUiEvent.GetGame -> {
            GetGameDialog().show(parentFragmentManager, "getGame")
        }

        is MapUiEvent.ToSetting -> {
            findNavController().navigate(R.id.action_mapFragment_to_settingFragment)
        }

        is MapUiEvent.Toggle -> {
            toggleFab()
        }
    }
}