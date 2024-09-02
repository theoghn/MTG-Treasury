package com.mready.mtgtreasury.ui.user.profile.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.models.AppUser
import com.mready.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    val user = MutableStateFlow(AppUser())
    val email = MutableStateFlow("")

    fun getUser() {
        viewModelScope.launch {
            val pair = userService.getUser()

            pair.first?.let { it1 ->
                user.update { it1 }
            }
            pair.second?.let { it1 ->
                email.update { it1 }
            }
        }
    }

    fun signOut() {
        userService.signOut()
    }

    fun deleteAccount() {
        userService.deleteUser()
    }
}