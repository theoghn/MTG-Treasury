package com.theoghn.mtgtreasury.services

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.theoghn.mtgtreasury.models.AppUser
import com.theoghn.mtgtreasury.utility.awaitOrNull
import com.theoghn.mtgtreasury.utility.requireUser
import com.theoghn.mtgtreasury.utility.requireUserId
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

    suspend fun getUserFlow(): Flow<AppUser?> {
        return callbackFlow {
            val listener =
                db.collection("users").document(auth.requireUserId)
                    .addSnapshotListener { value, error ->
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

        val userData = AppUser(id = userId, username = username)

        db.collection("users").document(userId).set(userData).awaitOrNull()

        return user!!
    }

    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password).awaitOrNull()?.user
    }

    fun signOut() {
        auth.signOut()
    }

    fun deleteUser(){
        val userId = auth.requireUserId
        auth.requireUser.delete().addOnCompleteListener {
            Log.d("UserService", "deleteUser: $userId")
            db.collection("users").document(userId).delete()
        }.onSuccessTask {
            Log.d("UserService", "deleteUser2: $userId")
            db.collection("users").document(userId).delete()
        }
    }

    suspend fun getUserAndEmail(): Pair<AppUser?, String?> {
        val userId = auth.requireUserId

        Log.d("UserService", "getUser: ${auth.requireUser.email}")
        return Pair(
            db.collection("users").document(userId).get().awaitOrNull()?.toObject<AppUser>(),
            auth.requireUser.email
        )
    }

    fun getUID():String{
        return auth.requireUserId
    }

    suspend fun updateInventoryValue(value: Double) {
        val userId = auth.requireUserId

        db.collection("users").document(userId).update("inventoryValue", value).awaitOrNull()
    }

    fun updateUsername(username: String) {
        val userId = auth.requireUserId

        db.collection("users").document(userId).update("username", username)
    }

    fun updateBio(bio: String) {
        val userId = auth.requireUserId

        db.collection("users").document(userId).update("bio", bio)
    }

    fun updateProfilePictureId(profilePictureId: Int){
        val userId = auth.requireUserId

        db.collection("users").document(userId).update("pictureId", profilePictureId)
    }
}