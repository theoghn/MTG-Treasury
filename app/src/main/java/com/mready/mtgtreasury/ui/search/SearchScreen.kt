package com.mready.mtgtreasury.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.BoxColor
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor


@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchScreenViewModel = hiltViewModel(),
    onNavigateToFilterSearch: (String) -> Unit
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SearchScreenTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onNavigateToFilterSearch = { onNavigateToFilterSearch(it)},
                focusRequester = focusRequester,
                keyboardController = keyboardController,
            )
        },
        containerColor = MainBackgroundColor
    ) {
        Box(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            LazyColumn {
                items(searchResults) { cardSuggestion ->
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .fillMaxWidth()
                            .clickable {
                                keyboardController?.hide()
                                viewModel.onSearchQueryChange(
                                    TextFieldValue(
                                        text = cardSuggestion,
                                        selection = TextRange(cardSuggestion.length)
                                    )
                                )
                                onNavigateToFilterSearch(cardSuggestion)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = cardSuggestion,
                            color = Color.White
                        )
                        Icon(
                            modifier = Modifier.clickable {
                                viewModel.onSearchQueryChange(
                                    TextFieldValue(
                                        text = cardSuggestion,
                                        selection = TextRange(cardSuggestion.length)
                                    )
                                )
                            },
                            painter = painterResource(id = R.drawable.arrow_top_left),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchScreenTopBar(
    searchQuery: TextFieldValue,
    focusRequester: FocusRequester,
    keyboardController: SoftwareKeyboardController?,
    onSearchQueryChange: (TextFieldValue) -> Unit,
    onNavigateToFilterSearch: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .focusRequester(focusRequester),
            value = searchQuery,
            onValueChange = {
                onSearchQueryChange(
                    it.copy(
                        text = it.text,
                        selection = TextRange(it.text.length)
                    )
                )
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                color = Color.White
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    onNavigateToFilterSearch(searchQuery.text)
                }
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .background(BoxColor, RoundedCornerShape(12.dp))
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
                        if (searchQuery.text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_cards),
                                fontSize = 14.sp,
                                color = Color.LightGray
                            )
                        }
                        innerTextField()
                    }

                    if (searchQuery.text.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.clickable {
                                onSearchQueryChange(
                                    TextFieldValue(
                                        text = "",
                                        selection = TextRange(0)
                                    )
                                )
                            },
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            },
            cursorBrush = SolidColor(Color.Red)
        )
    }
}