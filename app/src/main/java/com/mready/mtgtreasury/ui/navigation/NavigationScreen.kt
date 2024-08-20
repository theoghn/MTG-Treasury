package com.mready.mtgtreasury.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.card.CardScreenDestination
import com.mready.mtgtreasury.ui.components.HexagonBox
import com.mready.mtgtreasury.ui.decks.DecksScreen
import com.mready.mtgtreasury.ui.decks.DecksScreenDestination
import com.mready.mtgtreasury.ui.home.HomeScreen
import com.mready.mtgtreasury.ui.home.HomeScreenDestination
import com.mready.mtgtreasury.ui.profile.ProfileScreen
import com.mready.mtgtreasury.ui.profile.ProfileScreenDestination
import com.mready.mtgtreasury.ui.search.SearchScreen
import com.mready.mtgtreasury.ui.search.SearchScreenDestination
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor
import com.mready.mtgtreasury.ui.theme.BottomBarColor

@Composable
fun NavigationScreen(
    modifier: Modifier = Modifier,
    navigateToCard: (String) -> Unit,
) {
    val navigationSections = listOf(
        HomeScreenDestination,
        SearchScreenDestination,
        DecksScreenDestination,
        ProfileScreenDestination
    )
    val navIcons = listOf(
        Pair(R.drawable.ic_bnav_home, R.drawable.ic_bnav_home_selected),
        Pair(R.drawable.ic_bnav_search, R.drawable.ic_bnav_search_selected),
        Pair(R.drawable.ic_bnav_deck, R.drawable.ic_bnav_deck_selected),
        Pair(R.drawable.ic_bnav_profile, R.drawable.ic_bnav_profile_selected)
    )
    val navController = rememberNavController()
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        containerColor = MainBackgroundColor,
        bottomBar = {

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(BottomBarColor)
                    .padding(horizontal = 4.dp)
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 10.dp)
                        .padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    navigationSections.forEachIndexed { index, section ->
                        NavBarItem(
                            modifier = Modifier
                                .padding(bottom = if (index == 0 || index == 3) 0.dp else 0.dp),
                            isSelected = selectedIndex == index,
                            iconId = if (selectedIndex == index) {
                                navIcons[index].second
                            } else {
                                navIcons[index].first
                            },
                            onClick = {
                                selectedIndex = index
                                navController.navigate(section) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )

                    }
                }
            }
        }
    ) { pad ->
        NavHost(
            modifier = Modifier
                .padding(
                    top = pad.calculateTopPadding(),
                    bottom = pad.calculateBottomPadding() - 28.dp
                )
                .fillMaxSize(),
            navController = navController,
            startDestination = HomeScreenDestination,
        ) {
            composable<HomeScreenDestination> {
                HomeScreen(
                    onCardClick = { id ->
                        navigateToCard(
                            id
                        )
                    }
                )
            }

            composable<SearchScreenDestination> {
                SearchScreen(onCardClick = { id ->
                    navigateToCard(
                        id
                    )
                })
            }

            composable<DecksScreenDestination> {
                DecksScreen()
            }

            composable<ProfileScreenDestination> {
                ProfileScreen()
            }
        }
    }
}

@Composable
fun NavBarItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    iconId: Int,
    onClick: () -> Unit
) {
    HexagonBox(
        modifier = modifier,
        onClick = { onClick() },
        isSelected = isSelected
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = iconId),
            contentDescription = null,
        )
    }
}