package com.example.ccl_3.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.ccl_3.model.BookmarkType
import com.example.ccl_3.model.Difficulty
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.QuizSource
import com.example.ccl_3.ui.components.BottomNavBar
import com.example.ccl_3.ui.components.BottomNavItem
import com.example.ccl_3.ui.difficulty.DifficultyScreen
import com.example.ccl_3.ui.history.HistoryScreen
import com.example.ccl_3.ui.main.MainScreen
import com.example.ccl_3.ui.notebook.NotebookScreen
import com.example.ccl_3.ui.quiz.QuizScreen
import com.example.ccl_3.ui.region.RegionScreen
import com.example.ccl_3.ui.summary.SummaryScreen
import com.example.ccl_3.ui.theme.AppColors

@Composable
fun AppNavHost(navController: NavHostController) {
    val appNavigator = AppNavigatorImpl(navController)

    CompositionLocalProvider(LocalAppNavigator provides appNavigator) {
        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val bottomNavItems = listOf(
                    BottomNavItem(route = Routes.MAIN, label = "Home", icon = Icons.Outlined.Home),
                    BottomNavItem(route = Routes.NOTEBOOK, label = "Bookmarks", icon = Icons.Outlined.Book),
                    BottomNavItem(route = Routes.HISTORY, label = "History", icon = Icons.Outlined.History)
                )
                val showBottomBar = currentDestination?.hierarchy?.any { dest ->
                    bottomNavItems.any { it.route == dest.route }
                } == true

                if (showBottomBar) {
                    BottomNavBar(navController = navController, items = bottomNavItems)
                }
            },

        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Routes.MAIN,
                modifier = Modifier.padding(padding)
            ) {

                composable(Routes.MAIN) {
                    MainScreen(onRegionSelected = { region, isGlobal ->
                        appNavigator.navigateToRegion(region, isGlobal)
                    })

                }
                composable(
                    route = Routes.REGION,
                    arguments = listOf(
                        navArgument("regionName") { type = NavType.StringType },
                        navArgument("isGlobal") { type = NavType.BoolType }
                    )
                ) { backStackEntry ->

                    val region = backStackEntry.arguments?.getString("regionName")!!
                    val isGlobal = backStackEntry.arguments?.getBoolean("isGlobal")!!

                    RegionScreen(
                        regionName = region,
                        isGlobal = isGlobal,
                        onModeSelected = { mode ->
                            appNavigator.navigateToDifficulty(region, isGlobal, mode)
                        }
                    )


                }
                composable(
                    route = Routes.DIFFICULTY,
                    arguments = listOf(
                        navArgument("regionName") { type = NavType.StringType },
                        navArgument("isGlobal") { type = NavType.BoolType },
                        navArgument("gameMode") { type = NavType.StringType }
                    )
                ) { backStackEntry ->

                    val region = backStackEntry.arguments!!.getString("regionName")!!
                    val isGlobal = backStackEntry.arguments!!.getBoolean("isGlobal")
                    val gameMode = GameMode.valueOf(
                        backStackEntry.arguments!!.getString("gameMode")!!
                    )
                    DifficultyScreen(
                        regionName = region,
                        isGlobal = isGlobal,
                        onDifficultySelected = { difficulty ->
                            appNavigator.navigateToQuiz(region, isGlobal, gameMode, difficulty)
                        }
                    )
                }
                composable(
                    route = Routes.QUIZ,
                    arguments = listOf(
                        navArgument("regionName") { type = NavType.StringType },
                        navArgument("isGlobal") { type = NavType.BoolType },
                        navArgument("gameMode") { type = NavType.StringType },
                        navArgument("difficulty") { type = NavType.StringType }
                    )
                ) { backStackEntry ->

                    val region = backStackEntry.arguments?.getString("regionName")!!
                    val isGlobal = backStackEntry.arguments?.getBoolean("isGlobal")!!
                    val gameMode = GameMode.valueOf(
                        backStackEntry.arguments?.getString("gameMode")!!
                    )
                    val difficulty = enumValueOf<Difficulty>(backStackEntry.arguments?.getString("difficulty")!!)

                    QuizScreen(
                        quizSource = QuizSource.Standard(region, isGlobal, gameMode, difficulty),
                        onBack = { appNavigator.popBackStack() },
                        onSummary = { appNavigator.navigateToSummary() }
                    )
                }

                composable(
                    route = Routes.BOOKMARK_QUIZ,
                    arguments = listOf(
                        navArgument("contentType") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val contentType = enumValueOf<BookmarkType>(backStackEntry.arguments?.getString("contentType")!!)
                    QuizScreen(
                        quizSource = QuizSource.Bookmarks(contentType),
                        onBack = { appNavigator.popBackStack() },
                        onSummary = { appNavigator.navigateToSummary() }
                    )
                }

                composable(Routes.SUMMARY) {
                    SummaryScreen(
                        onNewGame = { appNavigator.navigateToMain() }
                    )
                }
                composable(Routes.NOTEBOOK) {
                    NotebookScreen(
                        onStartQuiz = { contentType ->
                            appNavigator.navigateToBookmarkQuiz(contentType)
                        }
                    )
                }
                composable(Routes.HISTORY) {
                    HistoryScreen()
                }
            }
        }
    }
}