package com.mready.mtgtreasury.ui.auth.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mready.mtgtreasury.ui.auth.signin.BaseTextField
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.components.TwoColorText

@Composable
fun SingUpScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSingUp: () -> Unit
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }

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
                text = "Sign in.",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(44.dp))

            BaseTextField(
//            modifier = Modifier.fillMaxWidth(),
                fieldValue = email.value,
                placeholderText = "Email",
                onValueChange = {
                    email.value = it
                },
                onClearClick = {
                    email.value = ""
                }
            )

            BaseTextField(
//            modifier = Modifier.fillMaxWidth(),
                fieldValue = password.value,
                placeholderText = "Password",
                onValueChange = {
                    password.value = it
                },
                onClearClick = {
                    password.value = ""
                }
            )


            PrimaryButton(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onClick = {
                    onNavigateToHome()
                }
            ) {
                Text(
                    text = "Sign in",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            TwoColorText(
                modifier = Modifier.clickable { onNavigateToSingUp() },
                firstPart = "Don't have an account?",
                secondPart = "Create Account",
                fontSize = 14.sp
            )

            Text(
                text = "Forgot Password?",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}