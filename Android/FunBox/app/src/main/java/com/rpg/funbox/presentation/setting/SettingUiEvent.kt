package com.rpg.funbox.presentation.setting

import com.rpg.funbox.presentation.map.MapUiEvent

sealed class SettingUiEvent {

    data object GoToMapFragment : SettingUiEvent()

    data object SetName : SettingUiEvent()

    data object SelectProfile : SettingUiEvent()

    data object SetProfile : SettingUiEvent()

    data object StartWithdrawal : SettingUiEvent()

    data object Withdraw : SettingUiEvent()

    data object CloseSetNameDialog : SettingUiEvent()

    data object CloseSetProfileDialog : SettingUiEvent()

    data object ToGame : SettingUiEvent()

    data object RejectGame : SettingUiEvent()

    data object GetGame : SettingUiEvent()
}