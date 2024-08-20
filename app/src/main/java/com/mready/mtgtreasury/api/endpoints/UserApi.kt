package com.mready.mtgtreasury.api.endpoints

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mready.mtgtreasury.api.ScryfallApiClient
import com.mready.mtgtreasury.utility.awaitOrNull
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserApi @Inject constructor(
    private val auth: FirebaseAuth
){
    suspend fun createAccount(email: String, password: String) : FirebaseUser? {
        return auth.createUserWithEmailAndPassword(email, password).awaitOrNull()?.user
    }

    suspend fun signIn(email: String, password: String) : FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password).awaitOrNull()?.user
    }
}