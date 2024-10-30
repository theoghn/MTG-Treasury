package com.theoghn.mtgtreasury.services

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.theoghn.mtgtreasury.utility.await
import javax.inject.Singleton
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.theoghn.mtgtreasury.models.AppUser
import com.theoghn.mtgtreasury.models.Deck
import javax.inject.Inject

@Singleton
class ExternalUserService @Inject constructor() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun getUserInfo(userId: String): AppUser {
        return db.collection("users").document(userId).get().await().toObject<AppUser>()!!
    }

    suspend fun getUserDecks(userId: String): List<Deck> {
        return db
            .collection("decks")
            .whereEqualTo("uid", userId)
            .get()
            .await()
            .toObjects<Deck>()
    }

    suspend fun getUserWishlist(userId: String): List<String> {
        return db.collection("users").document(userId).get().await().toObject<AppUser>()!!.wishlist
    }

    suspend fun getUserInventory(userId: String): HashMap<String, Int>{
        return db.collection("users").document(userId).get().await().toObject<AppUser>()!!.inventory
    }

}
