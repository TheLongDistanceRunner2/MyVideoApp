package com.example.myvideoapp.ui

import android.view.View

sealed interface VideoUiState {
    object CheckingPermissions : VideoUiState
    data class PermissionsMissing(val showRationale: Boolean = false) : VideoUiState
    object ReadyToConnect : VideoUiState
    object Connecting : VideoUiState
    data class Connected(
        val publisherView: View? = null,
        val subscriberView: View? = null,
        val isMicrophoneEnabled: Boolean = true,
        val isCameraEnabled: Boolean = true
    ) : VideoUiState
    data class Disconnected(val reason: String? = null) : VideoUiState
}

sealed interface VideoEvent {
    data class CheckPermissions(val arePermissionsGranted: Boolean) : VideoEvent
    object GrantPermissions : VideoEvent
    object JoinCall : VideoEvent
    object Retry : VideoEvent
    object EndCall : VideoEvent
    object ToggleCamera : VideoEvent
    object ToggleMicrophone : VideoEvent
}

sealed interface VideoEffect {
    object RequestPermissions : VideoEffect
}
