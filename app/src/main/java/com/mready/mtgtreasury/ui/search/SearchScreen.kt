package com.mready.mtgtreasury.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
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
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.BoxColor
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onCardClick: (String) -> Unit
) {
    var searchCard by rememberSaveable {
        mutableStateOf("")
    }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isBottomSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isFilterBottomSheetVisible by rememberSaveable {
        mutableStateOf(false)
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

                BasicTextField(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    value = searchCard,
                    onValueChange = { searchCard = it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 12.sp,
                        color = Color.White
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .background(BoxColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.magnify),
                                contentDescription = null,
                                tint = Color.LightGray
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(modifier = Modifier.weight(1f)) {
                                if (searchCard.isEmpty()) {
                                    Text(
                                        text = "Search cards",
                                        fontSize = 12.sp,
                                        color = Color.LightGray
                                    )
                                }
                                innerTextField()
                            }

                            if (searchCard.isNotEmpty()) {
                                Icon(
                                    modifier = Modifier.clickable { searchCard = "" },
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = Color.LightGray
                                )
                            }
                        }
                    },
                    cursorBrush = SolidColor(AccentColor)
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
            Box(modifier = Modifier.fillMaxSize()) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
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
                        .padding(vertical = 20.dp)
                        .align(Alignment.TopCenter),
                    text = "Advanced Search",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )

                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { isFilterBottomSheetVisible = true }
                ) {

                }
            }
        }
    }

    Filter(
        isFilterBottomSheetVisible = isFilterBottomSheetVisible,
        hideFilter = { isFilterBottomSheetVisible = false }
    ){
        Box(modifier = Modifier.height(400.dp))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Filter(
    modifier: Modifier = Modifier,
    isFilterBottomSheetVisible: Boolean,
    hideFilter: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val filterBottomSheetState = rememberModalBottomSheetState()
    if (isFilterBottomSheetVisible) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = filterBottomSheetState,
            onDismissRequest = { hideFilter ( ) },
            windowInsets = WindowInsets.statusBars,
            containerColor = BoxColor,
        ) {
            content()
        }
    }
}