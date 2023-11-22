package com.example.funbox.presentation.login

import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import timber.log.Timber

class NaverProfileCallback : NidProfileCallback<NidProfileResponse> {

    var profileUserId: String? = null

    override fun onSuccess(result: NidProfileResponse) {
        Timber.d("회원 이름: ${result.profile?.id}")
        profileUserId = result.profile?.id
    }

    override fun onFailure(httpStatus: Int, message: String) {
        // showErrorMessage()
    }

    override fun onError(errorCode: Int, message: String) {
        onFailure(httpStatus = errorCode, message = message)
    }
}