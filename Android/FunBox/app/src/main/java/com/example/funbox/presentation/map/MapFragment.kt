package com.example.funbox.presentation.map

import com.example.funbox.MessageDialog
import com.example.funbox.R
import android.animation.ObjectAnimator
import android.graphics.Color
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.example.funbox.databinding.FragmentMapBinding
import com.example.funbox.presentation.BaseFragment
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {

    private var isFabOpen = false

    private val viewModel: MapViewModel by activityViewModels()

    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val handler = Handler()

    private fun handleUiEvent(event: MapUiEvent) = when (event) {
        is MapUiEvent.MessageOpen -> {
            MessageDialog().show(parentFragmentManager, "messageDialog")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setLocationListener()

        collectLatestFlow(viewModel.mapUiEvent) { handleUiEvent(it) }

        binding.floatingActionButton.setOnClickListener {
            toggleFab()
        }

        viewModel.mapApi()
        initMapView()
    }

    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)
    }

    private fun setLocationListener() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            handler.postDelayed(locationRunnable, 5000)
        } else {
            // 권한이 없으면 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                123
            )
        }
    }

    private val locationRunnable = object : Runnable {
        override fun run() {
            // 주기적으로 위치 업데이트 요청
            requestLocationUpdates()
            handler.postDelayed(this, 5000) // 다음 위치 업데이트는 5초 뒤에
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location
                location?.let {
                    val latitude = it.latitude
                    val longitude = it.longitude
                    viewModel.setXY(latitude, longitude)

                    sendXYtoApi(latitude, longitude)

                }
            }
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

    override fun onMapReady(map: NaverMap) {
        this.naverMap = map

        val hasMsg = InfoWindow()
        hasMsg.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return "● ● ●"
            }
        }

        viewModel.users.value.map {

            val marker = Marker()
            marker.position = it.loc
            marker.iconTintColor = Color.RED
            marker.map = naverMap
            marker.captionText = it.name
            marker.captionTextSize = 20F
            if (it.isMsg) {
                hasMsg.open(marker)
            }
            marker.setOnClickListener { _ ->
                viewModel.userDetailApi(it.id)
                viewModel.userDetail.value

                val infoWindow = InfoWindow()
                infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(requireContext()) {
                    override fun getContentView(p0: InfoWindow): View {
                        val view: View = View.inflate(context, R.layout.map_profile, null)

                        view.findViewById<ImageView>(R.id.iv_profile)
                            .load(viewModel.userDetail.value.profile)
                        view.findViewById<TextView>(R.id.tv_profile_name).text =
                            viewModel.userDetail.value.name
                        view.findViewById<TextView>(R.id.tv_profile_msg).text =
                            viewModel.userDetail.value.msg

                        return view
                    }
                }
                if (!marker.hasInfoWindow() || marker.infoWindow == hasMsg) {
                    infoWindow.open(marker)
                } else {
                    marker.infoWindow?.close()
                    if (it.isMsg) {
                        hasMsg.open(marker)
                    }
                }
                true
            }
        }
    }
}