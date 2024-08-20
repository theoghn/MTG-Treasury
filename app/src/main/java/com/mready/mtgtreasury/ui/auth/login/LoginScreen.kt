package com.mready.mtgtreasury.ui.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.BoxColor

@Composable
fun LoginScreen(
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
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

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun BaseTextField(
    modifier: Modifier = Modifier,
    fieldValue: String,
    placeholderText:String,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .background(BoxColor, RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp)
        ,
        value = fieldValue,
        onValueChange = {
            onValueChange(it)
        },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 14.sp,
            color = Color.White
        ),
//        keyboardOptions = KeyboardOptions.Default.copy(
//            imeAction = ImeAction.Search
//        ),
//        keyboardActions = KeyboardActions(
//            onSearch = {
//                keyboardController?.hide()
//                onNavigateToFilterSearch(searchQuery)
//            }
//        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (fieldValue.isEmpty()) {
                        Text(
                            text = placeholderText,
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                    }
                    innerTextField()
                }

                if (fieldValue.isNotEmpty()) {
                    Icon(
                        modifier = Modifier.clickable {
                            onClearClick()
                        },
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        },
        cursorBrush = SolidColor(AccentColor)
    )
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    LoginScreen()
}