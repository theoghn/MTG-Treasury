package com.mready.mtgtreasury.ui.user.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.user.signin.BaseTextField
import com.mready.mtgtreasury.ui.user.signin.PasswordField
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.components.TwoColorText
import com.mready.mtgtreasury.ui.theme.AccentColor

@Composable
fun SingUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToSingIn: () -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var passwordConfirmation by rememberSaveable { mutableStateOf("") }

    var showUsernameError by rememberSaveable { mutableStateOf(false) }
    var showPasswordError by rememberSaveable { mutableStateOf(false) }
    var showConfirmPasswordError by rememberSaveable { mutableStateOf(false) }
    var showEmailError by rememberSaveable { mutableStateOf(false) }


    val exception by viewModel.exception.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .align(Alignment.TopStart)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onNavigateToSingIn() },
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White,
        )

        Column(
            modifier = Modifier
                .imePadding()
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.sign_up_title),
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )



            Text(
                modifier = Modifier.align(Alignment.Start),
                text = exception,
                color = Color.Red
            )

            BaseTextField(
                fieldValue = username,
                placeholderText = stringResource(R.string.username),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                onValueChange = {
                    username = it.replace("\n", "")
                    if (showUsernameError) {
                        showUsernameError = false
                    }
                },
                maxLength = 20,
                isError = if (showUsernameError) username.length < 5 else false,
                errorMessage = stringResource(R.string.username_min_length_warning)
            )

            BaseTextField(
                fieldValue = email,
                placeholderText = stringResource(id = R.string.email),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                onValueChange = {
                    email = it.replace("\n", "")
                    if (showEmailError) {
                        showEmailError = false
                    }
                },
                isError = if (showEmailError) email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(
                    email
                ).matches() else false,
                errorMessage = if (email.isEmpty()) {
                    stringResource(id = R.string.email_is_required)
                } else {
                    stringResource(R.string.email_is_invalid)
                }
            )

            PasswordField(
                fieldValue = password,
                placeholderText = stringResource(id = R.string.password),
                onValueChange = {
                    password = it.replace("\n", "")
                    if (showPasswordError) {
                        showPasswordError = false
                    }
                },
                isError = if (showPasswordError) password != passwordConfirmation || password.length < 6 else false,
                errorMessage = if (passwordConfirmation != password) {
                    stringResource(R.string.passwords_must_match)
                } else {
                    stringResource(R.string.password_min_length_warning)
                }
            )

            PasswordField(
                fieldValue = passwordConfirmation,
                placeholderText = stringResource(R.string.confirm_password),
                onValueChange = {
                    passwordConfirmation = it.replace("\n", "")
                    if (showConfirmPasswordError) {
                        showConfirmPasswordError = false
                    }
                },
                isError = if (showConfirmPasswordError) password != passwordConfirmation || passwordConfirmation.length < 6 else false,
                errorMessage = if (passwordConfirmation != password) {
                    stringResource(R.string.passwords_must_match)
                } else {
                    stringResource(R.string.password_min_length_warning)
                }
            )



            PrimaryButton(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onClick = {
                    viewModel.createAccount(email, password, passwordConfirmation, username)
                    showEmailError = true
                    showPasswordError = true
                    showUsernameError = true
                    showConfirmPasswordError = true
                }
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(28.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = stringResource(R.string.sign_up),
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }


            TwoColorText(
                modifier = Modifier.clickable { onNavigateToSingIn() },
                firstPart = stringResource(R.string.already_have_an_account),
                secondPart = stringResource(R.string.login),
                fontSize = 14.sp
            )
        }
    }
}