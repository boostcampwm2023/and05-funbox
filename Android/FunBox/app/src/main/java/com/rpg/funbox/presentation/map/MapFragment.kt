package com.rpg.funbox.presentation.map

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.coroutines.coroutineContext

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {

    private val viewModel: MapViewModel by activityViewModels()

    private lateinit var mContext: Context
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
                    Toast.makeText(
                        mContext,
                        resources.getString(R.string.location_permission_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        requestMultiPermissions.launch(AccessPermission.locationPermissionList)
        if (!requireActivity().checkPermission(AccessPermission.locationPermissionList)) {
            requestMultiPermissions.launch(AccessPermission.locationPermissionList)
        }
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

        locationTimer = Timer()
    }

    override fun onPause() {
        super.onPause()

        locationTimer.cancel()
    }

    override fun onStart() {
        super.onStart()

        initMapView()
        submitUserLocation()

        viewModel.buttonGone()
        isFabOpen = false
    }

    override fun onDestroyView() {
        super.onDestroyView()

        locationTimer.cancel()
    }

    @UiThread
    override fun onMapReady(map: NaverMap) {
        this.naverMap = map.apply {
            locationSource = this@MapFragment.locationSource

            locationTrackingMode = LocationTrackingMode.Face
            uiSettings.isLocationButtonEnabled = true

            minZoom = 5.0
            maxZoom = 20.0
            uiSettings.isZoomControlEnabled = true

            extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
            addOnLocationChangeListener { location ->
                viewModel.setXY(location.latitude, location.longitude)
            }
        }

        naverMap.locationOverlay.apply {
            isVisible = true
            iconHeight = 120
            iconWidth = 120
            icon = OverlayImage.fromResource(R.drawable.navi_icon)
        }

        map.setOnMapClickListener { _, _ ->
            viewModel.buttonGone()
            viewModel.users.value.forEach {
                it.mapPin?.infoWindow?.close()
                if (it.isMsg) {
                    it.mapPin?.let { mapPin ->
                        val hasMsg = InfoWindow()
                        hasMsg.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
                            override fun getText(infoWindow: InfoWindow): CharSequence {
                                return "● ● ●"
                            }
                        }
                        hasMsg.open(mapPin)
                    }
                }
            }
        }

        map.addOnCameraChangeListener { _, _ ->
            naverMap.locationOverlay.apply {
                circleOutlineColor = resources.getColor(R.color.purple, null)
                circleColor = resources.getColor(R.color.not, null)
                circleRadius = (600 / naverMap.projection.metersPerPixel).toInt()
                circleOutlineWidth = 10
            }
        }

        lifecycleScope.launch {
            viewModel.usersUpdate.collect {
                viewModel.users.value.also {
                    it.map { user ->
                        runBlocking {
                            if (user.mapPin == null) {
                                val marker = Marker().apply {
                                    position = user.loc
                                    icon = MarkerIcons.BLACK
                                    iconTintColor = resources.getColor(R.color.purple, null)
                                    width = Marker.SIZE_AUTO
                                    height = Marker.SIZE_AUTO
                                    captionText = user.name.toString()
                                    captionTextSize = 20F
                                    if (user.isMsg) {
                                        user.mapPin?.let { mapPin ->
                                            val hasMsg = InfoWindow()
                                            hasMsg.adapter =
                                                object : InfoWindow.DefaultTextAdapter(mContext) {
                                                    override fun getText(infoWindow: InfoWindow): CharSequence {
                                                        return "● ● ●"
                                                    }
                                                }
                                            hasMsg.open(mapPin)
                                        }
                                    }
                                    setMarkerClickListener(user)
                                }
                                marker.map = naverMap
                                user.mapPin = marker
                            } else {
                                user.mapPin?.let { marker ->
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
        user: User
    ) {
        val infoWindow = InfoWindow()
        val hasMsg = InfoWindow()
        hasMsg.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return "● ● ●"
            }
        }
        setOnClickListener { _ ->
            val adapter1 = MapProfileAdapter(
                mContext,
                viewModel.getDetail(user.id),
                viewModel.getProfile(user.id)
            )
            viewModel.updateClickedUserId(user.id)
            infoWindow.adapter = adapter1
            Timber.d(viewModel.getProfile(user.id).toString())
            infoWindow.open(this@setMarkerClickListener)

            if (this.hasInfoWindow() || this.infoWindow == hasMsg) {
                viewModel.buttonVisible()
                viewModel.users.value.forEach { user ->
                    if (user.isMsg) {
                        user.mapPin?.let { mapPin -> hasMsg.open(mapPin) }
                    }
                }
                infoUserId = user.id
                viewModel.users.value.forEach { temp ->
                    if (temp.id != user.id) {
                        temp.mapPin?.infoWindow?.close()
                        if (temp.isMsg) {
                            temp.mapPin?.let { mapPin -> hasMsg.open(mapPin) }
                        }
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

    private fun initMapView() {
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        val mapFragment = binding.map.getFragment()
            ?: MapFragment.newInstance().also { _ ->
                NaverMapOptions().camera(locationSource.lastLocation?.latitude?.let { latitude ->
                    locationSource.lastLocation?.longitude?.let { longitude ->
                        LatLng(
                            latitude,
                            longitude
                        )
                    }
                }?.let { CameraPosition(it, 8.0) })
            }
        mapFragment.getMapAsync(this)
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
                                mContext,
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
            viewModel.otherUserStartState(other = OtherState.Online)
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra("StartGame", true)
            intent.putExtra("OtherUserId", viewModel.clickedUserId.value)
            applyGame(viewModel.clickedUserId.value)

            CoroutineScope(Dispatchers.Main).launch {
                delay(500)

                if (!viewModel.otherUserState.value.canStart){
                    showSnackBar(R.string.other_not)
                }else{
                    startActivity(intent, requireActivity().slideLeft())
                }

            }

            Timber.d("보내기")
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

        is MapUiEvent.Change -> {
            showSnackBar(R.string.not_yet)
        }

        else -> {}
    }
}