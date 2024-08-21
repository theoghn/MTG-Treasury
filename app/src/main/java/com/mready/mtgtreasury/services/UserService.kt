package com.mready.mtgtreasury.services

import com.mready.mtgtreasury.api.endpoints.ScryfallApi
import com.mready.mtgtreasury.api.endpoints.UserApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService @Inject constructor(
    private val userClient: UserApi
) {
    suspend fun createAccount(email: String, password: String, username: String) = userClient.createAccount(email, password, username)

    suspend fun signIn(email: String, password: String) = userClient.signIn(email, password)
}