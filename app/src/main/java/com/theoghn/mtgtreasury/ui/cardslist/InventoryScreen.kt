package com.theoghn.mtgtreasury.ui.cardslist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.theoghn.mtgtreasury.ui.theme.MainBackgroundColor
import com.theoghn.mtgtreasury.R
import com.theoghn.mtgtreasury.ui.components.CardsGrid
import com.theoghn.mtgtreasury.ui.search.filter.FilterSearchShimmerScreen
import com.theoghn.mtgtreasury.ui.theme.AccentColor
import com.theoghn.mtgtreasury.ui.theme.BoxColor

@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    userId: String,
    onNavigateToCard: (String) -> Unit,
    onBack: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(userId) {
        viewModel.initialize(userId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
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
                        text = stringResource(R.string.inventory),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }

                SearchTextField(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 2.dp),
                    fieldValue = searchQuery,
                    showBorder = false,
                    color = BoxColor,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    placeholderText = stringResource(id = R.string.search_cards),
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    onSearch = {
                        keyboardController?.hide()
                    }
                )
            }

        },
        containerColor = Color.Transparent
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            when (val currentState = uiState) {
                is InventoryScreenUiState.InventoryUi -> {
                    val cards = currentState.cards
                    CardsGrid(
                        cards = cards,
                        onNavigateToCard = { id -> onNavigateToCard(id) }
                    )
                }

                is InventoryScreenUiState.Loading -> {
                    FilterSearchShimmerScreen(
                        modifier = Modifier
                            .padding(top = 18.dp)
                    )
                }

                is InventoryScreenUiState.Empty -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.sad_face),
                                fontSize = 50.sp,
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.no_cards_found),
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    fieldValue: String,
    placeholderText: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    showBorder: Boolean = true,
    color: Color = MainBackgroundColor,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {

    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .background(color, RoundedCornerShape(12.dp))
            .then(
                if (showBorder) Modifier.border(
                    1.dp,
                    Color.LightGray,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .padding(vertical = 8.dp),
        value = fieldValue,
        onValueChange = {
            if (it.length <= 30) {
                onValueChange(it)
            }
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 14.sp,
            color = Color.White
        ),
        decorationBox = { innerTextField ->

            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.magnify),
                    contentDescription = null,
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))


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
                        modifier = Modifier.clickable { onValueChange("") },
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