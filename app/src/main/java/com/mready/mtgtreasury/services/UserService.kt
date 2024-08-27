package com.mready.mtgtreasury.services

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.mready.mtgtreasury.models.AppUser
import com.mready.mtgtreasury.utility.await
import com.mready.mtgtreasury.utility.awaitOrNull
import com.mready.mtgtreasury.utility.requireUser
import com.mready.mtgtreasury.utility.requireUserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserService @Inject constructor() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val isUserPresent = MutableStateFlow<Boolean?>(null)

    init {
        CoroutineScope(Dispatchers.IO).launch {
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
    }

    fun isUserPresent(): StateFlow<Boolean?> {
        return isUserPresent
    }

    fun getUserState(): Boolean {
        return Firebase.auth.currentUser != null
    }

    suspend fun getUserFlow() : Flow<AppUser?> {
        return callbackFlow {
            val listener =
                db.collection("users").document(auth.requireUserId).addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                    }
                    trySendBlocking((value!!.toObject<AppUser>()))
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    suspend fun createAccount(email: String, password: String, username: String): FirebaseUser {
        val user = auth.createUserWithEmailAndPassword(email, password).awaitOrNull()?.user
        val userId = auth.requireUserId

        val userData = hashMapOf(
            "username" to username,
            "inventory" to hashMapOf<String, Int>(),
            "wishlist" to listOf<String>(),
            "inventoryValue" to 0.0
        )

        db.collection("users").document(userId).set(userData).awaitOrNull()

        return user!!
    }

    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password).awaitOrNull()?.user
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun updateInventoryValue(value: Double) {
        val userId = auth.requireUserId

        db.collection("users").document(userId).update("inventoryValue", value).awaitOrNull()
    }
}

