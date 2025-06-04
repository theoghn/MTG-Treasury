package com.theoghn.mtgtreasury.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.models.AppUser
import com.theoghn.mtgtreasury.services.ExternalUserService
import com.theoghn.mtgtreasury.services.MessageService
import com.theoghn.mtgtreasury.services.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ActiveChatsViewModel @Inject constructor(
    private val userService: UserService,
    private val externalUserService: ExternalUserService,
    private val messageService: MessageService
) : ViewModel() {
    val activeChatsFlow = MutableStateFlow(emptyList<AppUser>())
    val uiState = MutableStateFlow<ActiveChatsUiState>(ActiveChatsUiState.Uninitialized)

    init {
        viewModelScope.launch {
            uiState.value = ActiveChatsUiState.Loading
            messageService.getUserChatPartners(userService.getUID()).collect { chats ->
                // Transform the list of AppUser into a list of lists of AppUser
                val transformedChats = chats.map { userId ->
                    externalUserService.getUserInfo(userId)
                }
                activeChatsFlow.value = transformedChats

                if (transformedChats.isEmpty()) {
                    uiState.value = ActiveChatsUiState.Empty
                } else {
                    uiState.value = ActiveChatsUiState.ActiveChatsUi(transformedChats)
                }
            }
        }
    }

}

sealed class ActiveChatsUiState {
    data class ActiveChatsUi(
        val users: List<AppUser>
    ) : ActiveChatsUiState()

    data object Loading : ActiveChatsUiState()
    data object Uninitialized : ActiveChatsUiState()
    data object Empty : ActiveChatsUiState()
}