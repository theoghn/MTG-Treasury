package com.mready.mtgtreasury.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Profile", fontSize = 38.sp, color = Color.White)
        Button(onClick = { Firebase.auth.signOut() }) {
            Text(text = "Sign out")

        }
    }
}