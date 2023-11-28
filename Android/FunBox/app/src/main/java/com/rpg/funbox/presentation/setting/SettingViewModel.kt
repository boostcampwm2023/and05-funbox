package com.rpg.funbox.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpg.funbox.presentation.map.MapUiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {
    private val _settingUiEvent = MutableSharedFlow<SettingUiEvent>()
    val settingUiEvent = _settingUiEvent.asSharedFlow()

    fun toMap() {
        viewModelScope.launch {
            _settingUiEvent.emit(SettingUiEvent.ToMap)
        }
    }
}