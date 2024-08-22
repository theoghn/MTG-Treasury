package com.mready.mtgtreasury.ui

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
    val navController = rememberNavController()
    val isUserLoggedIn = viewModel.isUserLoggedIn.collectAsState()

    val user = Firebase.auth.currentUser

    LaunchedEffect(isUserLoggedIn.value) {
        Log.d("RootApp", "isUserLoggedIn: $isUserLoggedIn")
        if (!isUserLoggedIn.value) {
            navController.navigate(SignInDestination) {
                launchSingleTop = true
                popUpTo(0) { inclusive = true }
            }
        } else {
            navController.navigate(NavigationScreenDestination) {
                launchSingleTop = true
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackgroundColor)
    ) {
        NavHost(
            modifier = Modifier
                .fillMaxSize(),
            navController = navController,
            startDestination = SignInDestination,
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
                        navController.navigate(
                            CardScreenDestination(id)
                        )
                    }
                )
            }

            composable<CardScreenDestination> { backStackEntry ->
                val destination: CardScreenDestination = backStackEntry.toRoute()
                CardScreen(
                    id = destination.id,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<SignUpDestination> {
                SingUpScreen(
                    onNavigateToHome = {
                        navController.navigate(NavigationScreenDestination) {
                            popUpTo(SignUpDestination) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToSingIn = {
                        navController.navigate(SignInDestination) {
                            popUpTo(SignUpDestination) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable<SignInDestination> {
                SignInScreen(
                    onNavigateToHome = {
                        navController.navigate(NavigationScreenDestination) {
                            popUpTo(SignInDestination) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToSingUp = {
                        navController.navigate(SignUpDestination) {
                            popUpTo(SignInDestination) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}