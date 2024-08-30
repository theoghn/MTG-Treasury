package com.mready.mtgtreasury.ui.user.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(private val service: UserService) : ViewModel() {
    val exception = MutableStateFlow<String>("")
    val loading = MutableStateFlow(false)


    fun signIn(email: String, password: String) {
        loading.update { true }
        exception.update { "" }

        if (email.isEmpty() || password.isEmpty()) {
            loading.update { false }
            return
        }

        viewModelScope.launch {
            try {
                service.signIn(email, password)
            } catch (e: Exception) {
                if (e.message?.contains("server") == true) {
                    exception.update { "No internet connection." }
                } else {
                    exception.update { "Email and password do not match." }
                }
            }
            loading.update { false }
        }
    }
}