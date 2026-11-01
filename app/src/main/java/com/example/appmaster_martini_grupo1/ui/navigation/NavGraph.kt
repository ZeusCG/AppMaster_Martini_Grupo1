package com.example.appmaster_martini_grupo1.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmaster_martini_grupo1.ui.screens.CatalogScreen
import com.example.appmaster_martini_grupo1.ui.screens.DetailScreen
import com.example.appmaster_martini_grupo1.ui.screens.FavoritesScreen
import com.example.appmaster_martini_grupo1.ui.screens.HomeScreen
import com.example.appmaster_martini_grupo1.ui.screens.LoginScreen
import com.example.appmaster_martini_grupo1.ui.screens.ProfileScreen
import com.example.appmaster_martini_grupo1.ui.screens.RegisterScreen

/** Rutas congeladas (contrato, sección 4). NO renombrar. */
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val CATALOG = "catalog"
    const val FAVORITES = "favorites"
    const val DETAIL = "detail/{id}"

    /** Arma la ruta del detalle con un id concreto. */
    fun detail(id: String) = "detail/$id"
}

private const val ANIM_MS = 320

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
        // Avanzar: la pantalla nueva entra desde la derecha; la actual sale hacia la izquierda.
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(ANIM_MS)
            ) + fadeIn(tween(ANIM_MS))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(ANIM_MS)
            ) + fadeOut(tween(ANIM_MS))
        },
        // Volver (pop): al revés.
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween(ANIM_MS)
            ) + fadeIn(tween(ANIM_MS))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween(ANIM_MS)
            ) + fadeOut(tween(ANIM_MS))
        }
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        // Saca el login del back stack para que "Atrás" no vuelva a él.
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onGoCatalog = { navController.navigate(Routes.CATALOG) },
                onGoFavorites = { navController.navigate(Routes.FAVORITES) },
                onGoProfile = { navController.navigate(Routes.PROFILE) },
                onGoLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.CATALOG) {
            CatalogScreen(
                onItemClick = { id -> navController.navigate(Routes.detail(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        // El detalle tiene SU propia transición: sube desde abajo (como "abrir un ítem").
        composable(
            route = Routes.DETAIL,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(ANIM_MS)
                ) + fadeIn(tween(ANIM_MS))
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(ANIM_MS)
                ) + fadeOut(tween(ANIM_MS))
            }
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            DetailScreen(
                contentId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FAVORITES) {
            FavoritesScreen(
                onItemClick = { id -> navController.navigate(Routes.detail(id)) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
