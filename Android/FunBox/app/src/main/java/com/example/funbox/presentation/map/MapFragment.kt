package com.example.funbox.presentation.map

import com.example.funbox.MessageDialog
import com.example.funbox.R
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.example.funbox.databinding.FragmentMapBinding
import com.example.funbox.presentation.BaseFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.flow.map

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {

    private var isFabOpen = false

    private val viewModel: MapViewModel by activityViewModels()

    private lateinit var naverMap: NaverMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapViewModel = viewModel

        binding.floatingActionButton.setOnClickListener {
            toggleFab()
        }

        binding.floatingWrite.setOnClickListener {
            val messageDialog = MessageDialog()
            messageDialog.show(parentFragmentManager, "messageDialog")
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
