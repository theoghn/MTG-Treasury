package com.mready.mtgtreasury.api.endpoints

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.mready.mtgtreasury.models.User
import com.mready.mtgtreasury.utility.awaitOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserApi @Inject constructor() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val isUserPresent = MutableStateFlow(false)

    init {
        auth.addAuthStateListener {
            if (it.currentUser == null) {
                Log.d("UserApi", "init: User not present")
                isUserPresent.update { false }
            } else {
                Log.d("UserApi", "init: User present")
                isUserPresent.update { true }
            }
        }
    }

    fun isUserPresent(): StateFlow<Boolean> {
        return isUserPresent
    }

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


}

