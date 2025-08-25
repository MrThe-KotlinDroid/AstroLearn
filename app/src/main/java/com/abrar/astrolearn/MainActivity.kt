package com.abrar.astrolearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abrar.astrolearn.model.SpaceTopic
import com.abrar.astrolearn.ui.screen.FavoritesScreen
import com.abrar.astrolearn.ui.screen.HomeScreen
import com.abrar.astrolearn.ui.screen.TopicDetailScreen
import com.abrar.astrolearn.ui.screen.QuizStartScreen
import com.abrar.astrolearn.ui.screen.QuizSessionScreen
import com.abrar.astrolearn.ui.screen.QuizResultsScreen
import com.abrar.astrolearn.ui.theme.AstroLearnTheme
import com.abrar.astrolearn.viewmodel.QuizViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        enableEdgeToEdge()

        setContent {
            AstroLearnTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AstroLearnApp()
                }
            }
        }
    }
}

@Composable
fun AstroLearnApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            // Apply safe drawing insets as padding to the Scaffold content area
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) { innerPadding ->
        AstroLearnNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AstroLearnNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                onTopicClick = { topic ->
                    navController.navigate("topic_detail/${topic.id}/${topic.name}/${topic.description}")
                },
                onFavoritesClick = {
                    navController.navigate("favorites")
                },
                onQuizClick = {
                    navController.navigate("quiz_start")
                }
            )
        }

        composable("topic_detail/{topicId}/{topicName}/{topicDescription}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId")?.toIntOrNull() ?: 0
            val topicName = backStackEntry.arguments?.getString("topicName") ?: ""
            val topicDescription = backStackEntry.arguments?.getString("topicDescription") ?: ""

            val topic = SpaceTopic(
                id = topicId,
                name = topicName,
                description = topicDescription
            )

            TopicDetailScreen(
                topic = topic,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Quiz Navigation Routes
        composable("quiz_start") {
            QuizStartScreen(
                onStartQuiz = {
                    navController.navigate("quiz_session") {
                        popUpTo("quiz_start") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("quiz_session") {
            val quizViewModel: QuizViewModel = viewModel()

            // Start the quiz when entering this screen
            LaunchedEffect(Unit) {
                quizViewModel.startQuiz()
            }

            QuizSessionScreen(
                viewModel = quizViewModel,
                onQuizComplete = {
                    navController.navigate("quiz_results") {
                        popUpTo("quiz_session") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }

        composable("quiz_results") {
            val quizViewModel: QuizViewModel = viewModel()

            QuizResultsScreen(
                viewModel = quizViewModel,
                onRetakeQuiz = {
                    navController.navigate("quiz_session") {
                        popUpTo("quiz_results") { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}