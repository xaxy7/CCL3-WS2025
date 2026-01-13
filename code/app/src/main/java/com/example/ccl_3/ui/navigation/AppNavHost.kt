package com.example.ccl_3.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.model.RoundType
import com.example.ccl_3.ui.main.MainScreen
import com.example.ccl_3.ui.quiz.QuizScreen
import com.example.ccl_3.ui.region.RegionScreen
import kotlin.math.round


@Composable
fun AppNavHost(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN
    ){

        composable(Routes.MAIN) {
            MainScreen { region, isGlobal ->
                navController.navigate("region/$region/$isGlobal")
            }

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
                    navController.navigate("quiz/$region/$isGlobal/${mode.name}/${RoundType.PRACTICE.name}")
                }
            )


        }
        composable(
            route = Routes.QUIZ,
            arguments = listOf(
                navArgument("regionName") { type = NavType.StringType },
                navArgument("isGlobal") { type = NavType.BoolType },
                navArgument("gameMode") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val region = backStackEntry.arguments?.getString("regionName")!!
            val isGlobal = backStackEntry.arguments?.getBoolean("isGlobal")!!
            val gameMode = GameMode.valueOf(
                backStackEntry.arguments?.getString("gameMode")!!
            )
            val roundType = RoundType.valueOf(
                backStackEntry.arguments?.getString("roundType")!!
            )

            QuizScreen(
                regionName = region,
                isGlobal = isGlobal,
                gameMode = gameMode,
                roundType = roundType
            )
        }

    }
}