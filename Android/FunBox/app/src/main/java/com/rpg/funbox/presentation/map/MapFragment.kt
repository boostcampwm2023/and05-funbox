package com.rpg.funbox.presentation.map

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
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
import com.rpg.funbox.data.dto.User
import com.rpg.funbox.databinding.FragmentMapBinding
import com.rpg.funbox.presentation.BaseFragment
import com.rpg.funbox.presentation.MapSocket.applyGame
import com.rpg.funbox.presentation.MapSocket.rejectGame
import com.rpg.funbox.presentation.game.GameActivity
import com.rpg.funbox.presentation.checkPermission
import com.rpg.funbox.presentation.fadeInOut
import com.rpg.funbox.presentation.login.AccessPermission
import com.rpg.funbox.presentation.login.AccessPermission.LOCATION_PERMISSION_REQUEST_CODE
import com.rpg.funbox.presentation.slideLeft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {

    private val viewModel: MapViewModel by activityViewModels()

    private lateinit var locationTimer: Timer
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationSource: FusedLocationSource

    private var isFabOpen = false
    private var infoUserId: Int? = null
    private val requestMultiPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(requireActivity())
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(requireActivity())
                }

                else -> {
                    requireActivity().finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        requestMultiPermissions.launch(AccessPermission.locationPermissionList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.mapUiEvent) { handleUiEvent(it) }

        binding.floatingActionButton.setOnClickListener {
            toggleFab()
        }

        if (requireActivity().checkPermission(AccessPermission.locationPermissionList)) {
            viewModel.setLocationPermitted()
        }

        initMapView()
    }

    override fun onResume() {
        super.onResume()

        submitUserLocation()
    }

    override fun onStart() {
        super.onStart()

        viewModel.buttonGone()
        isFabOpen = false
        viewModel.users.value.forEach { user ->
            user.isInfoOpen = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        locationTimer.cancel()
    }

    @UiThread
    override fun onMapReady(map: NaverMap) {
        val infoWindow = InfoWindow()
        this.naverMap = map.apply {
            locationSource = this@MapFragment.locationSource
            locationTrackingMode = LocationTrackingMode.Face
            uiSettings.isLocationButtonEnabled = true

            minZoom = 5.0
            maxZoom = 20.0
            uiSettings.isZoomControlEnabled = true

            extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
            addOnLocationChangeListener { location ->
                val cameraUpdate =
                    CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
                naverMap.moveCamera(cameraUpdate)
                viewModel.setXY(location.latitude, location.longitude)
            }
            viewModel.users.value.let { users ->
                users.forEach { user ->
                    viewModel.userDetail.value?.let { userDetail ->
                        if ((user.id == userDetail.id) && (user.isInfoOpen)) {
                            infoWindow.open(this)
                        }
                    }
                }
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

        map.setOnMapClickListener { _, _ ->
            viewModel.buttonGone()
            viewModel.users.value.forEach {
                it.isInfoOpen = false
                it.mapPin?.infoWindow?.close()
                Timber.d("@111111")
                if (it.isMsg) {
                    it.mapPin?.let { mapPin -> hasMsg.open(mapPin) }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.usersUpdate.collect {
                viewModel.users.value.also {
                    it.forEach { Timber.d("User MapPin: ${it.id}") }
                    it.map { user ->
                        runBlocking {
                            if (user.mapPin == null) {
                                val marker = Marker().apply {
                                    position = user.loc
                                    iconTintColor = Color.YELLOW
                                    captionText = user.name.toString()
                                    captionTextSize = 20F
                                    if (user.isMsg) {
                                        user.mapPin?.let { mapPin -> hasMsg.open(mapPin) }
                                    }
                                    setMarkerClickListener(user, infoWindow, hasMsg)
                                }
                                marker.map = naverMap
                                user.mapPin = marker
                            } else {
                                user.mapPin?.let { marker ->
                                    Timber.d("${user.id} Marker")
                                    marker.position = user.loc
                                    marker.map = naverMap
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Marker.setMarkerClickListener(
        user: User,
        infoWindow: InfoWindow,
        hasMsg: InfoWindow
    ) {
        var adapter1 = MapProfileAdapter(requireContext(), viewModel.userDetail.value, null)
        setOnClickListener { _ ->
            Timber.d("클릭리스너!!!!!!!!!!!!!!!!!!!!!!!!!!!")
            viewModel.userDetailApi(user.id)
            infoWindow.adapter = adapter1
            runBlocking {
                val test = viewModel.userDetail.value?.profile
                val image: Bitmap = returnProfileImage(test)
                Handler(Looper.getMainLooper()).postDelayed({
                    infoWindow.adapter = adapter1
                    Timber.d(adapter1.toString())
                    infoWindow.open(this@setMarkerClickListener)
                }, 1500)
                adapter1 =
                    MapProfileAdapter(
                        requireContext(),
                        viewModel.userDetail.value,
                        image
                    )
            }


            if (!this.hasInfoWindow() || this.infoWindow == hasMsg) {
                viewModel.buttonVisible()
                viewModel.users.value.forEach { user ->
                    if (user.isMsg) {
                        user.mapPin?.let { mapPin -> hasMsg.open(mapPin) }
                    }
                }
                user.isInfoOpen = true
                infoUserId = user.id
                viewModel.users.value.forEach { temp ->
                    if (temp.id != user.id) {
                        temp.isInfoOpen = false
                        temp.mapPin?.infoWindow?.close()
                        Timber.d("@111111")
                        if (temp.isMsg) {
                            temp.mapPin?.let { mapPin -> hasMsg.open(mapPin) }
                        }
                    }
                }
                infoWindow.open(this)
                Timber.d("@@@@@@")
            } else {
                viewModel.buttonGone()
                user.isInfoOpen = false
                this.infoWindow?.close()
                Timber.d("!!!!!!!")
                if (user.isMsg) {
                    hasMsg.open(this)
                }
            }
            true
        }
    }

    private suspend fun returnProfileImage(test: String?) = try {
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

    private fun initMapView() {
        Timber.d("Init MapView")
        binding.map.getFragment<MapFragment>().getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
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

    private fun submitUserLocation() {
        if (requireActivity().checkPermission(AccessPermission.locationPermissionList)) {
            locationTimer = Timer()
            Timber.d("타이머 실행")
            locationTimer.scheduleAtFixedRate(0, 3000) {
                lifecycleScope.launch {
                    val location = fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).await()
                    val makePinDeferred = async {
                        try {
                            viewModel.setUsersLocations(location.latitude, location.longitude)
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                resources.getString(R.string.gps_on_toast_message),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    makePinDeferred.await()

                }
            }
        }
    }

    private fun handleUiEvent(event: MapUiEvent) = when (event) {
        is MapUiEvent.LocationPermitted -> {
            initMapView()
            submitUserLocation()
        }

        is MapUiEvent.MessageOpen -> {
            findNavController().navigate(R.id.action_mapFragment_to_messageDialog)
            //MessageDialog().show(parentFragmentManager, "messageDialog")
        }

        is MapUiEvent.ToGame -> {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra("StartGame", false)
            viewModel.applyGameFromServerData.value?.let { gameData ->
                intent.putExtra("RoomId", gameData.roomId)
                intent.putExtra("OtherUserId", gameData.userId.toInt())
            }
            startActivity(intent, requireActivity().fadeInOut())
        }

        is MapUiEvent.GameStart -> {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra("StartGame", true)
            intent.putExtra("OtherUserId", viewModel.userDetail.value?.id)
            viewModel.userDetail.value?.let { applyGame(it.id) }
            startActivity(intent, requireActivity().slideLeft())
        }

        is MapUiEvent.RejectGame -> {
            viewModel.applyGameFromServerData.value?.roomId?.let { rejectGame(it) }
        }

        is MapUiEvent.GetGame -> {
            //findNavController().navigate(R.id.action_mapFragment_to_getGameDialog)
            GetGameDialog().show(parentFragmentManager, "getGame")
        }

        is MapUiEvent.ToSetting -> {
            findNavController().navigate(R.id.action_mapFragment_to_settingFragment)
        }

        is MapUiEvent.Toggle -> {
            toggleFab()
        }

        is MapUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }

        else -> {}
    }
}