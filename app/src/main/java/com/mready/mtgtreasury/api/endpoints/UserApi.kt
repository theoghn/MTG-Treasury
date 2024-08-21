package com.mready.mtgtreasury.api.endpoints

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.mready.mtgtreasury.utility.awaitOrNull
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserApi @Inject constructor() {
    private val auth = Firebase.auth

    suspend fun createAccount(email: String, password: String, username: String): FirebaseUser? {
        val user = auth.createUserWithEmailAndPassword(email, password).awaitOrNull()?.user
        val userId = user?.uid

        val userData = hashMapOf(
            "username" to username,
        )


        val x = FirebaseFirestore.getInstance().collection("users").document(userId!!)
            .set(userData).awaitOrNull()

        Log.d("UserApi", "createAccount: $x")
        return user
    }

    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password).awaitOrNull()?.user
    }
}