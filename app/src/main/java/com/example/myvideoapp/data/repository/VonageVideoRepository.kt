package com.example.myvideoapp.data.repository

import android.content.Context
import android.util.Log
import android.view.View
import com.example.myvideoapp.data.VideoApiConfig
import com.opentok.android.BaseVideoRenderer
import com.opentok.android.OpentokError
import com.opentok.android.Publisher
import com.opentok.android.PublisherKit
import com.opentok.android.Session
import com.opentok.android.Stream
import com.opentok.android.Subscriber
import com.opentok.android.SubscriberKit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "VonageVideoRepository"

sealed class ConnectionState {
    data object Idle : ConnectionState()
    data object Connecting : ConnectionState()
    data class Connected(
        val publisherView: View? = null,
        val subscriberView: View? = null,
        val isCameraEnabled: Boolean = true,
        val isMicrophoneEnabled: Boolean = true
    ) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
    data class Disconnected(val message: String) : ConnectionState()
}

class VonageVideoRepository(private val context: Context) {

    private var session: Session? = null
    private var publisher: Publisher? = null
    private var subscriber: Subscriber? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    fun joinCall() {
        if (!VideoApiConfig.isValid) {
            _connectionState.value = ConnectionState.Error("Invalid API configuration!")
            return
        }
        _connectionState.value = ConnectionState.Connecting
        initializeSession(VideoApiConfig.APP_ID, VideoApiConfig.SESSION_ID, VideoApiConfig.TOKEN)
    }

    private fun initializeSession(appId: String, sessionId: String, token: String) {
        session = Session.Builder(context, appId, sessionId).build().apply {
            setSessionListener(sessionListener)
            connect(token)
        }
    }

    fun endCall() {
        session?.disconnect()
    }

    private fun cleanUp() {
        session = null
        publisher = null
        subscriber = null
    }

    fun toggleMicrophone() {
        publisher?.let { pub ->
            val isEnabled = !pub.publishAudio
            pub.publishAudio = isEnabled
            _connectionState.update { currentState ->
                if (currentState is ConnectionState.Connected) {
                    currentState.copy(isMicrophoneEnabled = isEnabled)
                } else {
                    currentState
                }
            }
        }
    }

    fun toggleCamera() {
        publisher?.let { pub ->
            val isEnabled = !pub.publishVideo
            pub.publishVideo = isEnabled
            _connectionState.update { currentState ->
                if (currentState is ConnectionState.Connected) {
                    currentState.copy(isCameraEnabled = isEnabled)
                } else {
                    currentState
                }
            }
        }
    }

    private val sessionListener = object : Session.SessionListener {
        override fun onConnected(session: Session) {
            Log.d(TAG, "=> onConnected: Connected to session: ${session.sessionId}")

            publisher = Publisher.Builder(context).build().apply {
                setPublisherListener(publisherListener)
                renderer.setStyle(
                    BaseVideoRenderer.STYLE_VIDEO_SCALE,
                    BaseVideoRenderer.STYLE_VIDEO_FILL
                )
            }

            _connectionState.value = ConnectionState.Connected(publisherView = publisher?.view)
            session.publish(publisher)
        }

        override fun onDisconnected(session: Session) {
            Log.d(TAG, "=> onDisconnected: Disconnected from session: ${session.sessionId}")
            _connectionState.value = ConnectionState.Disconnected("Call ended")
            cleanUp()
        }

        override fun onStreamReceived(session: Session, stream: Stream) {
            Log.d(TAG, "=> onStreamReceived: New Stream Received ${stream.streamId} in session: ${session.sessionId}")

            if (subscriber == null) {
                subscriber = Subscriber.Builder(context, stream).build().apply {
                    renderer.setStyle(
                        BaseVideoRenderer.STYLE_VIDEO_SCALE,
                        BaseVideoRenderer.STYLE_VIDEO_FILL
                    )
                    setSubscriberListener(subscriberListener)
                }
                session.subscribe(subscriber)
                _connectionState.update {
                    if (it is ConnectionState.Connected) {
                        it.copy(subscriberView = subscriber?.view)
                    } else {
                        it
                    }
                }
            }
        }

        override fun onStreamDropped(session: Session, stream: Stream) {
            Log.d(TAG, "=> onStreamDropped: Stream Dropped: ${stream.streamId} in session: ${session.sessionId}")
            subscriber = null
            _connectionState.update {
                if (it is ConnectionState.Connected) {
                    it.copy(subscriberView = null)
                } else {
                    it
                }
            }
        }

        override fun onError(session: Session, opentokError: OpentokError) {
            Log.e(TAG, "=> Session error: ${opentokError.message}")
            _connectionState.value = ConnectionState.Error(opentokError.message ?: "Session Error")
            cleanUp()
        }
    }

    private val publisherListener = object : PublisherKit.PublisherListener {
        override fun onStreamCreated(publisherKit: PublisherKit, stream: Stream) {
            Log.d(TAG, "=> onStreamCreated: Publisher Stream Created. Stream ID: ${stream.streamId}")
        }

        override fun onStreamDestroyed(publisherKit: PublisherKit, stream: Stream) {
            Log.d(TAG, "=> onStreamDestroyed: Publisher Stream Destroyed. Stream ID: ${stream.streamId}")
        }

        override fun onError(publisherKit: PublisherKit, opentokError: OpentokError) {
            Log.e(TAG, "=> PublisherKit onError: ${opentokError.message}")
            _connectionState.value = ConnectionState.Error(opentokError.message ?: "Publisher Error")
            cleanUp()
        }
    }

    private val subscriberListener = object : SubscriberKit.SubscriberListener {
        override fun onConnected(subscriberKit: SubscriberKit) {
            Log.d(TAG, "=> onConnected: Subscriber connected.")
        }

        override fun onDisconnected(subscriberKit: SubscriberKit) {
            Log.d(TAG, "=> onDisconnected: Subscriber disconnected.")
        }

        override fun onError(subscriberKit: SubscriberKit, opentokError: OpentokError) {
            Log.e(TAG, "=> SubscriberKit onError: ${opentokError.message}")
            _connectionState.value = ConnectionState.Error(opentokError.message ?: "Subscriber Error")
            cleanUp()
        }
    }
}