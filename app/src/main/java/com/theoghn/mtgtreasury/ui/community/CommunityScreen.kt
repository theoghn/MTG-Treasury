package com.theoghn.mtgtreasury.ui.community

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theoghn.mtgtreasury.ui.cardslist.SearchTextField
import com.theoghn.mtgtreasury.ui.theme.BoxColor

@Composable
fun CommunityScreen(
    modifier: Modifier = Modifier,
    viewModel: CommunityViewModel = hiltViewModel(),
    onNavigateToProfile: (String) -> Unit,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

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
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = {
//                            onBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Community",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }

                SearchTextField(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 2.dp),
                    fieldValue = searchQuery,
                    showBorder = false,
                    color = BoxColor,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    placeholderText = "Search Users",
                    onValueChange = {
                        viewModel.updateSearchQuery(it)
                    },
                    onSearch = {
                        viewModel.onSearchQueryChange()
                        keyboardController?.hide()
                    }
                )
            }

        },
        containerColor = Color.Transparent
    ) { it ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (uiState) {
                is CommunityScreenUiState.CommunityUi -> {
                    val users = (uiState as CommunityScreenUiState.CommunityUi).users
                    LazyColumn {
                        items(users) { user ->
                            Card(
                                modifier = modifier
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                                    .height(100.dp)
                                    .fillMaxWidth(),
                                onClick = {onNavigateToProfile(user.id) },
                                colors = CardDefaults.cardColors(
                                    containerColor = BoxColor,
                                    contentColor = BoxColor
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = user.username,
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                is CommunityScreenUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center),
                        color = Color.White
                    )
                }

                is CommunityScreenUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No users found",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                is CommunityScreenUiState.Uninitialized -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Search for users to see their profiles",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}