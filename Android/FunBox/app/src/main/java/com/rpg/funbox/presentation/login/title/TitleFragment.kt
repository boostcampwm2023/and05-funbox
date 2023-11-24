package com.rpg.funbox.presentation.login.title

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentTitleBinding
import com.rpg.funbox.presentation.BaseFragment
import com.rpg.funbox.presentation.MainActivity
import com.rpg.funbox.presentation.login.NaverOAuthLoginCallback
import com.rpg.funbox.presentation.login.NaverProfileCallback
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import timber.log.Timber

class TitleFragment : BaseFragment<FragmentTitleBinding>(R.layout.fragment_title) {

    private val viewModel: TitleViewModel by activityViewModels()
    private val naverProfileCallback: NaverProfileCallback = NaverProfileCallback()
    private val naverOAuthLoginCallback: NaverOAuthLoginCallback =
        NaverOAuthLoginCallback(naverProfileCallback)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initNaverIdLoginSdk()
        collectLatestFlow(viewModel.titleUiEvent) { handleUiEvent(it) }
    }

    private fun initNaverIdLoginSdk() {
        NaverIdLoginSDK.initialize(
            requireContext(),
            getString(R.string.naver_login_id_key),
            getString(R.string.naver_login_secret_key),
            getString(R.string.social_login_info_naver_client_name)
        )
    }

    private fun getNaverProfile(): NidProfileCallback<NidProfileResponse> {
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

        return profileCallback
    }

    private fun getNaverOAuth(profileCallback: NidProfileCallback<NidProfileResponse>): OAuthLoginCallback {
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(profileCallback)
                viewModel.successNaverLogin()
                NaverIdLoginSDK.getAccessToken()?.let { token -> Timber.d("NAVER Token: $token") }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                showErrorMessage()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(httpStatus = errorCode, message = message)
            }
        }

        return oauthLoginCallback
    }

    private fun startNaverLogin(oauthLoginCallback: OAuthLoginCallback) {
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
            naverProfileCallback.profileUserId?.let { userId ->
                viewModel.submitUserId(userId)
            }
        }

        is TitleUiEvent.NaverAccessTokenSubmit -> {
            naverOAuthLoginCallback.accessToken?.let { token ->
                viewModel.submitAccessToken(token)
            }
        }

        is TitleUiEvent.SignUpStart -> {
            findNavController().navigate(R.id.action_TitleFragment_to_NicknameFragment)
        }

        is TitleUiEvent.NaverLoginSuccess -> {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.social_login_info_naver_success),
                Toast.LENGTH_SHORT
            ).show()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }

        is TitleUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }
    }
}