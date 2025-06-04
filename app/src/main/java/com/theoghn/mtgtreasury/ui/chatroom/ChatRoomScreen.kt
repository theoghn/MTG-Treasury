package com.theoghn.mtgtreasury.ui.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theoghn.mtgtreasury.R
import com.theoghn.mtgtreasury.models.ChatMessage
import com.theoghn.mtgtreasury.ui.components.PrimaryButton
import com.theoghn.mtgtreasury.ui.theme.AccentColor
import com.theoghn.mtgtreasury.ui.theme.BoxColor
import com.theoghn.mtgtreasury.ui.theme.LightBlue
import com.theoghn.mtgtreasury.ui.theme.MainBackgroundColor

@Composable
fun ChatRoomScreen(
    modifier: Modifier = Modifier,
    receiverId: String,
    receiverUsername: String,
    viewModel: ChatRoomViewModel = hiltViewModel(),
    onBack: () -> Boolean
) {
    var message by remember { mutableStateOf("") }
    val chatMessages by viewModel.messagesFlow.collectAsState()

    LaunchedEffect(receiverId) {
        viewModel.initialize(receiverId)
    }

    Box(
        modifier = Modifier
//            .statusBarsPadding()
//            .navigationBarsPadding()
//            .verticalScroll(rememberScrollState())
            .imePadding()
            .fillMaxSize(),
    ) {
        ChatScreen(
            chatMessages = chatMessages,
            receiverId = receiverId
        )

        MessageInputBox(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .imePadding(),
            message = message,
            onMessageChange = { newMessage ->
                message = newMessage
            },
            onSendClick = {
                if (message.isNotBlank()) {
                    viewModel.sendMessage(message = message, receiverId = receiverId)
                    message = ""
                }
            }
        )

        ChatRoomHeader(
            onBack = onBack,
            receiverUsername = receiverUsername
        )
    }


}

@Composable
fun MessageInputBox(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(100)),
            placeholder = { Text(stringResource(R.string.chat_type_message)) },
            maxLines = 3,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = BoxColor,
                unfocusedContainerColor = BoxColor
            ),
            trailingIcon = {
                if (message.isNotBlank()) {
                    PrimaryButton(
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .clip(CircleShape)
                            .background(AccentColor),
                        onClick = {
                            onSendClick()
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 4.dp, bottom = 4.dp)
                                .graphicsLayer(
                                    rotationZ = -30f // negative = tilted up-left
                                ),
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.chat_send),
                            tint = Color.White
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun ChatScreen(chatMessages: List<ChatMessage>, receiverId: String) {
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 68.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 10.dp),
        reverseLayout = true,
    ) {
        items(chatMessages) { message ->
            Row(
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .fillMaxWidth(),
                horizontalArrangement = if (message.senderId == receiverId) {
                    Arrangement.Start
                } else {
                    Arrangement.End
                }
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(
                            max = LocalConfiguration.current.screenWidthDp.dp * 0.7f,
                            min = 30.dp
                        )
                        .background(
                            if (message.senderId == receiverId) {
                                AccentColor
                            } else {
                                LightBlue
                            }, RoundedCornerShape(20.dp)
                        )
                ) {
                    Text(
                        text = message.text,
                        modifier = Modifier
                            .padding(10.dp)
                            .defaultMinSize(minWidth = 24.dp),
                        color = Color.White,
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun ChatRoomHeader(
    onBack: () -> Boolean,
    receiverUsername: String
) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .background(MainBackgroundColor),
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = { onBack() }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.Center),
            text = receiverUsername,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}