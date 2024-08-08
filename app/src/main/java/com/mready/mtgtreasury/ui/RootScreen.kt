package com.mready.mtgtreasury.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mready.mtgtreasury.ui.card.CardScreen
import com.mready.mtgtreasury.ui.card.CardScreenDestination
import com.mready.mtgtreasury.ui.navigation.NavigationScreen
import com.mready.mtgtreasury.ui.navigation.NavigationScreenDestination

@Composable
fun RootScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            modifier = Modifier
                .fillMaxSize(),
            navController = navController,
            startDestination = NavigationScreenDestination,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(500)
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
                NavigationScreen(navigateToCard = { id->
                    navController.navigate(
                        CardScreenDestination(id)
                    )
                })
            }

            composable<CardScreenDestination> { backStackEntry ->
                val destination: CardScreenDestination = backStackEntry.toRoute()
                CardScreen(
                    id = destination.id,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}