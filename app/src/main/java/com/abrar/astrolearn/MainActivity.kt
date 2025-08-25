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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abrar.astrolearn.data.QuizResultStore
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
                },
                onQuizMeClick = { topicName, explanation ->
                    // Sanitize and safely encode the parameters to handle special characters
                    val sanitizedTopicName = topicName.replace("\n", " ").replace("\r", " ").trim()
                    val sanitizedExplanation = explanation.replace("\n", " ").replace("\r", " ").trim()

                    try {
                        val encodedTopicName = java.net.URLEncoder.encode(sanitizedTopicName, "UTF-8")
                        val encodedExplanation = java.net.URLEncoder.encode(sanitizedExplanation, "UTF-8")
                        navController.navigate("custom_quiz_session/$encodedTopicName/$encodedExplanation")
                    } catch (e: Exception) {
                        // Fallback: use base64 encoding if URL encoding fails
                        val base64TopicName = android.util.Base64.encodeToString(
                            sanitizedTopicName.toByteArray(Charsets.UTF_8),
                            android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
                        )
                        val base64Explanation = android.util.Base64.encodeToString(
                            sanitizedExplanation.toByteArray(Charsets.UTF_8),
                            android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
                        )
                        navController.navigate("custom_quiz_session_base64/$base64TopicName/$base64Explanation")
                    }
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
                        popUpTo("quiz_session") { inclusive = false }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }

        // Custom quiz route for favorite topics
        composable("custom_quiz_session/{topicName}/{explanation}") { backStackEntry ->
            val encodedTopicName = backStackEntry.arguments?.getString("topicName") ?: ""
            val encodedExplanation = backStackEntry.arguments?.getString("explanation") ?: ""

            // Safely decode the URL-encoded parameters with error handling
            val topicName = try {
                java.net.URLDecoder.decode(encodedTopicName, "UTF-8")
            } catch (e: IllegalArgumentException) {
                // Fallback to encoded string if decoding fails
                encodedTopicName.replace("%", "").replace("+", " ")
            }

            val explanation = try {
                java.net.URLDecoder.decode(encodedExplanation, "UTF-8")
            } catch (e: IllegalArgumentException) {
                // Fallback to encoded string if decoding fails
                encodedExplanation.replace("%", "").replace("+", " ")
            }

            val quizViewModel: QuizViewModel = viewModel()

            // Start the custom quiz when entering this screen
            LaunchedEffect(Unit) {
                quizViewModel.startCustomQuiz(topicName, explanation, 4)
            }

            QuizSessionScreen(
                viewModel = quizViewModel,
                onQuizComplete = {
                    navController.navigate("quiz_results") {
                        popUpTo("custom_quiz_session/{topicName}/{explanation}") { inclusive = false }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Fallback custom quiz route using base64 encoding
        composable("custom_quiz_session_base64/{topicName}/{explanation}") { backStackEntry ->
            val base64TopicName = backStackEntry.arguments?.getString("topicName") ?: ""
            val base64Explanation = backStackEntry.arguments?.getString("explanation") ?: ""

            // Safely decode the base64-encoded parameters
            val topicName = try {
                String(android.util.Base64.decode(base64TopicName, android.util.Base64.URL_SAFE), Charsets.UTF_8)
            } catch (e: Exception) {
                "Custom Quiz" // Fallback topic name
            }

            val explanation = try {
                String(android.util.Base64.decode(base64Explanation, android.util.Base64.URL_SAFE), Charsets.UTF_8)
            } catch (e: Exception) {
                "Quiz based on your favorite topic." // Fallback explanation
            }

            val quizViewModel: QuizViewModel = viewModel()

            // Start the custom quiz when entering this screen
            LaunchedEffect(Unit) {
                quizViewModel.startCustomQuiz(topicName, explanation, 4)
            }

            QuizSessionScreen(
                viewModel = quizViewModel,
                onQuizComplete = {
                    navController.navigate("quiz_results") {
                        popUpTo("custom_quiz_session_base64/{topicName}/{explanation}") { inclusive = false }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("quiz_results") {
            // Use a separate ViewModel instance for results screen
            // The quiz data will be loaded from QuizResultStore
            val quizViewModel: QuizViewModel = viewModel()

            // Preload quiz results immediately when composable is created (not in LaunchedEffect)
            // This ensures data is available during first composition
            val preloadedResult = remember { QuizResultStore.getLastQuizResult() }

            // Set the result immediately if available
            LaunchedEffect(preloadedResult) {
                if (preloadedResult != null) {
                    quizViewModel.loadQuizResult()
                }
            }

            QuizResultsScreen(
                viewModel = quizViewModel,
                onRetakeQuiz = {
                    navController.navigate("quiz_session") {
                        popUpTo("quiz_results") { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}