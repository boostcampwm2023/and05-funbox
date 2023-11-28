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
import com.navercorp.nid.NaverIdLoginSDK

class TitleFragment : BaseFragment<FragmentTitleBinding>(R.layout.fragment_title) {

    private val viewModel: TitleViewModel by activityViewModels()
    private val naverOAuthLoginCallback: NaverOAuthLoginCallback = NaverOAuthLoginCallback()

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

    private fun handleUiEvent(event: TitleUiEvent) = when (event) {
        is TitleUiEvent.NaverLoginStart -> {
            // findNavController().navigate(R.id.action_TitleFragment_to_NicknameFragment)
            NaverIdLoginSDK.authenticate(requireContext(), naverOAuthLoginCallback)
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