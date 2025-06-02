package com.theoghn.mtgtreasury.services

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.theoghn.mtgtreasury.models.AppUser
import com.theoghn.mtgtreasury.models.ChatMessage
import com.theoghn.mtgtreasury.utility.awaitOrNull
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

//    fun sendMessage(
//        fromUserId: String,
//        toUserId: String,
//        messageText: String
//    ) {
//        val roomId = getChatRoomId(fromUserId, toUserId)
//        val roomRef = database.getReference("chatRooms/$roomId")
//        val messageRef = roomRef.child("messages").push()
//
//        val message = mapOf(
//            "senderId" to fromUserId,
//            "text" to messageText,
//            "timestamp" to System.currentTimeMillis()
//        )
//
//        // Check if the "users" node exists; set it only if missing
//        roomRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (!snapshot.exists()) {
//                    roomRef.child("users").setValue(listOf(fromUserId, toUserId))
//                }
//                messageRef.setValue(message)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Optional: handle error
//            }
//        })
//    }

    suspend fun sendMessage(
        fromUserId: String,
        toUserId: String,
        messageText: String
    ) {
        val roomId = getChatRoomId(fromUserId, toUserId)
        val messageRef = database
            .getReference("chatRooms/$roomId/messages")
            .push()

        val message = mapOf(
            "senderId" to fromUserId,
            "text" to messageText,
            "timestamp" to System.currentTimeMillis()
        )

        messageRef.setValue(message)

        // Update chatsWith list for both users
        updateUserChatsWith(fromUserId, toUserId)
        updateUserChatsWith(toUserId, fromUserId)
    }


    private suspend fun updateUserChatsWith(userId: String, otherUserId: String) {
        val userDoc = FirebaseFirestore.getInstance().collection("users").document(userId)
        userDoc.update("chatsWith", FieldValue.arrayUnion(otherUserId)).awaitOrNull()
    }

    fun getUserChatPartners(userId: String): Flow<List<String>> = callbackFlow {
        val ref = FirebaseFirestore.getInstance().collection("users").document(userId)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                close(error ?: Exception("Unknown Firestore error"))
                return@addSnapshotListener
            }

            val user = snapshot.toObject(AppUser::class.java)
            trySend(user?.chatsWith ?: emptyList()).isSuccess
        }

        awaitClose { listener.remove() }
    }

}

fun getChatRoomId(user1: String, user2: String): String {
    return listOf(user1, user2).sorted().joinToString("_")
}