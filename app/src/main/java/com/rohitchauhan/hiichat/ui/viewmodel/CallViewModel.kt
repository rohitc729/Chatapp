package com.rohitchauhan.hiichat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitchauhan.hiichat.data.repository.CallSignalingRepo
import com.rohitchauhan.hiichat.data.webrtc.CallData
import com.rohitchauhan.hiichat.data.webrtc.IceCandidateModel
import com.rohitchauhan.hiichat.data.webrtc.WebRTCClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.webrtc.*
import javax.inject.Inject

sealed interface CallState {
    data object Idle : CallState
    data class Calling(val callerName: String) : CallState
    data class Incoming(val callerName: String) : CallState
    data object Connected : CallState
    data object Ended : CallState
}

@HiltViewModel
class CallViewModel @Inject constructor(
    val webRTCClient: WebRTCClient,
    private val signalingRepo: CallSignalingRepo
) : ViewModel() {

    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState = _callState.asStateFlow()

    private var currentChatId: String? = null
    var isMuted = MutableStateFlow(false)
    var isVideoEnabled = MutableStateFlow(true)

    fun startCall(chatId: String, callerId: String, receiverId: String, callerName: String, isVideoCall: Boolean) {
        if (currentChatId == chatId && _callState.value !is CallState.Idle && _callState.value !is CallState.Ended) {
            return
        }
        currentChatId = chatId
        _callState.value = CallState.Calling(callerName)

        setupPeerConnection(chatId)

        val offerCallData = CallData(
            callerId = callerId,
            receiverId = receiverId,
            callerName = callerName,
            isVideoCall = isVideoCall,
            callState = "CALLING"
        )

        webRTCClient.createOffer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(sdp: SessionDescription) {
                webRTCClient.setLocalDescription(object : SdpObserverAdapter() {}, sdp)
                signalingRepo.sendCallOffer(
                    chatId,
                    offerCallData.copy(
                        offerSdp = sdp.description,
                        offerType = sdp.type.canonicalForm()
                    )
                )
            }
        })

        observeSignaling(chatId, callerId)
    }

    fun answerCall(chatId: String, callerId: String) {
        currentChatId = chatId
        setupPeerConnection(chatId)

        observeSignaling(chatId, callerId)
    }

    private fun setupPeerConnection(chatId: String) {
        webRTCClient.initPeerConnection(object : PeerConnectionAdapter() {
            override fun onIceCandidate(candidate: IceCandidate) {
                signalingRepo.sendIceCandidate(
                    chatId,
                    IceCandidateModel(
                        sdpMid = candidate.sdpMid,
                        sdpMLineIndex = candidate.sdpMLineIndex,
                        sdp = candidate.sdp,
                        senderId = webRTCClient.peerConnection?.toString() ?: ""
                    )
                )
            }

            override fun onAddStream(stream: MediaStream) {
                _callState.value = CallState.Connected
            }
        })
    }

    private fun observeSignaling(chatId: String, myUid: String) {
        viewModelScope.launch {
            signalingRepo.observeCallData(chatId).collect { callData ->
                if (callData?.callState == "ENDED") {
                    _callState.value = CallState.Ended
                    return@collect
                }

                if (callData != null) {
                    if (callData.offerSdp != null && callData.answerSdp == null && callData.callerId != myUid) {
                        val remoteSdp = SessionDescription(SessionDescription.Type.OFFER, callData.offerSdp)
                        webRTCClient.setRemoteDescription(object : SdpObserverAdapter() {}, remoteSdp)

                        webRTCClient.createAnswer(object : SdpObserverAdapter() {
                            override fun onCreateSuccess(sdp: SessionDescription) {
                                webRTCClient.setLocalDescription(object : SdpObserverAdapter() {}, sdp)
                                signalingRepo.updateCallData(
                                    chatId,
                                    mapOf(
                                        "answerSdp" to sdp.description,
                                        "answerType" to sdp.type.canonicalForm(),
                                        "callState" to "ANSWERED"
                                    )
                                )
                            }
                        })
                    } else if (callData.answerSdp != null && callData.callerId == myUid) {
                        val remoteSdp = SessionDescription(SessionDescription.Type.ANSWER, callData.answerSdp)
                        webRTCClient.setRemoteDescription(object : SdpObserverAdapter() {}, remoteSdp)
                        _callState.value = CallState.Connected
                    }
                }
            }
        }

        viewModelScope.launch {
            signalingRepo.observeIceCandidates(chatId).collect { candidateModel ->
                val iceCandidate = IceCandidate(candidateModel.sdpMid, candidateModel.sdpMLineIndex, candidateModel.sdp)
                webRTCClient.addIceCandidate(iceCandidate)
            }
        }
    }

    fun toggleAudio() {
        isMuted.value = !isMuted.value
        webRTCClient.toggleAudio(isMuted.value)
    }

    fun toggleVideo() {
        isVideoEnabled.value = !isVideoEnabled.value
        webRTCClient.toggleVideo(isVideoEnabled.value)
    }

    fun switchCamera() {
        webRTCClient.switchCamera()
    }

    fun endCall() {
        currentChatId?.let { chatId ->
            signalingRepo.endCall(chatId)
        }
        webRTCClient.close()
        _callState.value = CallState.Ended
    }

    override fun onCleared() {
        super.onCleared()
        webRTCClient.close()
    }
}

open class PeerConnectionAdapter : PeerConnection.Observer {
    override fun onSignalingChange(state: PeerConnection.SignalingState?) {}
    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {}
    override fun onIceConnectionReceivingChange(p0: Boolean) {}
    override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) {}
    override fun onIceCandidate(candidate: IceCandidate) {}
    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {}
    override fun onAddStream(stream: MediaStream) {}
    override fun onRemoveStream(stream: MediaStream?) {}
    override fun onDataChannel(p0: DataChannel?) {}
    override fun onRenegotiationNeeded() {}
    override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}
}

open class SdpObserverAdapter : SdpObserver {
    override fun onCreateSuccess(sdp: SessionDescription) {}
    override fun onSetSuccess() {}
    override fun onCreateFailure(p0: String?) {}
    override fun onSetFailure(p0: String?) {}
}
