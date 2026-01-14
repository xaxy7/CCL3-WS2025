package com.example.ccl_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ccl_3.data.api.ApiClient
import com.example.ccl_3.data.db.DatabaseProvider
import com.example.ccl_3.data.repository.BookmarkRepository
import com.example.ccl_3.data.repository.QuizRepository
import com.example.ccl_3.data.repository.RoundRepository
import com.example.ccl_3.data.repository.RoundResultRepository
import com.example.ccl_3.ui.navigation.AppNavHost
import com.example.ccl_3.ui.quiz.QuizViewModelFactory

//val viewModel = QuizViewModel(
//    repository = QuizRepository(ApiClient.api),
//
//)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseProvider.getDatabase(applicationContext)

        val quizRepository = QuizRepository(ApiClient.api)
        val roundRepository = RoundRepository(db.roundStateDao())
        val roundResultRepository = RoundResultRepository(db.roundResultDao())
        val bookmarkRepository = BookmarkRepository(db.bookmarkDao())
        val factory = QuizViewModelFactory(
            quizRepository = quizRepository,
            roundRepository = roundRepository,
            roundResultRepository = roundResultRepository,
            bookmarkRepository = bookmarkRepository,
            appContext = applicationContext
        )
        //clears the database for testing
//        lifecycleScope.launch {
//            db.roundStateDao().clear()
//        }
        enableEdgeToEdge()
        setContent {
//            val quizViewModel: QuizViewModel = viewModel(factory= factory)
            val navController = rememberNavController()
            AppNavHost(navController)
//            ccl_3Theme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//
//                }
//            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    ccl_3Theme {
//        Greeting("Android")
//    }
//}
//@Preview(showBackground = true)
//@Composable
//fun RegionScreenPreview() {
//
//    RegionScreen(regionName = "Europe") {
//        println(it)
//    }
//}
//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    MainScreen( ) {  }
//}
