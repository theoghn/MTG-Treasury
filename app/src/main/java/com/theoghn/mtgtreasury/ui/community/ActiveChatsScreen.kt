package com.theoghn.mtgtreasury.ui.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theoghn.mtgtreasury.R
import com.theoghn.mtgtreasury.ui.theme.BoxColor
import com.theoghn.mtgtreasury.utility.getProfilePictureResourceId

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveChatsScreen(
    viewModel: ActiveChatsViewModel = hiltViewModel(),
    onNavigateToChatRoom: (String, String) -> Unit,
    onNavigateToCommunity: () -> Unit
) {
    val activeChats by viewModel.activeChatsFlow.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

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
                        text = stringResource(R.string.community_chats),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is ActiveChatsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier,
                        color = Color.White
                    )
                }

                is ActiveChatsUiState.Empty -> {
                    Text(
                        text = stringResource(R.string.chat_empty_state),
                        color = Color.White
                    )
                }

                is ActiveChatsUiState.ActiveChatsUi -> {
                    FlowColumn(
                        modifier = Modifier.align(Alignment.TopCenter),

                        ) {
                        activeChats.forEach { user ->
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .height(84.dp)
                                    .fillMaxWidth(),
                                onClick = { onNavigateToChatRoom(user.id, user.username) },
                                colors = CardDefaults.cardColors(
                                    containerColor = BoxColor,
                                    contentColor = BoxColor
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Image(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .size(60.dp)
                                            .aspectRatio(1f)
                                            .clip(CircleShape),
                                        painter = painterResource(
                                            id = getProfilePictureResourceId(
                                                user.pictureId
                                            )
                                        ),
                                        contentScale = ContentScale.Crop,
                                        contentDescription = null
                                    )

                                    Text(
                                        modifier = Modifier
                                            .padding(start = 16.dp),
                                        text = user.username,
                                        color = Color.White,
                                        fontSize = 24.sp
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Handle other states if necessary
                }
            }
        }


    }


}