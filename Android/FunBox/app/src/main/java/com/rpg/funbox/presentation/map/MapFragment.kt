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
import com.rpg.funbox.data.OkHttpClientInstance
import com.rpg.funbox.databinding.FragmentMapBinding
import com.rpg.funbox.presentation.BaseFragment

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
import timber.log.Timber

import java.net.ServerSocket


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate


class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {

    private var isFabOpen = false

    private val viewModel: MapViewModel by activityViewModels()

    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationSource: FusedLocationSource
    private val LOCATION_PERMISSION_REQUEST_CODE = 5000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val requestMultiPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (!hasPermission()) {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                PERMISSIONS,
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
      
        socketConnect()
        
        collectLatestFlow(viewModel.mapUiEvent) { handleUiEvent(it) }

        binding.floatingActionButton.setOnClickListener {
            toggleFab()
        }

        // viewModel.mapApi()
        requestMultiPermissions.launch(AccessPermission.locationPermissionList)
        if (requireActivity().checkPermission(AccessPermission.locationPermissionList)) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        } else {
            requireActivity().finish()
        }

        Timer().scheduleAtFixedRate(0, 3000) {
            lifecycleScope.launch {
                val tmp = runBlocking {
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                }
                Timber.d("Now: ${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}, Locations: $tmp")
                viewModel.setUsersLocations(tmp.latitude, tmp.longitude)
                initMapView()
            }
        }

    }

    private fun socketConnect2(){
        //OkHttpClientInstance.okHttpClient

        //val stomp = StompClient(url, intervalMillis, client)

    }
    private fun socketConnect() {
        val socket = IO.socket("http://175.45.193.191:3000/socket")
        socket.connect()
        socket.on(io.socket.client.Socket.EVENT_CONNECT) {
            // 소켓 서버에 연결이 성공하면 호출됨
            Timber.i("Socket", "Connect")
        }.on(io.socket.client.Socket.EVENT_DISCONNECT) { args ->
            // 소켓 서버 연결이 끊어질 경우에 호출됨
            Timber.i("Socket", "Disconnet: ${args[0]}")
        }.on(EVENT_CONNECT_ERROR) { args ->
            // 소켓 서버 연결 시 오류가 발생할 경우에 호출됨
            if (args[0] is EngineIOException) {
                Timber.i("Socket", "Connect Error")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.buttonGone()
        isFabOpen = false
    }

    private fun initMapView() {
        val fm = childFragmentManager
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

    @UiThread
    override fun onMapReady(map: NaverMap) {
        this.naverMap = map.apply {
            locationSource = locationSource
            locationTrackingMode = LocationTrackingMode.Face
            uiSettings.isLocationButtonEnabled = true
            minZoom = 13.0
            maxZoom = 17.0
            uiSettings.isZoomControlEnabled = false
            extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
            addOnLocationChangeListener { location ->
                val cameraUpdate =
                    CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
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
                Timber.d("User: ${user.id}")
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
}