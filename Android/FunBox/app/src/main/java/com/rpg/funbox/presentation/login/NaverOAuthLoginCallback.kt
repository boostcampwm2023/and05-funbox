package com.rpg.funbox.presentation.login

import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import timber.log.Timber

class NaverOAuthLoginCallback() : OAuthLoginCallback {

    private val profileCallback = object : NidProfileCallback<NidProfileResponse> {
        override fun onSuccess(result: NidProfileResponse) {
            Timber.d("회원 이름: ${result.profile?.id}")
        }

        override fun onFailure(httpStatus: Int, message: String) {
            accessToken = null
        }

        override fun onError(errorCode: Int, message: String) {
            onFailure(httpStatus = errorCode, message = message)
        }
    }
    var accessToken: String? = null

    override fun onSuccess() {
        NidOAuthLogin().callProfileApi(profileCallback)
        NaverIdLoginSDK.getAccessToken()?.let { token ->
            Timber.d("NAVER Token: $token")
            accessToken = token
        }
    }

    override fun onFailure(httpStatus: Int, message: String) {
        // showErrorMessage()
    }

    override fun onError(errorCode: Int, message: String) {
        onFailure(httpStatus = errorCode, message = message)
    }
}