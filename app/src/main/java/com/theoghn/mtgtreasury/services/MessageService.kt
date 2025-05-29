package com.theoghn.mtgtreasury.services

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theoghn.mtgtreasury.models.ChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageService @Inject constructor() {
    private val database = FirebaseDatabase.getInstance("https://mtg-treasury-default-rtdb.europe-west1.firebasedatabase.app/")

    fun listenForMessages(roomId: String): Flow<List<ChatMessage>> = callbackFlow {
        val ref = database
            .getReference("chatRooms/$roomId/messages")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(ChatMessage::class.java)
                }.sortedBy { it.timestamp }

                trySend(messages).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        // Remove listener when the flow is closed
        awaitClose {
            ref.removeEventListener(listener)
        }
    }

    fun sendMessage(
        fromUserId: String,
        toUserId: String,
        messageText: String
    ) {
        val roomId = getChatRoomId(fromUserId, toUserId)
        val roomRef = database.getReference("chatRooms/$roomId")
        val messageRef = roomRef.child("messages").push()

        val message = mapOf(
            "senderId" to fromUserId,
            "text" to messageText,
            "timestamp" to System.currentTimeMillis()
        )

        // Check if the "users" node exists; set it only if missing
        roomRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    roomRef.child("users").setValue(listOf(fromUserId, toUserId))
                }
                messageRef.setValue(message)
            }

            override fun onCancelled(error: DatabaseError) {
                // Optional: handle error
            }
        })
    }
}

fun getChatRoomId(user1: String, user2: String): String {
    return listOf(user1, user2).sorted().joinToString("_")
}