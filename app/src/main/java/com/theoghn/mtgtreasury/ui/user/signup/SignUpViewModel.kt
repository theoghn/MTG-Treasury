package com.theoghn.mtgtreasury.ui.user.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(private val service: UserService) : ViewModel() {
    val exception = MutableStateFlow("")
    val loading = MutableStateFlow(false)

    fun createAccount(
        email: String,
        password: String,
        passwordConfirmation: String,
        username: String
    ) {
        loading.update { true }
        exception.update { "" }

        if (passwordConfirmation != password || password.length < 6) {
            loading.update { false }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loading.update { false }
            return
        }

        if (email.isEmpty() || username.length < 5) {
            loading.update { false }
            return
        }

        viewModelScope.launch {
            try {
                service.createAccount(email, password, username)
            } catch (e: Exception) {
                if (e.message?.contains("network") == true) {
                    exception.update { "No internet connection." }
                } else {
                    exception.update { "Something went wrong." }
                }
            }
            loading.update { false }
        }

    }
}