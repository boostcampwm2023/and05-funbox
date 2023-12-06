package com.rpg.funbox.presentation.login.title

data class TitleUiState(
    val networkSuccess: Boolean = true
) {
    val isNaverLoginBtnEnabled: Boolean = (networkSuccess)
}