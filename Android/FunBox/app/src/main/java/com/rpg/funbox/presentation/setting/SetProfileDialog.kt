package com.rpg.funbox.presentation.setting

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ext.SdkExtensions.getExtensionVersion
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogProfileChangeBinding
import com.rpg.funbox.presentation.BaseDialogFragment
import com.rpg.funbox.presentation.login.AccessPermission
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File

@SuppressLint("IntentReset")
class SetProfileDialog : BaseDialogFragment<DialogProfileChangeBinding>(R.layout.dialog_profile_change) {

    private val viewModel: SettingViewModel by activityViewModels()
    private val profileImagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            val path = absolutelyPath(uri)
            if (path != null) {
                val file = File(path)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                viewModel.selectNewProfile(uri, body)
            }
        }
    private val requestMultiPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var flag = true
            it.entries.forEach { entry ->
                flag = entry.value
            }
            if (flag) {
                getProfileImage()
            }
        }
    private var storageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imagePath = result.data?.data
                val file = absolutelyPath(imagePath)?.let { File(it) }
                val requestFile = file?.asRequestBody("image/*".toMediaTypeOrNull())
                val body =
                    requestFile?.let { MultipartBody.Part.createFormData("file", file.name, it) }

                if (body != null) {
                    viewModel.selectNewProfile(imagePath, body)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        lifecycleScope.launch {
            viewModel.settingUiEvent.collectLatest { uiEvent ->
                when (uiEvent) {
                    is SettingUiEvent.SelectProfile -> {
                        if (isPhotoPickerAvailable()) {
                            profileImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        } else {
                            requestMultiPermissions.launch(AccessPermission.profilePermissionList)
                        }
                    }

                    is SettingUiEvent.CloseSetProfileDialog -> {
                        dismiss()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getProfileImage() {
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        chooserIntent.putExtra(Intent.EXTRA_INTENT, intent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, resources.getString(R.string.msg_select_picture))
        storageLauncher.launch(chooserIntent)
    }

    private fun absolutelyPath(uri: Uri?): String? {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
            uri?.let { requireActivity().contentResolver.query(uri, proj, null, null, null) }
        cursor?.moveToNext()
        val index = cursor?.getColumnIndex(MediaStore.MediaColumns.DATA)

        Timber.d("Index: $index")

        return index?.let { cursor.getString(index) }
    }

    private fun isPhotoPickerAvailable(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getExtensionVersion(Build.VERSION_CODES.R) >= 2
        } else {
            false
        }
    }
}