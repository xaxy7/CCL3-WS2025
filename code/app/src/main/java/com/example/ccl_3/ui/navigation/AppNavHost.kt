package com.example.ccl_3.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ccl_3.model.GameMode
import com.example.ccl_3.ui.main.MainScreen
import com.example.ccl_3.ui.quiz.QuizScreen
import com.example.ccl_3.ui.region.RegionScreen


@Composable
fun AppNavHost(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN
    ){

        composable(Routes.MAIN) {
            MainScreen{
                region ->
                navController.navigate("region/$region")
            }
        }
        composable(
            Routes.REGION,
            arguments = listOf(navArgument("regionName"){type = NavType.StringType})
        ) { backStackEntry ->

            val region = backStackEntry.arguments?.getString("regionName")!!

            RegionScreen(
                regionName = region,
                onModeSelected = {mode->
                    navController.navigate("quiz/$region/${mode.name}")

                }
            )

        }
        composable(
            route = Routes.QUIZ,
            arguments = listOf(
                navArgument("regionName") {type = NavType.StringType},
                navArgument("gameMode"){type = NavType.StringType}
            )
        ) { backStackEntry ->

            val region = backStackEntry.arguments?.getString("regionName")!!
            val gameMode = GameMode.valueOf(
                backStackEntry.arguments?.getString("gameMode")!!
            )
            QuizScreen(

                regionName = region,
                gameMode = gameMode
            )
        }
    }
}