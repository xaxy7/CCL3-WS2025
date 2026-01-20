package com.example.ccl_3.ui.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.ccl_3.model.BookmarkType
import com.example.ccl_3.model.Difficulty
import com.example.ccl_3.model.GameMode


interface AppNavigator {
    fun navigateToRegion(region: String, isGlobal: Boolean)
    fun navigateToDifficulty(region: String, isGlobal: Boolean, mode: GameMode)
    fun navigateToQuiz(region: String, isGlobal: Boolean, gameMode: GameMode, difficulty: Difficulty)
    fun navigateToBookmarkQuiz(contentType: BookmarkType)
    fun navigateToSummary()
    fun navigateToMain()
    fun navigateToNotebook()
    fun navigateToHistory()
    fun popBackStack()
}

class AppNavigatorImpl(private val navController: NavHostController) : AppNavigator {

    override fun navigateToRegion(region: String, isGlobal: Boolean) {
        navController.navigate("region/$region/$isGlobal")
    }

    override fun navigateToDifficulty(region: String, isGlobal: Boolean, mode: GameMode) {
        navController.navigate("difficulty/$region/$isGlobal/${mode.name}")
    }

    override fun navigateToQuiz(region: String, isGlobal: Boolean, gameMode: GameMode, difficulty: Difficulty) {
        navController.navigate("quiz/$region/$isGlobal/${gameMode.name}/${difficulty.name}")
    }

    override fun navigateToBookmarkQuiz(contentType: BookmarkType) {
        navController.navigate("bookmarkQuiz/${contentType.name}")
    }

    override fun navigateToSummary() {
        navController.navigate(Routes.SUMMARY)
    }

    override fun navigateToMain() {
        navController.navigate(Routes.MAIN) {
            popUpTo(0) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    override fun navigateToNotebook() {
        navController.navigate(Routes.NOTEBOOK) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    override fun navigateToHistory() {
        navController.navigate(Routes.HISTORY) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    override fun popBackStack() {
        navController.popBackStack()
    }
}

val LocalAppNavigator = compositionLocalOf<AppNavigator> { error("No AppNavigator found!") }
