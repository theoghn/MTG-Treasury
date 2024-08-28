package com.mready.mtgtreasury.ui.user.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mready.mtgtreasury.ui.user.signin.BaseTextField
import com.mready.mtgtreasury.ui.user.signin.PasswordField
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.components.TwoColorText

@Composable
fun SingUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    onNavigateToSingIn: () -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var passwordConfirmation by rememberSaveable { mutableStateOf("") }

    val exception by viewModel.exception.collectAsState()
    val loading by viewModel.loading.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Sign up.",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            if (loading) {
                CircularProgressIndicator()
            }

            Text(
                modifier = Modifier.align(Alignment.Start),
                text = exception,
                color = Color.Red
            )

            BaseTextField(
                fieldValue = username,
                placeholderText = "Username",
                onValueChange = {
                    username = it
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            BaseTextField(
                fieldValue = email,
                placeholderText = "Email",
                onValueChange = {
                    email = it
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            PasswordField(
                fieldValue = password,
                placeholderText = "Password",
                onValueChange = {
                    password = it
                }
            )

            PasswordField(
                fieldValue = passwordConfirmation,
                placeholderText = "Confirm password",
                onValueChange = {
                    passwordConfirmation = it
                },
            )


            PrimaryButton(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onClick = {
                    viewModel.createAccount(email, password, passwordConfirmation, username)
                }
            ) {
                Text(
                    text = "Sign up",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            TwoColorText(
                modifier = Modifier.clickable { onNavigateToSingIn() },
                firstPart = "Already have an account?",
                secondPart = "Login",
                fontSize = 14.sp
            )
        }
    }
}