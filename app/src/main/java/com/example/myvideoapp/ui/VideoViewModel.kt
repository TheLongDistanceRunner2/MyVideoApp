package com.example.myvideoapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myvideoapp.data.repository.ConnectionState
import com.example.myvideoapp.data.repository.VonageVideoRepository
import com.example.myvideoapp.ui.VideoUiState.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {
    private val videoRepository = VonageVideoRepository(application)

    private val _uiState = MutableStateFlow<VideoUiState>(CheckingPermissions)
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<VideoEffect>()
    val effects = _effects.asSharedFlow()

    private var arePermissionsGranted = false

    init {
        videoRepository.connectionState
            .onEach { connectionState ->
                val newState = when (connectionState) {
                    ConnectionState.Connecting -> Connecting
                    is ConnectionState.Connected -> Connected(
                        publisherView = connectionState.publisherView,
                        subscriberView = connectionState.subscriberView,
                        isCameraEnabled = connectionState.isCameraEnabled,
                        isMicrophoneEnabled = connectionState.isMicrophoneEnabled
                    )
                    is ConnectionState.Error -> Disconnected(connectionState.message)
                    is ConnectionState.Disconnected -> {
                        if (arePermissionsGranted) {
                            ReadyToConnect
                        } else {
                            PermissionsMissing(showRationale = false)
                        }
                    }
                    ConnectionState.Idle -> return@onEach
                }
                _uiState.value = newState
            }.launchIn(viewModelScope)
    }

    fun onEvent(event: VideoEvent) {
        viewModelScope.launch {
            when (event) {
                is VideoEvent.CheckPermissions -> {
                    arePermissionsGranted = event.arePermissionsGranted
                    if (event.arePermissionsGranted) {
                        _uiState.value = ReadyToConnect
                    } else {
                        _uiState.value = PermissionsMissing(showRationale = false)
                    }
                }
                is VideoEvent.GrantPermissions -> {
                     _uiState.update {
                        if (it is PermissionsMissing) it.copy(showRationale = true) else it
                    }
                    _effects.emit(VideoEffect.RequestPermissions)
                }
                is VideoEvent.JoinCall -> {
                    if (arePermissionsGranted) {
                        videoRepository.joinCall()
                    }
                }
                is VideoEvent.Retry -> {
                    if (arePermissionsGranted) {
                        videoRepository.joinCall()
                    }
                }
                is VideoEvent.EndCall -> videoRepository.endCall()
                is VideoEvent.ToggleCamera -> videoRepository.toggleCamera()
                is VideoEvent.ToggleMicrophone -> videoRepository.toggleMicrophone()
            }
        }
    }

    override fun onCleared() {
        videoRepository.endCall()
        super.onCleared()
    }
}