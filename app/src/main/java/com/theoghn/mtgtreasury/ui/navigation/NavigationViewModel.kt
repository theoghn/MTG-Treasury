package com.theoghn.mtgtreasury.ui.navigation

import androidx.lifecycle.ViewModel
import com.theoghn.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    val currentUID = MutableStateFlow("")

    init {
        currentUID.update { userService.getUID() }
    }
}