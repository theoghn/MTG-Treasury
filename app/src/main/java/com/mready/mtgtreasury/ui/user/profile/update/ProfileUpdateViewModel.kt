package com.mready.mtgtreasury.ui.user.profile.update

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
class ProfileUpdateViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    val user = MutableStateFlow(AppUser())
    val email = MutableStateFlow("")

    init {
        getUser()
    }

    private fun getUser() {
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

    fun updateBio(bio: String) {
        userService.updateBio(bio)
    }

    fun updateUsername(username: String): Boolean {
        if (username.length < 5) return false
        userService.updateUsername(username)
        return true
    }
}