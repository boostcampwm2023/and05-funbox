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
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.rpg.funbox.presentation.slideLeft
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import kotlin.properties.Delegates

class TitleFragment : BaseFragment<FragmentTitleBinding>(R.layout.fragment_title) {

    private val viewModel: TitleViewModel by activityViewModels()
    private var isNetworkError by Delegates.notNull<Boolean>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        initNaverIdLoginSdk()
        collectLatestFlow(viewModel.titleUiEvent) { handleUiEvent(it) }

        isNetworkError = requireActivity().intent.getBooleanExtra("ServerError", false)
        Timber.d("$isNetworkError")
        if (isNetworkError) {
            viewModel.alertNetworkError()
        }
    }

    private fun initNaverIdLoginSdk() {
        NaverIdLoginSDK.initialize(
            requireContext(),
            getString(R.string.naver_login_id_key),
            getString(R.string.naver_login_secret_key),
            getString(R.string.social_login_info_naver_client_name)
        )
        // NaverIdLoginSDK.logout()
    }

    private fun loginAuthenticate() {
        val naverOAuthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                NaverIdLoginSDK.getAccessToken()?.let { token ->
                    viewModel.submitAccessToken(token)
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {}

            override fun onError(errorCode: Int, message: String) {
                onFailure(httpStatus = errorCode, message = message)
            }
        }

        runBlocking {
            NaverIdLoginSDK.authenticate(requireContext(), naverOAuthLoginCallback)
        }
    }

    private fun handleUiEvent(event: TitleUiEvent) = when (event) {
        is TitleUiEvent.NaverLoginStart -> {
            runBlocking {
                val authenticateDeferred = async { loginAuthenticate() }
                authenticateDeferred.await()
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
            requireActivity().slideLeft()
        }

        is TitleUiEvent.NetworkErrorEvent -> {
            Timber.d("Network Error")
            showSnackBar(R.string.network_error_message)
        }
    }
}