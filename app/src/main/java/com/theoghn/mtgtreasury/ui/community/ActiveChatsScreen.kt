package com.theoghn.mtgtreasury.ui.community

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveChatsScreen(
    viewModel: ActiveChatsViewModel = hiltViewModel(),
    onNavigateToChatRoom: (String, String) -> Unit,
    onNavigateToCommunity: () -> Unit
) {
    val activeChats = viewModel.activeChatsFlow.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Your Chats",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )

                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                            onNavigateToCommunity()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) {
        FlowColumn(
            Modifier.padding(it)
        ) {
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



}