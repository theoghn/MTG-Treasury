package com.mready.mtgtreasury.ui.user.profile.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.decks.view.RemoveAlert
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.LegalChipColor
import com.mready.mtgtreasury.ui.theme.LightBlue
import com.mready.mtgtreasury.utility.getProfilePictureResourceId


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    navigateToProfileUpdate: (String) -> Unit
) {
    val user by viewModel.user.collectAsState()
    val email by viewModel.email.collectAsState()

    val scrollState = rememberScrollState()

    var isLogOutDialogVisible by rememberSaveable { mutableStateOf(false) }
    var isDeleteAccountDialogVisible by rememberSaveable { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getUser()
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
                .verticalScroll(scrollState),
        ) {
            Image(
                modifier = Modifier
                    .size(80.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = getProfilePictureResourceId(user.pictureId)),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        isBottomSheetVisible = true
                    },
                text = "Change profile picture",
                fontSize = 16.sp,
                color = LightBlue
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navigateToProfileUpdate("username") },
            ) {
                Text(
                    text = stringResource(id = R.string.username),
                    fontSize = 16.sp,
                    color = Color.LightGray
                )

                Text(
                    text = user.username,
                    fontSize = 16.sp,
                    color = Color.White
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
                    color = Color.LightGray
                )

                Text(
                    text = user.bio.ifEmpty { "Add a bio" },
                    fontSize = 16.sp,
                    color = Color.White
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Email",
                fontSize = 16.sp,
                color = Color.LightGray
            )

            Text(
                text = email,
                fontSize = 16.sp,
                color = Color.White
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
            }
        )
    }

    if (isDeleteAccountDialogVisible) {
        RemoveAlert(
            title = stringResource(R.string.warning),
            message = stringResource(R.string.are_you_sure_you_want_to_delete_your_account),
            hideAlert = { isDeleteAccountDialogVisible = false },
            onConfirm = {
                viewModel.deleteAccount()
            }
        )
    }

    if (isBottomSheetVisible) {
        PicturePickerBottomSheet(
            sheetState = sheetState,
            currentPictureId = user.pictureId,
            hideBottomSheet = {
                isBottomSheetVisible = false
            },
            updateProfilePictureId = { id ->
                if (id != user.pictureId) {
                    viewModel.updateProfilePictureId(id)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PicturePickerBottomSheet(
    sheetState: SheetState,
    currentPictureId: Int,
    hideBottomSheet: () -> Unit,
    updateProfilePictureId: (Int) -> Unit
) {
    var selectedPictureId by remember { mutableIntStateOf(currentPictureId) }

    ModalBottomSheet(
        onDismissRequest = {
            hideBottomSheet()
        },
        sheetState = sheetState
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(4)) {
            items((0..3).toList()) { id ->
                val pictureResourceId = getProfilePictureResourceId(id)

                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BadgedBox(
//                        modifier = Modifier
//                            .size(70.dp),
                        badge = {
                            if (selectedPictureId == id) {
                                Badge(
                                    modifier = Modifier.size(16.dp),
                                    containerColor = LegalChipColor
                                )
                                {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    ) {
                        Image(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .clickable {
                                    if (selectedPictureId != id) {
                                        selectedPictureId = id
                                    }
                                },
                            painter = painterResource(id = pictureResourceId),
                            contentScale = ContentScale.Crop,
                            contentDescription = null
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .height(44.dp)
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, AccentColor.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(),
                onClick = {
                    hideBottomSheet()
                },
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontSize = 16.sp,
                        color = AccentColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            PrimaryButton(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onClick = {
                    updateProfilePictureId(selectedPictureId)
                    hideBottomSheet()
                }
            ) {
                Text(
                    text = "Save Picture",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}