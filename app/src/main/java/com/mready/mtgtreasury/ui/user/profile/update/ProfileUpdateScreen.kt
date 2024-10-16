package com.mready.mtgtreasury.ui.user.profile.update

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.user.signin.BaseTextField

@Composable
fun ProfileUpdateScreen(
    viewModel: ProfileUpdateViewModel = hiltViewModel(),
    updateType: String,
    onBack: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val email by viewModel.email.collectAsState()

    var showUsernameError by rememberSaveable { mutableStateOf(false) }
    var newValue by rememberSaveable {
        mutableStateOf("")
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(user) {
        when (updateType) {
            "username" -> {
                newValue = user.username
            }

            "bio" -> {
                newValue = user.bio
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = {
                        keyboardController?.hide()
                        onBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = if (updateType == "username") "Username" else "Bio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )

                IconButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                        when (updateType) {
                            "username" -> {
                                showUsernameError = true
                                if (newValue.length < 5) return@IconButton
                                viewModel.updateUsername(newValue)
                            }

                            "bio" -> viewModel.updateBio(newValue)
                        }
                        keyboardController?.hide()
                        onBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = AccentColor
                    )
                }
            }
        },
        containerColor = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            when (updateType) {
                "username" -> {
                    UsernameUpdateContent(
                        newValue = newValue,
                        onValueChange = { it2 -> newValue = it2 },
                        showUsernameError = showUsernameError,
                        updateUsernameError = { it2: Boolean -> showUsernameError = it2 }
                    )
                }

                "bio" -> {
                    BioUpdateContent(
                        newValue = newValue,
                        onValueChange = { it2 -> newValue = it2 })
                }
            }
        }
    }
}

@Composable
fun UsernameUpdateContent(
    newValue: String,
    onValueChange: (String) -> Unit,
    showUsernameError: Boolean,
    updateUsernameError: (Boolean) -> Unit
) {

    BaseTextField(
        fieldValue = newValue,
        placeholderText = stringResource(R.string.username),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        onValueChange = {
            onValueChange(it)
            if (showUsernameError) {
                updateUsernameError(false)
            }
        },
        maxLength = 20,
        isError = if (showUsernameError) newValue.length < 5 else false,
        errorMessage = stringResource(R.string.username_min_length_warning)
    )
}

@Composable
fun BioUpdateContent(newValue: String, onValueChange: (String) -> Unit) {
    BaseTextField(
        fieldValue = newValue,
        placeholderText = stringResource(R.string.bio),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        onValueChange = {
            onValueChange(it.replace("\n", ""))
        },
        singleLine = false,
        minLines = 1,
        maxLines = 5,
        maxLength = 150,
        isError = false,
        errorMessage = ""
    )
}

