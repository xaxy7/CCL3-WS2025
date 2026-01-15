package com.example.ccl_3.ui.navigation


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ccl_3.model.BookmarkType
import com.example.ccl_3.model.Difficulty
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.QuizSource
import com.example.ccl_3.ui.difficulty.DifficultyScreen
import com.example.ccl_3.ui.main.MainScreen
import com.example.ccl_3.ui.notebook.NotebookScreen
import com.example.ccl_3.ui.quiz.QuizScreen
import com.example.ccl_3.ui.region.RegionScreen
import com.example.ccl_3.ui.summary.SummaryScreen


@Composable
fun AppNavHost(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN
    ){

        composable(Routes.MAIN) {
            MainScreen(onRegionSelected = {
                    region, isGlobal ->
                navController.navigate("region/$region/$isGlobal")
            },navController)

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
                    navController.navigate("difficulty/$region/$isGlobal/${mode.name}")
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
            val difficultyString =
                backStackEntry.arguments!!.getString("difficulty")!!.uppercase()

            val difficulty = Difficulty.valueOf(difficultyString)
            Log.d("NAV_DEBUG", "Difficulty arg = ${backStackEntry.arguments!!.getString("difficulty")}")
//            val roundType = RoundType.valueOf(
//                backStackEntry.arguments?.getString("roundType")!!
//            )

            QuizScreen(
                regionName = region,
                isGlobal = isGlobal,
                gameMode = gameMode,
//                roundType = roundType,
                difficulty = difficulty,
                navController = navController
            )
        }
        composable(Routes.SUMMARY) {

            SummaryScreen(
                navController = navController
            )
        }
        composable(Routes.NOTEBOOK) {
            NotebookScreen(navController = navController)
        }
        composable(
            route = Routes.BOOKMARK_QUIZ,
            arguments = listOf(
                navArgument("contentType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val contentType = BookmarkType.valueOf(
                backStackEntry.arguments?.getString("contentType")!!
            )
            val gameMode = if (contentType == BookmarkType.SHAPE) GameMode.GUESS_COUNTRY else GameMode.GUESS_FLAG
            QuizScreen(
                navController = navController,
                regionName = "Bookmarks",
                isGlobal = true,
                gameMode = gameMode,
//                roundType = RoundType.PRACTICE,
                source = QuizSource.BOOKMARK,
                difficulty = Difficulty.PRACTICE,
                bookmarkType = contentType
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

            DifficultyScreen { difficulty ->
                navController.navigate(
                    "quiz/$region/$isGlobal/${gameMode.name}/${difficulty.name}"
                )
            }
        }
    }
}