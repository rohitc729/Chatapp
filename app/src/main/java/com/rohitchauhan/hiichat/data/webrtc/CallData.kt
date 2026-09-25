package com.rohitchauhan.hiichat.data.webrtc

data class CallData(
    val callerId: String = "",
    val receiverId: String = "",
    val callerName: String = "",
    val isVideoCall: Boolean = true,
    val callState: String = "IDLE", // IDLE, CALLING, ANSWERED, ENDED
    val offerSdp: String? = null,
    val offerType: String? = null,
    val answerSdp: String? = null,
    val answerType: String? = null
)

data class IceCandidateModel(
    val sdpMid: String = "",
    val sdpMLineIndex: Int = 0,
    val sdp: String = "",
    val senderId: String = ""
)
