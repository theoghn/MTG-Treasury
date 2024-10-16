package com.mready.mtgtreasury.services

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.mready.mtgtreasury.utility.await
import javax.inject.Singleton
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.mready.mtgtreasury.models.AppUser
import com.mready.mtgtreasury.models.Deck
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

}
