package com.mready.mtgtreasury.api.endpoints

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mready.mtgtreasury.models.User
import com.mready.mtgtreasury.utility.awaitOrNull
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserApi @Inject constructor() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun createAccount(email: String, password: String, username: String): FirebaseUser? {
        val user = auth.createUserWithEmailAndPassword(email, password).awaitOrNull()?.user
        val userId = user?.uid

        val userData = hashMapOf(
            "username" to username,
            "inventory" to listOf<String>(),
            "wishlist" to listOf<String>()
        )

        val x = FirebaseFirestore.getInstance().collection("users").document(userId!!)
            .set(userData).awaitOrNull()

        Log.d("UserApi", "createAccount: $x")
        return user
    }

    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password).awaitOrNull()?.user
    }

    suspend fun addCardToInventory(cardId: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("UserApi", "addCardToInventory: User not logged in")
            return
        }

        val userDoc = db.collection("users").document(userId)

        userDoc.update("inventory", FieldValue.arrayUnion(cardId)).awaitOrNull()
    }

    suspend fun removeCardFromInventory(cardId: String) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Log.e("UserApi", "removeCardFromInventory: User not logged in")
            return
        }

        val userDoc = db.collection("users").document(userId)

        userDoc.update("inventory", FieldValue.arrayRemove(cardId)).awaitOrNull()
    }

    suspend fun addCardToWishlist(cardId: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("UserApi", "addCardToInventory: User not logged in")
            return
        }

        val userDoc = db.collection("users").document(userId)
        userDoc.update("wishlist", FieldValue.arrayUnion(cardId)).awaitOrNull()
    }

    suspend fun removeCardFromWishlist(cardId: String) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Log.e("UserApi", "removeCardFromInventory: User not logged in")
            return
        }

        val userDoc = db.collection("users").document(userId)
        userDoc.update("wishlist", FieldValue.arrayRemove(cardId)).awaitOrNull()
    }
}