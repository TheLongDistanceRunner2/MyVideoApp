package com.example.myvideoapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionRationaleScreen(
    showRationale: Boolean,
    permanentlyDenied: Boolean,
    onOk: () -> Unit,
    onGoToSettings: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // TODO move text to strings.xml
            val text = if (permanentlyDenied) {
                "You have permanently denied camera and microphone permissions. Please enable them in the app settings to use video calls."
            } else if (showRationale) {
                "Camera and microphone permissions are required to make video calls"
            } else {
                "This app requires camera and microphone access to start a video call."
            }
            Text(text, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))

            val buttonText = if (permanentlyDenied) "Open Settings" else "Grant permissions"
            val onClick = if (permanentlyDenied) onGoToSettings else onOk

            Button(onClick = onClick) {
                Text(buttonText)
            }
        }
    }
}