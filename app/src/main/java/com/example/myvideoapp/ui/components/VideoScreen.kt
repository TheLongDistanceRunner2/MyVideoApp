package com.example.myvideoapp.ui.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.myvideoapp.ui.VideoEffect
import com.example.myvideoapp.ui.VideoEvent
import com.example.myvideoapp.ui.VideoUiState
import com.example.myvideoapp.ui.VideoViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoScreen(viewModel: VideoViewModel) {
    val uiState by viewModel.uiState.collectAsState(initial = VideoUiState.CheckingPermissions)

    val permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    val permissionState = rememberMultiplePermissionsState(permissions) { permissionResults ->
        val allPermissionsGranted = permissionResults.values.all { it }
        viewModel.onEvent(VideoEvent.CheckPermissions(allPermissionsGranted))
    }

    val isPermanentlyDenied =
        permissionState.revokedPermissions.isNotEmpty() && !permissionState.shouldShowRationale

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is VideoEffect.RequestPermissions -> permissionState.launchMultiplePermissionRequest()
            }
        }
    }

    VideoContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        isPermanentlyDenied = isPermanentlyDenied,
        onGoToSettings = {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
        }
    )
}