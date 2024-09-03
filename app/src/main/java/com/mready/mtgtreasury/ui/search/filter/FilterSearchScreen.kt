package com.mready.mtgtreasury.ui.search.filter

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.ui.components.AsyncSvg
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.components.SecondaryButton
import com.mready.mtgtreasury.ui.components.ShimmerBox
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.BoxColor
import com.mready.mtgtreasury.ui.theme.LegalChipColor
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor
import com.mready.mtgtreasury.utility.Constants
import com.mready.mtgtreasury.utility.formatPrice
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SheetFilters {
    TYPE,
    SUPERTYPE,
    RARITY,
    COLOR,
    MANA,
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: FilterSearchViewModel = hiltViewModel(),
    searchQuery: String?,
    onNavigateToCard: (String) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val manaCosts by viewModel.manaCosts.collectAsState()
    val init by viewModel.init.collectAsState()

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val filterBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    var isFilterBottomSheetVisible by rememberSaveable { mutableStateOf(false) }

    var selectedFilter by rememberSaveable { mutableStateOf(SheetFilters.TYPE) }

    var selectedCardColors by rememberSaveable { mutableStateOf(listOf<String>()) }
    var selectedCardManaCosts by rememberSaveable { mutableStateOf(listOf<String>()) }
    var selectedCardRarities by rememberSaveable { mutableStateOf(listOf<String>()) }
    var selectedCardTypes by rememberSaveable { mutableStateOf(listOf<String>()) }
    var selectedCardSuperTypes by rememberSaveable { mutableStateOf(listOf<String>()) }

    if (!init) {
        viewModel.searchCards(
            name = searchQuery ?: "",
            manaCost = selectedCardManaCosts,
            colors = selectedCardColors,
            rarity = selectedCardRarities,
            type = selectedCardTypes,
            superType = selectedCardSuperTypes
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clickable {
                            onNavigateToSearch()
                        }
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BoxColor)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (searchQuery.isNullOrBlank()) stringResource(R.string.search_cards) else searchQuery,
                        color = Color.LightGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        modifier = Modifier
                            .fillMaxHeight(),
                        painter = painterResource(id = R.drawable.magnify),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                BadgedBox(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            isBottomSheetVisible = true
                        }
                        .background(BoxColor)
                        .padding(12.dp),
                    badge = {
                        if (selectedCardColors.isNotEmpty() ||
                            selectedCardManaCosts.isNotEmpty() ||
                            selectedCardRarities.isNotEmpty() ||
                            selectedCardTypes.isNotEmpty() ||
                            selectedCardSuperTypes.isNotEmpty()
                        ) {
                            Badge(
                                containerColor = AccentColor
                            )
                        }
                    }
                ) {
                    Icon(

                        painter = painterResource(id = R.drawable.filter_multiple),
                        contentDescription = null,
                        tint = AccentColor
                    )

                }


            }
        },
        containerColor = MainBackgroundColor
    ) {
        when (val currentState = uiState) {
            is FilterSearchScreenUiState.Loading -> {
                FilterSearchShimmerScreen(
                    modifier = Modifier
                        .padding(top = it.calculateTopPadding())
                )
            }

            is FilterSearchScreenUiState.FilterSearchScreenUi -> {
                val cards = currentState.cards

                Box(
                    modifier = Modifier
                        .padding(top = it.calculateTopPadding())
                        .fillMaxSize()
                        .background(Color.Transparent)
                ) {
                    LazyVerticalGrid(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(cards) { mtgCard ->
                            FilterMtgCard(
                                mtgCard = mtgCard,
                                onClick = {
                                    onNavigateToCard(mtgCard.id)
                                },
                                onAddToInventory = {
                                    viewModel.addCardToInventory(mtgCard.id)
                                },
                                isInInventory = mtgCard.qty > 0
                            )
                        }
                    }
                }
            }

            FilterSearchScreenUiState.Empty -> {
                Box(
                    Modifier
                        .padding(top = it.calculateTopPadding())
                        .fillMaxSize(),
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

    if (isBottomSheetVisible) {
        AdvancedSearchModalBottomSheet(
            bottomSheetState = bottomSheetState,
            onDismissRequest = { isBottomSheetVisible = false },
            onApplyFilters = {
                viewModel.searchCards(
                    name = searchQuery ?: "",
                    manaCost = selectedCardManaCosts,
                    colors = selectedCardColors,
                    rarity = selectedCardRarities,
                    type = selectedCardTypes,
                    superType = selectedCardSuperTypes
                )
                isBottomSheetVisible = false
            },
            updateSelectedFilter = {
                selectedFilter = it
                if (it == SheetFilters.MANA) {
                    viewModel.getCosts()
                }
            },
            showFilterSheet = { isFilterBottomSheetVisible = true },
            resetFilters = {
                selectedCardTypes = emptyList()
                selectedCardRarities = emptyList()
                selectedCardColors = emptyList()
                selectedCardSuperTypes = emptyList()
                selectedCardManaCosts = emptyList()
            }
        )
    }


    if (isFilterBottomSheetVisible) {
        ModalBottomSheet(
            modifier = Modifier,
            sheetState = filterBottomSheetState,
            onDismissRequest = {
                isFilterBottomSheetVisible = false
            },
            windowInsets = WindowInsets.statusBars,
            containerColor = BoxColor,
            dragHandle = {},
            shape = RoundedCornerShape(12.dp)
        ) {
            when (selectedFilter) {
                SheetFilters.TYPE -> {
                    TypeBottomSheet(
                        title = "Type",
                        listValues = Constants.SearchFilterValues.TYPE,
                        selectedCardTypes = selectedCardTypes,
                        hideFilter = { isFilterBottomSheetVisible = false },
                        saveChanges = { selectedCardTypes = it }
                    )
                }

                SheetFilters.SUPERTYPE -> {
                    TypeBottomSheet(
                        title = "Super Type",
                        listValues = Constants.SearchFilterValues.SUPERTYPE,
                        selectedCardTypes = selectedCardSuperTypes,
                        hideFilter = { isFilterBottomSheetVisible = false },
                        saveChanges = { selectedCardSuperTypes = it }
                    )
                }

                SheetFilters.RARITY -> {
                    TypeBottomSheet(
                        title = "Rarity",
                        listValues = Constants.SearchFilterValues.RARITY,
                        selectedCardTypes = selectedCardRarities,
                        hideFilter = { isFilterBottomSheetVisible = false },
                        saveChanges = { selectedCardRarities = it }
                    )
                }

                SheetFilters.COLOR -> {
                    ColorBottomSheet(
                        selectedCardColors = selectedCardColors,
                        hideFilter = { isFilterBottomSheetVisible = false },
                        saveChanges = { selectedCardColors = it }
                    )
                }

                SheetFilters.MANA -> {
                    ManaBottomSheet(
                        hideFilter = { isFilterBottomSheetVisible = false },
                        saveChanges = { selectedCardManaCosts = it },
                        manaCosts = manaCosts,
                        selectedCardManaCosts = selectedCardManaCosts,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSearchModalBottomSheet(
    bottomSheetState: SheetState,
    onDismissRequest: () -> Unit,
    updateSelectedFilter: (SheetFilters) -> Unit,
    showFilterSheet: () -> Unit,
    onApplyFilters: () -> Unit,
    resetFilters: () -> Unit
) {
    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = onDismissRequest,
        windowInsets = WindowInsets.statusBars,
        containerColor = MainBackgroundColor,
        dragHandle = {},
        shape = AbsoluteCutCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.systemBars)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onDismissRequest() }
                        .align(Alignment.TopStart)
                        .padding(4.dp),
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = AccentColor
                )

                Text(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.TopCenter),
                    text = stringResource(R.string.advanced_search),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = stringResource(R.string.type_and_rarity),
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )

            FilterItem(
                text = stringResource(R.string.type),
                updateSelectedFilter = { updateSelectedFilter(SheetFilters.TYPE) },
                showFilterSheet = showFilterSheet
            )

            FilterItem(
                text = stringResource(R.string.super_type),
                updateSelectedFilter = { updateSelectedFilter(SheetFilters.SUPERTYPE) },
                showFilterSheet = showFilterSheet
            )

            FilterItem(
                text = stringResource(R.string.rarity),
                updateSelectedFilter = { updateSelectedFilter(SheetFilters.RARITY) },
                showFilterSheet = showFilterSheet
            )

            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = stringResource(R.string.color),
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )

            FilterItem(
                text = stringResource(R.string.color),
                updateSelectedFilter = { updateSelectedFilter(SheetFilters.COLOR) },
                showFilterSheet = showFilterSheet
            )

            FilterItem(
                text = stringResource(R.string.mana_cost),
                updateSelectedFilter = { updateSelectedFilter(SheetFilters.MANA) },
                showFilterSheet = showFilterSheet
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, AccentColor.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(),
                    onClick = {
                        resetFilters()
                        onApplyFilters()
                        onDismissRequest()
                    },
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(R.string.clear_all),
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
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    onClick = {
                        onApplyFilters()
                        onDismissRequest()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.apply_filters),
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}


@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    hideFilter: () -> Unit,
    saveChanges: () -> Unit,
    resetFilter: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Icon(
                modifier = Modifier
                    .padding(16.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { hideFilter() }
                    .align(Alignment.TopStart)
                    .padding(4.dp),
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = AccentColor
            )

            Text(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .align(Alignment.TopCenter),
                text = title,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier
                .padding(vertical = 68.dp)
                .align(Alignment.TopCenter)
        ) {
            content()
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                modifier = Modifier
                    .height(50.dp)
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, AccentColor.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(),
                onClick = { resetFilter() },
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.reset_filter),
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
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onClick = {
                    saveChanges()
                    hideFilter()
                }
            ) {
                Text(
                    text = stringResource(R.string.apply_filter),
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun FilterItem(
    modifier: Modifier = Modifier,
    text: String,
    updateSelectedFilter: () -> Unit,
    showFilterSheet: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                updateSelectedFilter()
                showFilterSheet()
            }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier,
            text = text,
            fontSize = 14.sp,
            color = Color.LightGray,
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = AccentColor
        )
    }
}


@Composable
fun ManaBottomSheet(
    hideFilter: () -> Unit,
    selectedCardManaCosts: List<String>,
    manaCosts: Map<String, String>,
    saveChanges: (List<String>) -> Unit,
) {
    var temporaryCardManaCosts by rememberSaveable { mutableStateOf(selectedCardManaCosts) }

    FilterBottomSheet(
        modifier = Modifier.fillMaxHeight(0.7f),
        title = stringResource(R.string.mana_cost),
        hideFilter = { hideFilter() },
        saveChanges = { saveChanges(temporaryCardManaCosts) },
        resetFilter = { temporaryCardManaCosts = emptyList() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f, fill = false)
        )
        {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.select_mana_cost),
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
            ) {
                items(manaCosts.keys.toList()) { manaCost ->
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BadgedBox(
                            modifier = Modifier
                                .size(30.dp),
                            badge = {
                                if (manaCost in temporaryCardManaCosts) {
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
                            AsyncSvg(
                                modifier = Modifier
                                    .clickable {
                                        temporaryCardManaCosts =
                                            if (manaCost in temporaryCardManaCosts) {
                                                temporaryCardManaCosts - manaCost
                                            } else {
                                                temporaryCardManaCosts + manaCost
                                            }

                                    },
                                uri = "https://svgs.scryfall.io/card-symbols/${Constants.SearchFilterValues.MANA_COST[manaCost]}.svg"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorBottomSheet(
    selectedCardColors: List<String>,
    hideFilter: () -> Unit,
    saveChanges: (List<String>) -> Unit
) {
    var temporaryCardColors by rememberSaveable { mutableStateOf(selectedCardColors) }

    FilterBottomSheet(
        title = stringResource(R.string.color),
        hideFilter = { hideFilter() },
        saveChanges = { saveChanges(temporaryCardColors) },
        resetFilter = { temporaryCardColors = emptyList() }
    ) {
        Column(modifier = Modifier.fillMaxWidth())
        {
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f, false),
                text = stringResource(R.string.select_color),
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
            LazyVerticalGrid(columns = GridCells.Fixed(5)) {
                items(Constants.SearchFilterValues.COLOR) { color ->
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BadgedBox(
                            modifier = Modifier
                                .size(30.dp),
                            badge = {
                                if (color in temporaryCardColors) {
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
                            AsyncSvg(
                                modifier = Modifier
                                    .clickable {
                                        temporaryCardColors = if (color in temporaryCardColors) {
                                            temporaryCardColors - color
                                        } else {
                                            temporaryCardColors + color
                                        }
                                    },
                                uri = "https://svgs.scryfall.io/card-symbols/${color}.svg"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypeBottomSheet(
    listValues: List<String>,
    selectedCardTypes: List<String>,
    title: String,
    hideFilter: () -> Unit,
    saveChanges: (List<String>) -> Unit
) {
    var temporaryCardTypes by rememberSaveable { mutableStateOf(selectedCardTypes) }

    FilterBottomSheet(
        modifier = Modifier.fillMaxHeight(0.7f),
        title = title,
        hideFilter = { hideFilter() },
        saveChanges = { saveChanges(temporaryCardTypes) },
        resetFilter = { temporaryCardTypes = emptyList() }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF474554))
                .verticalScroll(rememberScrollState())
                .weight(weight = 1f, fill = false)
        ) {
            listValues.forEach { type ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                temporaryCardTypes = if (type in temporaryCardTypes) {
                                    temporaryCardTypes - type
                                } else {
                                    temporaryCardTypes + type
                                }
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = type,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        if (type in temporaryCardTypes) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                tint = AccentColor
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


@Composable
private fun FilterMtgCard(
    modifier: Modifier = Modifier,
    mtgCard: MtgCard,
    isInInventory: Boolean,
    onAddToInventory: () -> Unit,
    onClick: () -> Unit
) {
    var loading by rememberSaveable {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .height(290.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(4.dp),
                ambientColor = Color.White,
                spotColor = Color.White
            ),
        onClick = { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = BoxColor,
            contentColor = BoxColor
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape)
                    .background(if (isInInventory) LegalChipColor else Color.Transparent)
                    .padding(2.dp)
            ) {
                if (isInInventory) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(mtgCard.imageUris.smallSize)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                        .height(160.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Transparent)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.FillHeight,
                    placeholder = painterResource(id = R.drawable.card_back),
                    error = painterResource(id = R.drawable.card_back),
                    contentDescription = null,
                )

                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = mtgCard.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Text(
                    text = mtgCard.setName,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = formatPrice(mtgCard.prices.eur.toDouble()),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            color = Color.White
                        )

                        Text(
                            text = stringResource(R.string.qty, mtgCard.qty),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp,
                            lineHeight = 13.sp,
                            color = Color.White
                        )
                    }


                    Crossfade(targetState = loading, label = "") {
                        if (it) {
                            PrimaryButton(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                onClick = {},
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        } else {
                            SecondaryButton(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                onClick = {
                                    scope.launch {
                                        loading = true
                                        onAddToInventory()
                                        delay(700)
                                        loading = false
                                    }
                                },
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSearchShimmerScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyVerticalGrid(
            modifier = Modifier.padding(horizontal = 12.dp),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {
            items(6) {
                ShimmerBox(
                    modifier = Modifier
                        .height(290.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}