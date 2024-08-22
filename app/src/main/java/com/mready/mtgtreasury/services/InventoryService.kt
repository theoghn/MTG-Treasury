package com.mready.mtgtreasury.services

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.mready.mtgtreasury.models.User
import com.mready.mtgtreasury.utility.awaitOrNull
import com.mready.mtgtreasury.utility.requireUserId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class InventoryService @Inject constructor() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore


    suspend fun addCardToInventory(cardId: String) {
        val userId = auth.requireUserId

        val userDoc = db.collection("users").document(userId)
        userDoc.update("inventory", FieldValue.arrayUnion(cardId)).awaitOrNull()
    }

    suspend fun removeCardFromInventory(cardId: String) {
        val userId = auth.requireUserId

        val userDoc = db.collection("users").document(userId)
        userDoc.update("inventory", FieldValue.arrayRemove(cardId)).awaitOrNull()
    }

    suspend fun getInventory(): Flow<List<String>> {
        val userId = auth.requireUserId

        return callbackFlow {
            val listener =
                db.collection("users").document(userId).addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                    }
                    Log.d("InventoryService", "getInventory: ${value?.toObject<User>()?.inventory?.size}")
                    trySendBlocking((value!!.toObject<User>()!!.inventory))
                }

            awaitClose {
                listener.remove()
            }
        }
    }
}