package com.mready.mtgtreasury.ui.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser

import com.mready.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(private val service: UserService) : ViewModel() {
    val exception = MutableStateFlow<String>("")
    val user = MutableStateFlow<FirebaseUser?>(null)

    fun createAccount(
        email: String,
        password: String,
        passwordConfirmation: String,
        username: String
    ) {
        exception.update { "" }

        if (passwordConfirmation != password) {
            exception.update { "Passwords must match!" }
            return
        }

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            exception.update { "Email, password and username cannot be empty!" }
            return
        }

        viewModelScope.launch {
            try {
                user.update { service.createAccount(email, password, username) }
            } catch (e: Exception) {
                exception.update { e.message.toString() }
            }
        }
    }
}