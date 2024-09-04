package com.mready.mtgtreasury.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.services.UserService
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