package com.theoghn.mtgtreasury.ui.chatroom

import kotlinx.serialization.Serializable


@Serializable
class ChatRoomDestination(val receiverId: String,val receiverUsername: String)