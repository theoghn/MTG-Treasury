package com.theoghn.mtgtreasury.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {
    val currentUID = MutableStateFlow("")

//    init {
//        viewModelScope.launch{
//            userService.getUserFlow().collect { user ->
//                if (user != null) {
//                    currentUID.update { user.id }
//                } else {
//                    currentUID.update { "" }
//                }
//            }
//        }
//    }

    fun initialize(){
        currentUID.update { userService.getUID() }
    }
}