package com.mready.mtgtreasury.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.components.HexagonBox
import com.mready.mtgtreasury.ui.decks.DecksScreen
import com.mready.mtgtreasury.ui.decks.DecksScreenDestination
import com.mready.mtgtreasury.ui.home.HomeScreen
import com.mready.mtgtreasury.ui.home.HomeScreenDestination
import com.mready.mtgtreasury.ui.search.SearchRoot
import com.mready.mtgtreasury.ui.search.SearchScreen
import com.mready.mtgtreasury.ui.search.filter.FilterSearchScreen
import com.mready.mtgtreasury.ui.theme.BottomBarColor
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor
import com.mready.mtgtreasury.ui.user.profile.ProfileRoot
import com.mready.mtgtreasury.ui.user.profile.ProfileScreen
import com.mready.mtgtreasury.ui.user.profile.settings.SettingsScreen
import com.mready.mtgtreasury.ui.user.profile.update.ProfileUpdateScreen


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.NavigationScreen(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedContentScope,
    navigateToCard: (String) -> Unit,
    navigateToDeckCreation: () -> Unit,
    navigateToDeck: (String) -> Unit,
    navigateToInventory: () -> Unit,
    navigateToWishlist: () -> Unit,
    navigateToWebView: (String) -> Unit,
) {
    val navigationSections = listOf(
        HomeScreenDestination,
        SearchRoot,
        DecksScreenDestination,
        ProfileRoot
    )

    val navIcons = listOf(
        Pair(R.drawable.ic_bnav_home, R.drawable.ic_bnav_home_selected),
        Pair(R.drawable.ic_bnav_search, R.drawable.ic_bnav_search_selected),
        Pair(R.drawable.ic_bnav_deck, R.drawable.ic_bnav_deck_selected),
        Pair(R.drawable.ic_bnav_profile, R.drawable.ic_bnav_profile_selected)
    )
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route.toString().split("/")[0]

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
                        val isSelected =
                            section::class.qualifiedName?.let { currentDestination.startsWith(it) }
                                ?: false

                        NavBarItem(
                            isSelected = isSelected,
                            iconId = if (isSelected) {
                                navIcons[index].second
                            } else {
                                navIcons[index].first
                            },
                            onClick = {
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
                    animatedVisibilityScope = animatedVisibilityScope,
                    onCardClick = { id -> navigateToCard(id) },
                    navigateToWebView = { url -> navigateToWebView(url) }
                )
            }

            navigation<SearchRoot>(startDestination = SearchRoot.FilterSearchScreenDestination("")) {
                composable<SearchRoot.SearchScreenDestination> {
                    SearchScreen(
                        onNavigateToFilterSearch = { searchName ->
                            navController.navigate(
                                SearchRoot.FilterSearchScreenDestination(
                                    searchName
                                )
                            ) {
                                restoreState = true
                            }
                        },
                        onBack = {
                            navController.popBackStack()
                        },
                    )
                }

                composable<SearchRoot.FilterSearchScreenDestination> { navBackStackEntry ->
                    val destination: SearchRoot.FilterSearchScreenDestination =
                        navBackStackEntry.toRoute()
                    FilterSearchScreen(
                        searchQuery = destination.searchName,
                        onNavigateToSearch = {
                            val isPopSuccessful = navController.popBackStack(
                                route = SearchRoot.SearchScreenDestination,
                                inclusive = false
                            )
                            if (!isPopSuccessful) {
                                navController.navigate(SearchRoot.SearchScreenDestination)
                            }
                        },
                        sharedTransitionScope = this@NavigationScreen,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onNavigateToCard = { id ->
                            navigateToCard(
                                id
                            )
                        }
                    )
                }
            }

            composable<DecksScreenDestination> {
                DecksScreen(
                    onNavigateToDeck = { id ->
                        navigateToDeck(
                            id
                        )
                    },
                    onNavigateToDeckCreation = {
                        navigateToDeckCreation()
                    }
                )
            }

            navigation<ProfileRoot>(startDestination = ProfileRoot.ProfileScreenDestination) {
                composable<ProfileRoot.ProfileScreenDestination> {
                    ProfileScreen(
                        navigateToInventory = { navigateToInventory() },
                        navigateToWishlist = { navigateToWishlist() },
                        navigateToSettings = { navController.navigate(ProfileRoot.SettingsScreenDestination) }
                    )
                }

                composable<ProfileRoot.SettingsScreenDestination> {
                    SettingsScreen(
                        onSignOut = {
                            navController.popBackStack(0, false)
                        },
                        onBack = {
                            navController.popBackStack()
                        },
                        navigateToProfileUpdate = { updateType: String ->
                            navController.navigate(
                                ProfileRoot.ProfileUpdateScreenDestination(
                                    updateType
                                )
                            )
                        }
                    )
                }

                composable<ProfileRoot.ProfileUpdateScreenDestination> { navBackStackEntry ->
                    val destination: ProfileRoot.ProfileUpdateScreenDestination =
                        navBackStackEntry.toRoute()
                    ProfileUpdateScreen(
                        updateType = destination.updateType,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
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
