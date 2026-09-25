package com.rohitchauhan.hiichat.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rohitchauhan.hiichat.data.webrtc.CallData
import com.rohitchauhan.hiichat.data.webrtc.IceCandidateModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallSignalingRepo @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    fun sendCallOffer(chatId: String, callData: CallData) {
        firebaseDatabase.reference.child("calls").child(chatId).setValue(callData)
    }

    fun updateCallData(chatId: String, updates: Map<String, Any?>) {
        firebaseDatabase.reference.child("calls").child(chatId).updateChildren(updates)
    }

    fun sendIceCandidate(chatId: String, candidate: IceCandidateModel) {
        firebaseDatabase.reference.child("calls").child(chatId).child("candidates").push().setValue(candidate)
    }

    fun observeCallData(chatId: String): Flow<CallData?> = callbackFlow {
        val callRef = firebaseDatabase.reference.child("calls").child(chatId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(CallData::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        callRef.addValueEventListener(listener)
        awaitClose { callRef.removeEventListener(listener) }
    }

    fun observeIncomingCalls(myUid: String): Flow<Pair<String, CallData>?> = callbackFlow {
        if (myUid.isBlank()) {
            trySend(null)
            return@callbackFlow
        }
        val callsRef = firebaseDatabase.reference.child("calls")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val callData = child.getValue(CallData::class.java)
                    if (callData != null && callData.receiverId == myUid && callData.callState == "CALLING") {
                        trySend(Pair(child.key ?: "", callData))
                        return
                    }
                }
                trySend(null)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        callsRef.addValueEventListener(listener)
        awaitClose { callsRef.removeEventListener(listener) }
    }

    fun observeIceCandidates(chatId: String): Flow<IceCandidateModel> = callbackFlow {
        val candidatesRef = firebaseDatabase.reference.child("calls").child(chatId).child("candidates")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val candidate = child.getValue(IceCandidateModel::class.java)
                    if (candidate != null) {
                        trySend(candidate)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        candidatesRef.addValueEventListener(listener)
        awaitClose { candidatesRef.removeEventListener(listener) }
    }

    fun endCall(chatId: String) {
        firebaseDatabase.reference.child("calls").child(chatId).removeValue()
    }
}
