package com.mready.mtgtreasury.ui.auth.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.components.TwoColorText
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToSingUp: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val loading by viewModel.loading.collectAsState()
    val exception by viewModel.exception.collectAsState()

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

            if (loading) {
                CircularProgressIndicator()
            }

            Text(
                modifier = Modifier.align(Alignment.Start),
                text = exception,
                color = Color.Red
            )

            BaseTextField(
                fieldValue = email,
                placeholderText = "Email",
                onValueChange = {
                    email = it
                }
            )

            PasswordField(
                fieldValue = password,
                placeholderText = "Password",
                onValueChange = {
                    password = it
                }
            )


            PrimaryButton(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onClick = {
                    viewModel.signIn(email, password)
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

@Composable
fun BaseTextField(
    modifier: Modifier = Modifier,
    fieldValue: String,
    placeholderText: String,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .background(MainBackgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp),
        value = fieldValue,
        onValueChange = {
            onValueChange(it)
        },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 14.sp,
            color = Color.White
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (fieldValue.isEmpty()) {
                        Text(
                            text = placeholderText,
                            fontSize = 14.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    innerTextField()
                }
            }
        },
        cursorBrush = SolidColor(AccentColor)
    )
}

@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    fieldValue: String,
    placeholderText: String,
    onValueChange: (String) -> Unit,
) {
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .background(MainBackgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp),
        value = fieldValue,
        onValueChange = {
            onValueChange(it)
        },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 14.sp,
            color = Color.White
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (fieldValue.isEmpty()) {
                        Text(
                            text = placeholderText,
                            fontSize = 14.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    innerTextField()
                }
                if (fieldValue.isNotEmpty()) {
                    Icon(
                        modifier = Modifier.clickable {
                            passwordVisibility = !passwordVisibility
                        },
                        painter = if (passwordVisibility) {
                            painterResource(id = R.drawable.eye_outline)
                        } else {
                            painterResource(id = R.drawable.eye_off_outline)
                        },
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        },
        cursorBrush = SolidColor(AccentColor)
    )
}
