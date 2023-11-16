package com.example.funbox.presentation.login.title

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.funbox.R
import com.example.funbox.databinding.FragmentTitleBinding
import com.example.funbox.presentation.BaseFragment
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import timber.log.Timber

class TitleFragment : BaseFragment<FragmentTitleBinding>(R.layout.fragment_title) {

    private val viewModel: TitleViewModel by viewModels()
    private var naverToken: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initNaverIdLoginSdk()
        collectLatestFlow(viewModel.titleUiEvent) { handleUiEvent(it) }
    }

    private fun initNaverIdLoginSdk() {
        val naverClientId = getString(R.string.naver_login_id_key)
        val naverClientSecret = getString(R.string.naver_login_secret_key)
        val naverClientName = getString(R.string.social_login_info_naver_client_name)
        NaverIdLoginSDK.initialize(
            requireContext(),
            naverClientId,
            naverClientSecret,
            naverClientName
        )
    }

    private fun startNaverLogin() {
        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                Timber.d("회원 이름: ${result.profile?.id}")
            }

            override fun onFailure(httpStatus: Int, message: String) {
                showErrorMessage()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(httpStatus = errorCode, message = message)
            }
        }

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                naverToken = NaverIdLoginSDK.getAccessToken()
                NidOAuthLogin().callProfileApi(profileCallback)
                viewModel.successNaverLogin()
                naverToken?.let { Timber.d("NAVER Token: $it") }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                showErrorMessage()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(httpStatus = errorCode, message = message)
            }
        }

        NaverIdLoginSDK.authenticate(requireContext(), oauthLoginCallback)
    }

    private fun showErrorMessage() {
        val errorCode = NaverIdLoginSDK.getLastErrorCode().code
        val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
        Timber.d("${errorCode}: $errorDescription")
        Toast.makeText(
            requireContext(), "${errorCode}: $errorDescription", Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleUiEvent(event: TitleUiEvent) = when (event) {
        is TitleUiEvent.NaverLoginStart -> {
            startNaverLogin()
        }

        is TitleUiEvent.NaverLoginSuccess -> {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.social_login_info_naver_success),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_TitleFragment_to_NicknameFragment)
        }

        is TitleUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }
    }
}