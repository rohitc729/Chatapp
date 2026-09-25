package com.rohitchauhan.hiichat.data.webrtc

import android.content.Context
import org.webrtc.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebRTCClient @Inject constructor(
    private val context: Context
) {
    val eglBase: EglBase = EglBase.create()
    
    private val peerConnectionFactory: PeerConnectionFactory by lazy {
        initPeerConnectionFactory()
    }

    var peerConnection: PeerConnection? = null
        private set

    private var localVideoCapturer: VideoCapturer? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var localMediaStream: MediaStream? = null

    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
        PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer(),
        PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer()
    )

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
    }

    private fun initPeerConnectionFactory(): PeerConnectionFactory {
        val videoEncoderFactory = DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true)
        val videoDecoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

        return PeerConnectionFactory.builder()
            .setVideoEncoderFactory(videoEncoderFactory)
            .setVideoDecoderFactory(videoDecoderFactory)
            .setOptions(PeerConnectionFactory.Options())
            .createPeerConnectionFactory()
    }

    fun initPeerConnection(observer: PeerConnection.Observer): PeerConnection? {
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        }
        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, observer)
        return peerConnection
    }

    fun startLocalVideo(surfaceViewRenderer: SurfaceViewRenderer) {
        surfaceViewRenderer.init(eglBase.eglBaseContext, null)
        surfaceViewRenderer.setEnableHardwareScaler(true)
        surfaceViewRenderer.setMirror(true)

        val surfaceTextureHelper = SurfaceTextureHelper.create("SurfaceTextureHelperThread", eglBase.eglBaseContext)
        val videoCapturer = createCameraCapturer() ?: return
        localVideoCapturer = videoCapturer

        val videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        videoCapturer.startCapture(720, 1280, 30)

        localVideoTrack = peerConnectionFactory.createVideoTrack("101", videoSource)
        localVideoTrack?.addSink(surfaceViewRenderer)

        localAudioTrack = peerConnectionFactory.createAudioTrack("102", peerConnectionFactory.createAudioSource(MediaConstraints()))

        localMediaStream = peerConnectionFactory.createLocalMediaStream("100")
        localMediaStream?.addTrack(localVideoTrack)
        localMediaStream?.addTrack(localAudioTrack)

        peerConnection?.addStream(localMediaStream)
    }

    fun startLocalAudio() {
        localAudioTrack = peerConnectionFactory.createAudioTrack("102", peerConnectionFactory.createAudioSource(MediaConstraints()))
        localMediaStream = peerConnectionFactory.createLocalMediaStream("100")
        localMediaStream?.addTrack(localAudioTrack)
        peerConnection?.addStream(localMediaStream)
    }

    fun initRemoteSurfaceView(surfaceViewRenderer: SurfaceViewRenderer) {
        surfaceViewRenderer.init(eglBase.eglBaseContext, null)
        surfaceViewRenderer.setEnableHardwareScaler(true)
        surfaceViewRenderer.setMirror(false)
    }

    private fun createCameraCapturer(): VideoCapturer? {
        val enumerator = Camera2Enumerator(context)
        val deviceNames = enumerator.deviceNames

        // Try to find front facing camera
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        // Fallback to back camera
        for (deviceName in deviceNames) {
            if (enumerator.isBackFacing(deviceName)) {
                return enumerator.createCapturer(deviceName, null)
            }
        }
        return null
    }

    fun createOffer(sdpObserver: SdpObserver) {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
        peerConnection?.createOffer(sdpObserver, constraints)
    }

    fun createAnswer(sdpObserver: SdpObserver) {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
        peerConnection?.createAnswer(sdpObserver, constraints)
    }

    fun setLocalDescription(sdpObserver: SdpObserver, sdp: SessionDescription) {
        peerConnection?.setLocalDescription(sdpObserver, sdp)
    }

    fun setRemoteDescription(sdpObserver: SdpObserver, sdp: SessionDescription) {
        peerConnection?.setRemoteDescription(sdpObserver, sdp)
    }

    fun addIceCandidate(iceCandidate: IceCandidate) {
        peerConnection?.addIceCandidate(iceCandidate)
    }

    fun switchCamera() {
        (localVideoCapturer as? CameraVideoCapturer)?.switchCamera(null)
    }

    fun toggleAudio(isMuted: Boolean) {
        localAudioTrack?.setEnabled(!isMuted)
    }

    fun toggleVideo(isMuted: Boolean) {
        localVideoTrack?.setEnabled(!isMuted)
    }

    fun close() {
        try {
            localVideoCapturer?.stopCapture()
            localVideoCapturer?.dispose()
            peerConnection?.close()
            peerConnection = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
