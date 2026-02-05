package com.example.myvideoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myvideoapp.ui.VideoEvent
import com.example.myvideoapp.ui.VideoUiState

@Composable
fun CallScreen(state: VideoUiState.Connected, onEvent: (VideoEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            state.subscriberView?.let { view ->
                AndroidView(
                    factory = { view },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(width = 90.dp, height = 120.dp)
            ) {
                state.publisherView?.let { view ->
                    AndroidView(
                        factory = { view },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onEvent(VideoEvent.ToggleMicrophone) }) {
                Icon(
                    imageVector = if (state.isMicrophoneEnabled) Icons.Filled.Mic else Icons.Filled.MicOff,
                    contentDescription = "Toggle Microphone",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = { onEvent(VideoEvent.EndCall) },
            ) {
                Icon(
                    imageVector = Icons.Filled.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.Red
                )
            }
            IconButton(onClick = { onEvent(VideoEvent.ToggleCamera) }) {
                Icon(
                    imageVector = if (state.isCameraEnabled) Icons.Filled.Videocam else Icons.Filled.VideocamOff,
                    contentDescription = "Toggle Camera",
                    tint = Color.White
                )
            }
        }
    }
}