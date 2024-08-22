package com.mready.mtgtreasury.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mready.mtgtreasury.api.endpoints.UserApi
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.InventoryService
import com.mready.mtgtreasury.services.WishlistService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val userApi: UserApi
) : ViewModel() {
    val isUserLoggedIn = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            userApi.isUserPresent().collect {
                isUserLoggedIn.value = it
            }
        }
    }
}