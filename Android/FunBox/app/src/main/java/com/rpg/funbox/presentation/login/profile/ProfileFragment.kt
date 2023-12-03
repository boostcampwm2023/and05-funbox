package com.rpg.funbox.presentation.login.profile

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentProfileBinding
import com.rpg.funbox.presentation.BaseFragment
import com.rpg.funbox.presentation.MainActivity
import com.rpg.funbox.presentation.login.title.TitleViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@SuppressLint("IntentReset")
class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val viewModel: TitleViewModel by activityViewModels()
    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imagePath = result.data?.data
                val file = File(absolutelyPath(imagePath))
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                viewModel.selectProfile(imagePath, body)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.profileUiEvent) { handleUiEvent(it) }
    }

    private fun getProfileImage() {
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        chooserIntent.putExtra(Intent.EXTRA_INTENT, intent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.msg_select_picture))
        launcher.launch(chooserIntent)
    }

    private fun absolutelyPath(uri: Uri?): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
            uri?.let { requireActivity().contentResolver.query(it, proj, null, null, null) }
        val index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()

        return cursor!!.getString(index!!)
    }

    private fun handleUiEvent(event: ProfileUiEvent) = when (event) {
        is ProfileUiEvent.ProfileSelect -> {
            getProfileImage()
        }

        is ProfileUiEvent.ProfileSubmit -> {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }

        is ProfileUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }

        else -> {}
    }
}