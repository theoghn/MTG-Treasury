package com.theoghn.mtgtreasury.ui.user.signin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theoghn.mtgtreasury.R
import com.theoghn.mtgtreasury.ui.components.PrimaryButton
import com.theoghn.mtgtreasury.ui.components.TwoColorText
import com.theoghn.mtgtreasury.ui.theme.AccentColor

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToSingUp: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var showPasswordError by rememberSaveable { mutableStateOf(false) }
    var showEmailError by rememberSaveable { mutableStateOf(false) }

    val loading by viewModel.loading.collectAsState()
    val exception by viewModel.exception.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearException()
        email = ""
        password = ""
        showEmailError = false
        showPasswordError = false
    }

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
                text = stringResource(R.string.sign_in_title),
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
                fieldValue = email,
                placeholderText = stringResource(R.string.email),
                onValueChange = {
                    email = it
                    if (showEmailError) {
                        showEmailError = false
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = if (showEmailError) email.isEmpty() else false,
                errorMessage = stringResource(R.string.email_is_required)
            )

            PasswordField(
                fieldValue = password,
                placeholderText = stringResource(R.string.password),
                onValueChange = {
                    password = it
                    if (showPasswordError) {
                        showPasswordError = false
                    }
                },
                isError = if (showPasswordError) password.isEmpty() else false,
                errorMessage = stringResource(R.string.password_is_required)
            )

            PrimaryButton(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onClick = {
                    viewModel.signIn(email, password)
                    showEmailError = true
                    showPasswordError = true
                }
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = stringResource(R.string.sign_in),
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }


            TwoColorText(
                modifier = Modifier.clickable { onNavigateToSingUp() },
                firstPart = stringResource(R.string.account_existance_question),
                secondPart = stringResource(R.string.create_account),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun BaseTextField(
    fieldValue: String,
    placeholderText: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    maxLength: Int = 30,
    onValueChange: (String) -> Unit,
    errorMessage: String,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = fieldValue,
        onValueChange = {
            if (it.length <= maxLength) {
                onValueChange(it)
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color.LightGray,
            errorBorderColor = Color.Red,
            cursorColor = AccentColor
        ),
        keyboardOptions = keyboardOptions,
        isError = isError,
        minLines = minLines,
        maxLines = maxLines,
        singleLine = singleLine,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 14.sp,
            color = Color.White
        ),
        placeholder = {
            Text(
                text = placeholderText,
                fontSize = 14.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingText = {
            if (isError) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    )
}

@Composable
fun PasswordField(
    fieldValue: String,
    isError: Boolean = false,
    placeholderText: String,
    errorMessage: String,
    onValueChange: (String) -> Unit,
) {
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = fieldValue,
        onValueChange = {
            if (it.length <= 30) {
                onValueChange(it)
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color.LightGray,
            errorBorderColor = Color.Red,
            cursorColor = AccentColor
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        isError = isError,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 14.sp,
            color = Color.White
        ),
        placeholder = {
            Text(
                text = placeholderText,
                fontSize = 14.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.SemiBold
            )
        },
        trailingIcon = {
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
        },
        supportingText = {
            if (isError) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    )


}
