package com.example.mc_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Database
import com.example.mc_project.data.api.ApiClient
import com.example.mc_project.data.db.DatabaseProvider
import com.example.mc_project.data.repository.QuizRepository
import com.example.mc_project.data.repository.RoundRepository
import com.example.mc_project.ui.theme.MC_projectTheme
import com.example.mc_project.ui.quiz.QuizScreen
import com.example.mc_project.ui.quiz.QuizViewModel
import com.example.mc_project.ui.quiz.QuizViewModelFactory
import kotlinx.coroutines.launch

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

        val factory = QuizViewModelFactory(
            quizRepository = quizRepository,
            roundRepository = roundRepository
        )
        //clears the database for testing
//        lifecycleScope.launch {
//            db.roundStateDao().clear()
//        }
        enableEdgeToEdge()
        setContent {
            val quizViewModel: QuizViewModel = viewModel(factory= factory)
            QuizScreen(viewModel = quizViewModel)
//            MC_projectTheme {
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MC_projectTheme {
        Greeting("Android")
    }
}