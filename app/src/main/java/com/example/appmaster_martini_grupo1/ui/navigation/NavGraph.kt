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
import com.example.appmaster_martini_grupo1.data.session.UserSession
import com.example.appmaster_martini_grupo1.ui.screens.CatalogScreen
import com.example.appmaster_martini_grupo1.ui.screens.ContactScreen
import com.example.appmaster_martini_grupo1.ui.screens.DetailScreen
import com.example.appmaster_martini_grupo1.ui.screens.FavoritesScreen
import com.example.appmaster_martini_grupo1.ui.screens.HomeScreen
import com.example.appmaster_martini_grupo1.ui.screens.InfoScreen
import com.example.appmaster_martini_grupo1.ui.screens.InterestsSurveyScreen
import com.example.appmaster_martini_grupo1.ui.screens.LoginScreen
import com.example.appmaster_martini_grupo1.ui.screens.ManageContentScreen
import com.example.appmaster_martini_grupo1.ui.screens.ProfileScreen
import com.example.appmaster_martini_grupo1.ui.screens.RegisterScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PROFILE = "profile"
    const val CATALOG = "catalog"
    const val FAVORITES = "favorites"
    const val INFO = "info"
    const val DETAIL = "detail/{id}"
    const val MANAGE = "manage"
    const val CONTACT = "contact"
    const val INTERESTS = "interests"

    fun detail(id: String) = "detail/$id"
}

private const val ANIM_MS = 320

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,

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
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Routes.INTERESTS) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.INTERESTS) {
            InterestsSurveyScreen(
                userId = UserSession.userId.value ?: 0L,
                onDone = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onGoManage = { navController.navigate(Routes.MANAGE) },
                onGoContact = { navController.navigate(Routes.CONTACT) },
                onGoCatalog = { navController.navigate(Routes.CATALOG) },
                onGoFavorites = { navController.navigate(Routes.FAVORITES) },
                onGoProfile = { navController.navigate(Routes.PROFILE) },
                onGoInfo = { navController.navigate(Routes.INFO) },
                onGoLogin = { navController.navigate(Routes.LOGIN) },
                onLogout = {
                    UserSession.clear()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MANAGE) {
            ManageContentScreen(
                onBack = { navController.popBackStack() },
                onGoLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.CONTACT) {
            ContactScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.INFO) {
            InfoScreen(onBack = { navController.popBackStack() })
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
