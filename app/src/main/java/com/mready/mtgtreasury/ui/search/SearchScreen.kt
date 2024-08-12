package com.mready.mtgtreasury.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.components.AsyncSvg
import com.mready.mtgtreasury.ui.components.PrimaryButton
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.BoxColor
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor

enum class SheetFilters {
    TYPE,
    SUPERTYPE,
    RARITY,
    COLOR,
    MANA,
}

object SearchFilterValues {
    val TYPE = listOf(
        "Artifact",
        "Battle",
        "Conspiracy",
        "Creature",
        "Dungeon",
        "Emblem",
        "Enchantment",
        "Hero",
        "Instant",
        "Kindred",
        "Land",
        "Phenomenon",
        "Plane",
        "Planeswalker",
        "Scheme",
        "Sorcery",
        "Vanguard"
    )
    val SUPERTYPE = listOf("Basic", "Legendary", "Ongoing", "Snow", "World")
    val RARITY = listOf("Common", "Uncommon", "Rare", "Mythic", "Special", "Bonus")
    val COLOR = listOf("W", "U", "B", "R", "G")
    val MANA_COST = mapOf(
        "X" to "X",
        "Y" to "Y",
        "Z" to "Z",
        "0" to "0",
        "½" to "HALF",
        "1" to "1",
        "2" to "2",
        "3" to "3",
        "4" to "4",
        "5" to "5",
        "6" to "6",
        "7" to "7",
        "8" to "8",
        "9" to "9",
        "10" to "10",
        "11" to "11",
        "12" to "12",
        "13" to "13",
        "14" to "14",
        "15" to "15",
        "16" to "16",
        "17" to "17",
        "18" to "18",
        "19" to "19",
        "20" to "20",
        "∞" to "INFINITY",
        "W/U" to "WU",
        "W/B" to "WB",
        "B/R" to "BR",
        "B/G" to "BG",
        "U/B" to "UB",
        "U/R" to "UR",
        "R/G" to "RG",
        "R/W" to "RW",
        "G/W" to "GW",
        "G/U" to "GU",
        "B/G/P" to "BGP",
        "B/R/P" to "BRP",
        "G/U/P" to "GUP",
        "G/W/P" to "GWP",
        "R/G/P" to "RGP",
        "R/W/P" to "RWP",
        "U/B/P" to "UBP",
        "U/R/P" to "URP",
        "W/B/P" to "WBP",
        "W/U/P" to "WUP",
        "C/W" to "CW",
        "C/U" to "CU",
        "C/B" to "CB",
        "C/R" to "CR",
        "C/G" to "CG",
        "2/W" to "2W",
        "2/U" to "2U",
        "2/B" to "2B",
        "2/R" to "2R",
        "2/G" to "2G",
        "H" to "H",
        "W/P" to "WP",
        "U/P" to "UP",
        "B/P" to "BP",
        "R/P" to "RP",
        "G/P" to "GP",
        "C/P" to "CP",
        "HW" to "HW",
        "HR" to "HR",
        "W" to "W",
        "U" to "U",
        "B" to "B",
        "R" to "R",
        "G" to "G",
        "C" to "C",
        "S" to "S",
        "L" to "L",
        "D" to "D"
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onCardClick: (String) -> Unit
) {
    var nameSearchField by rememberSaveable {
        mutableStateOf("")
    }
    var descriptionSearchField by rememberSaveable {
        mutableStateOf("")
    }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isBottomSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isFilterBottomSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedFilter by rememberSaveable {
        mutableStateOf(SheetFilters.TYPE)
    }

    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BaseTextField(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    searchField = nameSearchField,
                    placeholder = "Search Cards",
                    updateField = { nameSearchField = it },
                    resetField = { nameSearchField = "" }
                )

                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            isBottomSheetVisible = true
                        }
                        .background(BoxColor)
                        .padding(12.dp),
                    painter = painterResource(id = R.drawable.filter_multiple),
                    contentDescription = null,
                    tint = AccentColor
                )
            }
        },
        containerColor = MainBackgroundColor
    ) {
        Box(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
//            cards will be here
        }
    }

    if (isBottomSheetVisible) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = { isBottomSheetVisible = false },
            windowInsets = WindowInsets.statusBars,
            containerColor = MainBackgroundColor,
            dragHandle = {},
            shape = AbsoluteCutCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { isBottomSheetVisible = false }
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
                        text = "Advanced Search",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = "Card Text & Name",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text = "Name",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                )

                BaseTextField(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .height(44.dp),
                    searchField = nameSearchField,
                    placeholder = "Text in the Name",
                    updateField = { nameSearchField = it },
                    resetField = { nameSearchField = "" }
                )

                Text(
                    text = "Description",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                )

                BaseTextField(
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .height(44.dp),
                    searchField = descriptionSearchField,
                    placeholder = "Text in the description",
                    updateField = { descriptionSearchField = it },
                    resetField = { descriptionSearchField = "" }
                )

                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = "Type & Rarity",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )

                FilterItem(
                    text = "Type",
                    updateSelectedFilter = { selectedFilter = SheetFilters.TYPE },
                    showFilterSheet = { isFilterBottomSheetVisible = true }
                )

                FilterItem(
                    text = "Super Type",
                    updateSelectedFilter = { selectedFilter = SheetFilters.SUPERTYPE },
                    showFilterSheet = { isFilterBottomSheetVisible = true }
                )

                FilterItem(
                    text = "Rarity",
                    updateSelectedFilter = { selectedFilter = SheetFilters.RARITY },
                    showFilterSheet = { isFilterBottomSheetVisible = true }
                )

                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = "Color",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )

                FilterItem(
                    text = "Color",
                    updateSelectedFilter = { selectedFilter = SheetFilters.COLOR },
                    showFilterSheet = { isFilterBottomSheetVisible = true }
                )

                FilterItem(
                    text = "Mana Cost",
                    updateSelectedFilter = { selectedFilter = SheetFilters.MANA },
                    showFilterSheet = { isFilterBottomSheetVisible = true }
                )
            }
        }
    }

    val filterBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (isFilterBottomSheetVisible) {
        ModalBottomSheet(
            modifier = Modifier,
            sheetState = filterBottomSheetState,
            onDismissRequest = {
                isFilterBottomSheetVisible = false
            },
            windowInsets = WindowInsets.statusBars,
            containerColor = BoxColor,
            dragHandle = {}
        ) {
            when (selectedFilter) {
                SheetFilters.TYPE -> {
                    FilterBottomSheet(
                        title = "Type",
                        hideFilter = {
                            isFilterBottomSheetVisible = false
                        }
                    ) {
                        Box(modifier = Modifier.height(400.dp))
                        {
                            Text(text = "x")
                        }
                    }
                }

                SheetFilters.SUPERTYPE -> {
                    FilterBottomSheet(
                        title = "Super Type",
                        hideFilter = { isFilterBottomSheetVisible = false }
                    ) {
                        Box(modifier = Modifier.height(400.dp))
                        {
                            Text(text = "x")
                        }
                    }
                }

                SheetFilters.RARITY -> {
                    FilterBottomSheet(
                        title = "Rarity",
                        hideFilter = { isFilterBottomSheetVisible = false }
                    ) {
                        Box(modifier = Modifier.height(400.dp))
                        {
                            Text(text = "RARITY")
                        }
                    }
                }

                SheetFilters.COLOR -> {
                    FilterBottomSheet(
                        title = "Color",
                        hideFilter = { isFilterBottomSheetVisible = false }
                    ) {
                        Column(modifier = Modifier.fillMaxWidth())
                        {
                            Text(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(1f, false),
                                text = "Select Mana Cost",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                            )
                            LazyVerticalGrid(columns = GridCells.Fixed(5)) {
                                items(SearchFilterValues.COLOR) { color ->
                                    AsyncSvg(
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .size(30.dp),
                                        uri = "https://svgs.scryfall.io/card-symbols/${color}.svg"
                                    )
                                }
                            }
                        }
                    }
                }

                SheetFilters.MANA -> {
                    FilterBottomSheet(
                        title = "Mana Cost",
                        hideFilter = { isFilterBottomSheetVisible = false }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(weight = 1f, fill = false)
                        )
                        {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = "Select Mana Cost",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                            )
                            LazyVerticalGrid(columns = GridCells.Fixed(5)) {
                                items(SearchFilterValues.MANA_COST.keys.toList()) { manaCost ->
                                    AsyncSvg(
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .size(30.dp),
                                        uri = "https://svgs.scryfall.io/card-symbols/${SearchFilterValues.MANA_COST[manaCost]}.svg"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BaseTextField(
    modifier: Modifier = Modifier,
    searchField: String,
    placeholder: String,
    updateField: (String) -> Unit,
    resetField: () -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = searchField,
        onValueChange = { updateField(it) },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 12.sp,
            color = Color.White
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
                    tint = Color.LightGray
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (searchField.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }
                    innerTextField()
                }

                if (searchField.isNotEmpty()) {
                    Icon(
                        modifier = Modifier.clickable { resetField() },
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            }
        },
        cursorBrush = SolidColor(AccentColor)
    )
}

@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    hideFilter: () -> Unit,
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
                onClick = { },
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Cancel",
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
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = "Apply Filters",
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