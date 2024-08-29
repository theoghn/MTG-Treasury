package com.mready.mtgtreasury.ui.user.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    Button(
        modifier = Modifier.padding(bottom = 32.dp),
        onClick = { viewModel.signOut() }
    ) {
        Text(text = "Sign out")
    }
}