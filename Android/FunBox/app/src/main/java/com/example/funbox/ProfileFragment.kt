package com.example.funbox

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import coil.load
import androidx.fragment.app.viewModels
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.example.funbox.databinding.FragmentProfileBinding

@RequiresApi(Build.VERSION_CODES.P)
class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()
    private val permissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val readImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            this.binding.btnAddProfile.load(uri) {
                scale(Scale.FILL)
                crossfade(enable = true)
                transformations(RoundedCornersTransformation(10F))
            }
            this.viewModel.successSelectProfile(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.profileUiEvent) { handleUiEvent(it) }
    }

    private fun checkPermission() {
        val deniedPermission = permissionList.count {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_DENIED
        }

        if (deniedPermission > 0) {
            requireActivity().requestPermissions(permissionList, 999)
        } else {
            readImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }
    }

    private fun handleUiEvent(event: ProfileUiEvent) = when (event) {
        is ProfileUiEvent.ProfileSelect -> {
            checkPermission()
        }
        is ProfileUiEvent.ProfileSuccess -> {
            TODO("Go to Main")
        }
        is ProfileUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }
    }
}