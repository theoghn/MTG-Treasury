package com.theoghn.mtgtreasury.models


data class ChatMessage(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0
)
