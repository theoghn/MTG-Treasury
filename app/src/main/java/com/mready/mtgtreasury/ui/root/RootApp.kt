package com.mready.mtgtreasury.ui.root

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mready.mtgtreasury.ui.auth.signin.SignInDestination
import com.mready.mtgtreasury.ui.auth.signin.SignInScreen
import com.mready.mtgtreasury.ui.auth.signup.SignUpDestination
import com.mready.mtgtreasury.ui.auth.signup.SingUpScreen
import com.mready.mtgtreasury.ui.card.CardScreen
import com.mready.mtgtreasury.ui.card.CardScreenDestination
import com.mready.mtgtreasury.ui.navigation.NavigationScreen
import com.mready.mtgtreasury.ui.navigation.NavigationScreenDestination
import com.mready.mtgtreasury.ui.theme.MainBackgroundColor

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
                                authenticationController.navigate(SignUpDestination)
                            }
                        )
                    }

                    composable<SignUpDestination> {
                        SingUpScreen(
                            onNavigateToSingIn = {
                                authenticationController.navigate(SignInDestination)
                            }
                        )
                    }

                }
            }

            is RootUiState.MainApp -> {
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
                }
            }

            null -> {
                CircularProgressIndicator()
            }
        }
    }
}