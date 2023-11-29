package com.rpg.funbox.presentation.setting

sealed class SettingUiEvent {
    data object ToMap : SettingUiEvent()
    data object SetName: SettingUiEvent()
    data object SetProfile: SettingUiEvent()
    data object Draw: SettingUiEvent()
}