package com.mready.mtgtreasury.ui.root

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mready.mtgtreasury.ui.card.CardScreen
import com.mready.mtgtreasury.ui.card.CardScreenDestination
import com.mready.mtgtreasury.ui.cardslist.InventoryScreen
import com.mready.mtgtreasury.ui.cardslist.InventoryScreenDestination
import com.mready.mtgtreasury.ui.cardslist.wishlist.WishlistScreen
import com.mready.mtgtreasury.ui.cardslist.wishlist.WishlistScreenDestination
import com.mready.mtgtreasury.ui.decks.create.DeckCreationScreen
import com.mready.mtgtreasury.ui.decks.create.DeckCreationScreenDestination
import com.mready.mtgtreasury.ui.decks.view.DeckScreen
import com.mready.mtgtreasury.ui.decks.view.DeckScreenDestination
import com.mready.mtgtreasury.ui.navigation.NavigationScreen
import com.mready.mtgtreasury.ui.navigation.NavigationScreenDestination
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor
import com.mready.mtgtreasury.ui.user.signin.SignInDestination
import com.mready.mtgtreasury.ui.user.signin.SignInScreen
import com.mready.mtgtreasury.ui.user.signup.SignUpDestination
import com.mready.mtgtreasury.ui.user.signup.SingUpScreen
import com.mready.mtgtreasury.ui.webview.WebViewScreen
import com.mready.mtgtreasury.ui.webview.WebViewScreenDestination

@Composable
fun RootApp(
    modifier: Modifier = Modifier,
    viewModel: RootViewModel = hiltViewModel()
) {
    val mainNavController = rememberNavController()
    val authenticationController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackgroundColor)
    ) {
        when (uiState) {
            is RootUiState.Authentication -> {
                NavHost(
                    navController = authenticationController,
                    startDestination = SignInDestination
                ) {
                    composable<SignInDestination> {
                        SignInScreen(
                            onNavigateToSingUp = {
                                if(!authenticationController.popBackStack(SignUpDestination, inclusive = false)){
                                    authenticationController.navigate(SignUpDestination)
                                }
                            }
                        )
                    }

                    composable<SignUpDestination> {
                        SingUpScreen(
                            onNavigateToSingIn = {
                                if(!authenticationController.popBackStack(SignInDestination, inclusive = false)){
                                    authenticationController.navigate(SignInDestination)
                                }
                            }
                        )
                    }
                }
            }

            is RootUiState.MainApp -> {
                if(authenticationController.currentDestination?.route != null){
                    authenticationController.popBackStack(SignInDestination, inclusive = false)
                }

                NavHost(
                    modifier = Modifier
                        .fillMaxSize(),
                    navController = mainNavController,
                    startDestination = NavigationScreenDestination,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start,
                            tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start,
                            tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.End,
                            tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.End,
                            tween(700)
                        )
                    }

                ) {
                    composable<NavigationScreenDestination> {
                        NavigationScreen(
                            navigateToCard = { id ->
                                mainNavController.navigate(
                                    CardScreenDestination(id)
                                )
                            },
                            navigateToDeckCreation = {
                                mainNavController.navigate(
                                    DeckCreationScreenDestination(null)
                                )
                            },
                            navigateToDeck = { id: String ->
                                mainNavController.navigate(
                                    DeckScreenDestination(id)
                                )
                            },
                            navigateToInventory = {
                                mainNavController.navigate(
                                    InventoryScreenDestination
                                )
                            },
                            navigateToWishlist = {
                                mainNavController.navigate(
                                    WishlistScreenDestination
                                )
                            },
                            navigateToWebView = { url ->
                                mainNavController.navigate(
                                    WebViewScreenDestination(url)
                                )
                            }
                        )
                    }

                    composable<CardScreenDestination> { backStackEntry ->
                        val destination: CardScreenDestination = backStackEntry.toRoute()
                        CardScreen(
                            id = destination.id,
                            onBack = { mainNavController.popBackStack() }
                        )
                    }

                    composable<DeckScreenDestination> { backStackEntry ->
                        val destination: DeckScreenDestination = backStackEntry.toRoute()
                        DeckScreen(
                            id = destination.id,
                            onBack = { mainNavController.popBackStack() },
                            navigateToDeckCreation = { id: String ->
                                mainNavController.navigate(
                                    DeckCreationScreenDestination(id)
                                )
                            }
                        )
                    }

                    composable<DeckCreationScreenDestination> { backStackEntry ->
                        val destination: DeckCreationScreenDestination = backStackEntry.toRoute()
                        DeckCreationScreen(
                            deckId = destination.id,
                            onBack = { mainNavController.popBackStack() }
                        )
                    }

                    composable<InventoryScreenDestination> {
                        InventoryScreen(
                            onNavigateToCard = { id ->
                                mainNavController.navigate(
                                    CardScreenDestination(id)
                                )
                            },
                            onBack = { mainNavController.popBackStack() }
                        )
                    }


                    composable<WishlistScreenDestination> {
                        WishlistScreen(
                            onNavigateToCard = { id ->
                                mainNavController.navigate(
                                    CardScreenDestination(id)
                                )
                            },
                            onBack = { mainNavController.popBackStack() }
                        )
                    }

                    composable<WebViewScreenDestination> {backStackEntry ->
                        val destination: WebViewScreenDestination = backStackEntry.toRoute()

                        WebViewScreen(
                            url = destination.url,
                            onBack = { mainNavController.popBackStack() }
                        )
                    }
                }
            }

            null -> {
                CircularProgressIndicator()
            }
        }
    }
}