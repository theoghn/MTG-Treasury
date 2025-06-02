package com.theoghn.mtgtreasury.ui.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveChatsScreen(
    viewModel: ActiveChatsViewModel = hiltViewModel(),
    onNavigateToChatRoom: (String, String) -> Unit
) {
    val activeChats = viewModel.activeChatsFlow.collectAsState()

    FlowColumn {
        activeChats.value.forEach { user ->
            Row {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .clickable {
                            onNavigateToChatRoom(user.id, user.username)
                        },
                    text = user.username,
                    color = Color.White
                )
            }
        }
    }
}