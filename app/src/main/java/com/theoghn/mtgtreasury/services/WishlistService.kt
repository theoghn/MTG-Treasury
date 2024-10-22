package com.theoghn.mtgtreasury.services

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.theoghn.mtgtreasury.models.AppUser
import com.theoghn.mtgtreasury.utility.awaitOrNull
import com.theoghn.mtgtreasury.utility.requireUserId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishlistService @Inject constructor() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore


    suspend fun addCardToWishlist(cardId: String) {
        val userId = auth.requireUserId

        val userDoc = db.collection("users").document(userId)
        userDoc.update("wishlist", FieldValue.arrayUnion(cardId)).awaitOrNull()
    }

    suspend fun removeCardFromWishlist(cardId: String) {
        val userId = auth.requireUserId

        val userDoc = db.collection("users").document(userId)
        userDoc.update("wishlist", FieldValue.arrayRemove(cardId)).awaitOrNull()
    }

    suspend fun getWishlistFlow(): Flow<List<String>> {
        val userId = auth.requireUserId

        return callbackFlow {
            val listener =
                db.collection("users").document(userId).addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                    }

                    trySendBlocking((value!!.toObject<AppUser>()!!.wishlist))
                }

            awaitClose {
                listener.remove()
            }
        }
    }
}