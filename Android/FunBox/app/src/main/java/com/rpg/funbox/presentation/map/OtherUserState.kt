package com.rpg.funbox.presentation.map

data class OtherUserState (val otherState: OtherState = OtherState.Online){
    val canStart : Boolean = (otherState == OtherState.Online)
}

enum class OtherState {
    Online,
    Offline,
    Playing
}