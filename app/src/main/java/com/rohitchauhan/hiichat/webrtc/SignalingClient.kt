package com.rohitchauhan.webrtcvideocall.webrtc

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class SignalingClient(private val currentUserId: String) {

    private val database = FirebaseDatabase.getInstance().reference
    private val callsRef = database.child("calls")

    fun sendSdp(targetUserId: String, sdp: SessionDescription) {
        val sdpMap = mapOf(
            "type" to sdp.type.canonicalForm(),
            "sdp" to sdp.description,
            "senderId" to currentUserId
        )
        callsRef.child(targetUserId).child("sdp").setValue(sdpMap)
    }

    fun sendIceCandidate(targetUserId: String, candidate: IceCandidate) {
        val candidateMap = mapOf(
            "sdpMid" to candidate.sdpMid,
            "sdpMLineIndex" to candidate.sdpMLineIndex,
            "candidate" to candidate.sdp
        )
        callsRef.child(targetUserId).child("candidates").push().setValue(candidateMap)
    }

    fun observeSdp(onSdpReceived: (SessionDescription, String) -> Unit) {
        callsRef.child(currentUserId).child("sdp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val type = snapshot.child("type").getValue(String::class.java)
                    val sdp = snapshot.child("sdp").getValue(String::class.java)
                    val senderId = snapshot.child("senderId").getValue(String::class.java) ?: ""
                    if (type != null && sdp != null) {
                        onSdpReceived(
                            SessionDescription(
                                SessionDescription.Type.fromCanonicalForm(type),
                                sdp
                            ),
                            senderId
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun observeIceCandidates(onIceCandidateReceived: (IceCandidate) -> Unit) {
        callsRef.child(currentUserId).child("candidates").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val sdpMid = snapshot.child("sdpMid").getValue(String::class.java)
                val sdpMLineIndex = snapshot.child("sdpMLineIndex").getValue(Int::class.java)
                val candidate = snapshot.child("candidate").getValue(String::class.java)
                if (sdpMid != null && sdpMLineIndex != null && candidate != null) {
                    onIceCandidateReceived(IceCandidate(sdpMid, sdpMLineIndex, candidate))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun clearDatabase() {
        callsRef.child(currentUserId).removeValue()
    }
}
