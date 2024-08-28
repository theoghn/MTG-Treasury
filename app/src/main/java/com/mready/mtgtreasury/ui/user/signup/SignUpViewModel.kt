package com.mready.mtgtreasury.ui.user.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(private val service: UserService) : ViewModel() {
    val exception = MutableStateFlow("")
    val loading  = MutableStateFlow(false)

    fun createAccount(
        email: String,
        password: String,
        passwordConfirmation: String,
        username: String
    ) {
        loading.update { true }
        exception.update { "" }

        if (passwordConfirmation != password) {
            exception.update { "Passwords must match!" }
            loading.update { false }
            return
        }

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            exception.update { "Email, password and username cannot be empty!" }
            loading.update { false }
            return
        }

        viewModelScope.launch {
            try {
                service.createAccount(email, password, username)
            } catch (e: Exception) {
                exception.update { e.message.toString() }
            }
            loading.update { false }
        }
    }
}