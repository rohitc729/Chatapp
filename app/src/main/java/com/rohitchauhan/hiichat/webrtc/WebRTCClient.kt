package com.rohitchauhan.webrtcvideocall.webrtc

import android.content.Context
import org.webrtc.*

class WebRTCClient(
    private val context: Context,
    private val observer: PeerConnection.Observer
) {

    private val eglBase = EglBase.create()
    private val peerConnectionFactory by lazy { createPeerConnectionFactory() }
    private val peerConnection by lazy { createPeerConnection(observer) }

    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null

    init {
        initPeerConnectionFactory(context)
    }

    private fun initPeerConnectionFactory(context: Context) {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
    }

    private fun createPeerConnectionFactory(): PeerConnectionFactory {
        return PeerConnectionFactory.builder()
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setOptions(PeerConnectionFactory.Options().apply {
                disableEncryption = false
                disableNetworkMonitor = false
            })
            .createPeerConnectionFactory()
    }

    private fun createPeerConnection(observer: PeerConnection.Observer): PeerConnection? {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )
        val config = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }
        return peerConnectionFactory.createPeerConnection(config, observer)
    }

    fun getLocalVideoTrack(): VideoTrack {
        if (localVideoTrack == null) {
            val videoSource = peerConnectionFactory.createVideoSource(false)
            val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
            val videoCapturer = createVideoCapturer(context)
            
            videoCapturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
            videoCapturer.startCapture(720, 1280, 30)

            localVideoTrack = peerConnectionFactory.createVideoTrack("101", videoSource)

            val audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
            localAudioTrack = peerConnectionFactory.createAudioTrack("102", audioSource)

            val streamId = "stream1"
            peerConnection?.addTrack(localVideoTrack, listOf(streamId))
            peerConnection?.addTrack(localAudioTrack, listOf(streamId))
        }
        return localVideoTrack!!
    }

    private fun createVideoCapturer(context: Context): VideoCapturer {
        val enumerator = Camera2Enumerator(context)
        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        return enumerator.createCapturer(deviceNames[0], null)
    }

    fun call(sdpObserver: SdpObserver) {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        }
        peerConnection?.createOffer(object : SdpObserver by sdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {
                peerConnection?.setLocalDescription(sdpObserver, desc)
                sdpObserver.onCreateSuccess(desc)
            }
        }, constraints)
    }

    fun answer(sdpObserver: SdpObserver) {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        }
        peerConnection?.createAnswer(object : SdpObserver by sdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {
                peerConnection?.setLocalDescription(sdpObserver, desc)
                sdpObserver.onCreateSuccess(desc)
            }
        }, constraints)
    }

    fun onRemoteSessionReceived(sessionDescription: SessionDescription) {
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onSetSuccess() {}
            override fun onCreateFailure(p0: String?) {}
            override fun onSetFailure(p0: String?) {}
        }, sessionDescription)
    }

    fun addIceCandidate(iceCandidate: IceCandidate) {
        peerConnection?.addIceCandidate(iceCandidate)
    }
    
    fun getEglBaseContext(): EglBase.Context = eglBase.eglBaseContext

    fun close() {
        peerConnection?.close()
        peerConnectionFactory.dispose()
        eglBase.release()
    }
}
