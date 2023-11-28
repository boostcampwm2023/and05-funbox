package com.rpg.funbox.presentation.setting

sealed class SettingUiEvent {
    data object ToMap : SettingUiEvent()
}