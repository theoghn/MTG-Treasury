package com.mready.mtgtreasury.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mready.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(private val service: UserService) : ViewModel() {
    fun signOut() {
        viewModelScope.launch {
            service.signOut()
        }
    }
}