package com.mready.mtgtreasury.ui.user.settings

import androidx.lifecycle.ViewModel
import com.mready.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    fun signOut() {
        userService.signOut()
    }
}