package com.example.myvideoapp.ui.components

import androidx.compose.runtime.Composable
import com.example.myvideoapp.ui.VideoEvent
import com.example.myvideoapp.ui.VideoUiState

@Composable
fun VideoContent(
    uiState: VideoUiState,
    onEvent: (VideoEvent) -> Unit,
    isPermanentlyDenied: Boolean,
    onGoToSettings: () -> Unit
) {
    when (uiState) {
        is VideoUiState.CheckingPermissions -> LoadingScreen("Checking permissions...")

        is VideoUiState.PermissionsMissing -> PermissionRationaleScreen(
            showRationale = uiState.showRationale,
            permanentlyDenied = isPermanentlyDenied,
            onOk = { onEvent(VideoEvent.GrantPermissions) },
            onGoToSettings = onGoToSettings
        )

        is VideoUiState.ReadyToConnect -> ReadyToConnectScreen(
            onJoin = { onEvent(VideoEvent.JoinCall) }
        )

        is VideoUiState.Connecting -> LoadingScreen("Connecting...")

        is VideoUiState.Connected -> CallScreen(uiState, onEvent)

        is VideoUiState.Disconnected -> DisconnectedScreen(
            reason = uiState.reason,
            onRetry = { onEvent(VideoEvent.Retry) }
        )
    }
}