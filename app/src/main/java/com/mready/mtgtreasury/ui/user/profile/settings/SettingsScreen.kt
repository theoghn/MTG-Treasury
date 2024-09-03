package com.mready.mtgtreasury.ui.user.profile.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.decks.view.RemoveAlert


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onSignOut: () -> Unit,
    onBack: () -> Unit,
    navigateToProfileUpdate: (String) -> Unit
) {
    val user by viewModel.user.collectAsState()
    val email by viewModel.email.collectAsState()

    val scrollState = rememberScrollState()

    var isLogOutDialogVisible by rememberSaveable { mutableStateOf(false) }
    var isDeleteAccountDialogVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getUser()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
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
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.account_settings),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        },
        containerColor = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navigateToProfileUpdate("username") },
            ) {
                Text(
                    text = stringResource(id = R.string.username),
                    fontSize = 16.sp,
                    color = Color.White
                )

                Text(
                    text = user.username,
                    fontSize = 16.sp,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navigateToProfileUpdate("bio") },
            ) {
                Text(
                    text = stringResource(R.string.bio),
                    fontSize = 16.sp,
                    color = Color.White
                )

                Text(
                    text = user.bio.ifEmpty { "Add a bio" },
                    fontSize = 16.sp,
                    color = Color.LightGray
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Email",
                fontSize = 16.sp,
                color = Color.White
            )

            Text(
                text = email,
                fontSize = 16.sp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                modifier = Modifier
                    .clickable { isLogOutDialogVisible = true },
                text = "Log out",
                fontSize = 16.sp,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .clickable { isDeleteAccountDialogVisible = true },
                text = "Delete Account",
                fontSize = 16.sp,
                color = Color.Red
            )


        }
    }


    if (isLogOutDialogVisible) {
        RemoveAlert(
            title = stringResource(R.string.warning),
            message = stringResource(R.string.are_you_sure_you_want_to_log_out),
            confirmText = stringResource(R.string.log_out),
            hideAlert = { isLogOutDialogVisible = false },
            onConfirm = {
                viewModel.signOut()
                onSignOut()
            }
        )
    }

    if (isDeleteAccountDialogVisible) {
        RemoveAlert(
            title = stringResource(R.string.warning),
            message = stringResource(R.string.are_you_sure_you_want_to_delete_your_account),
            hideAlert = { isDeleteAccountDialogVisible = false },
            onConfirm = {
                viewModel.signOut()
                onSignOut()
            }
        )
    }
}