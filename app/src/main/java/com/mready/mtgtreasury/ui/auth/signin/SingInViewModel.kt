package com.mready.mtgtreasury.ui.auth.signin

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
class SignInViewModel @Inject constructor(private val service: UserService) : ViewModel() {
    val exception = MutableStateFlow<String>("")
    val user = MutableStateFlow<FirebaseUser?>(null)

    fun signIn(email: String, password: String) {
        exception.update { "" }

        if (email.isEmpty() || password.isEmpty()) {
            exception.update { "Email and password cannot be empty" }
        }

        viewModelScope.launch {
            try {
                user.update { service.signIn(email, password) }
            } catch (e: Exception) {
                exception.update { e.message.toString() }
            }
        }
    }
}