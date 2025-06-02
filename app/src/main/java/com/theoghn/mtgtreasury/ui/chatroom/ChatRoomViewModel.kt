package com.theoghn.mtgtreasury.ui.chatroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoghn.mtgtreasury.models.ChatMessage
import com.theoghn.mtgtreasury.services.MessageService
import com.theoghn.mtgtreasury.services.UserService
import com.theoghn.mtgtreasury.services.getChatRoomId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    val messageService: MessageService,
    val userService: UserService
) : ViewModel() {
    val messagesFlow = MutableStateFlow<List<ChatMessage>>(emptyList())

    fun initialize(receiverId: String) {
        getMessages(receiverId)
    }

    fun sendMessage(receiverId: String, message: String) {
        val userId = userService.getUID()
        viewModelScope.launch {
            messageService.sendMessage(
                toUserId = receiverId,
                messageText = message,
                fromUserId = userId
            )
        }
    }

    fun getMessages(receiverId: String) {
        val userId = userService.getUID()
        viewModelScope.launch {
            messageService.listenForMessages(getChatRoomId(userId, receiverId))
                .collect { messages ->
                    messagesFlow.update { messages.sortedBy { it.timestamp }.reversed() }
                }
        }
    }

}