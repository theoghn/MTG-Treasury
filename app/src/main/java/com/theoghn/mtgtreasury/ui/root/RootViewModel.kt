package com.theoghn.mtgtreasury.ui.root

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.theoghn.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    val uiState = MutableStateFlow<RootUiState?>(null)

    init {
        if (userService.getUserState()) {
            uiState.value = RootUiState.MainApp
        } else {
            uiState.value = RootUiState.Authentication
        }

        viewModelScope.launch {
            userService.isUserPresent().collect {
                if (it == true) {
                    uiState.update { RootUiState.MainApp }
                }
                if(it == false) {
                    uiState.update { RootUiState.Authentication }
                }
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                Log.d("TOKEN", "Fetching")

                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d(TAG, token)
            Log.d("TOKEN", token)
        })
    }

    fun signOut() {
        viewModelScope.launch {
            userService.signOut()
        }
    }
}

sealed class RootUiState {
    data object MainApp : RootUiState()

    data object Authentication : RootUiState()
}