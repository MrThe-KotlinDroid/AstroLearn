package com.abrar.astrolearn.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abrar.astrolearn.model.QuizResult
import com.abrar.astrolearn.viewmodel.QuizViewModel
import java.util.Locale

@Composable
fun QuizResultsScreen(
    viewModel: QuizViewModel = viewModel(),
    onRetakeQuiz: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val quizResult = uiState.quizResult

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B1426),
                        Color(0xFF1A1B3A),
                        Color(0xFF2D1B69),
                        Color.Black
                    )
                )
            )
    ) {
        // Always show content - either results or debug info
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (quizResult != null) {
                // Celebration Header - immediate display, faster animation
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            animationSpec = tween(300),
                            initialOffsetY = { -it / 2 }
                        ) + fadeIn(animationSpec = tween(300))
                    ) {
                        ResultHeaderCard(quizResult = quizResult)
                    }
                }

                // Score Breakdown - reduced delay and faster animation
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            animationSpec = tween(300, delayMillis = 50),
                            initialOffsetY = { it / 2 }
                        ) + fadeIn(animationSpec = tween(300, delayMillis = 50))
                    ) {
                        ScoreBreakdownCard(quizResult = quizResult)
                    }
                }

                // Performance Badge - minimal delay, faster animation
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = scaleIn(
                            animationSpec = tween(250, delayMillis = 100)
                        ) + fadeIn(animationSpec = tween(250, delayMillis = 100))
                    ) {
                        PerformanceBadgeCard(percentage = quizResult.percentage)
                    }
                }

                // Action Buttons - minimal delay, fastest animation
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            animationSpec = tween(200, delayMillis = 150),
                            initialOffsetY = { it / 3 }
                        ) + fadeIn(animationSpec = tween(200, delayMillis = 150))
                    ) {
                        ActionButtonsCard(
                            onRetakeQuiz = onRetakeQuiz,
                            onNavigateHome = onNavigateHome
                        )
                    }
                }
            } else {
                // Debug/Error state when no quiz result is available
                item {
                    DebugCard(
                        onRetakeQuiz = onRetakeQuiz,
                        onNavigateHome = onNavigateHome
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun ResultHeaderCard(quizResult: QuizResult) {
    val emoji = when {
        quizResult.percentage >= 90 -> "🏆"
        quizResult.percentage >= 80 -> "🌟"
        quizResult.percentage >= 70 -> "🚀"
        quizResult.percentage >= 60 -> "👍"
        else -> "📚"
    }

    val title = when {
        quizResult.percentage >= 90 -> "Outstanding!"
        quizResult.percentage >= 80 -> "Excellent Work!"
        quizResult.percentage >= 70 -> "Great Job!"
        quizResult.percentage >= 60 -> "Good Effort!"
        else -> "Keep Learning!"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1B3A).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Quiz Complete!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8B5CF6),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ScoreBreakdownCard(quizResult: QuizResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1B3A).copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "📊 Your Results",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Score Display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Final Score:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Text(
                    text = "${quizResult.score}%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFCD34D)
                )
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Correct Answers
            ResultStatRow(
                icon = "✅",
                label = "Correct Answers:",
                value = "${quizResult.correctAnswers}",
                color = Color(0xFF10B981)
            )

            // Incorrect Answers
            ResultStatRow(
                icon = "❌",
                label = "Incorrect Answers:",
                value = "${quizResult.totalQuestions - quizResult.correctAnswers}",
                color = Color(0xFFEF4444)
            )

            // Total Questions
            ResultStatRow(
                icon = "📝",
                label = "Total Questions:",
                value = "${quizResult.totalQuestions}",
                color = Color(0xFF8B5CF6)
            )

            // Accuracy
            ResultStatRow(
                icon = "🎯",
                label = "Accuracy:",
                value = "${String.format(Locale.getDefault(), "%.1f", quizResult.percentage)}%",
                color = Color(0xFFFCD34D)
            )
        }
    }
}

@Composable
private fun ResultStatRow(
    icon: String,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun PerformanceBadgeCard(percentage: Float) {
    val (badgeColor, badgeText, description) = when {
        percentage >= 90 -> Triple(Color(0xFFFFD700), "🏆 Astronomy Expert", "You have mastered the cosmos!")
        percentage >= 80 -> Triple(Color(0xFF10B981), "🌟 Star Student", "Excellent knowledge of space!")
        percentage >= 70 -> Triple(Color(0xFF8B5CF6), "🚀 Space Explorer", "Great understanding of astronomy!")
        percentage >= 60 -> Triple(Color(0xFF3B82F6), "👨‍🚀 Astronaut in Training", "Good foundation in space science!")
        else -> Triple(Color(0xFF6B7280), "📚 Aspiring Astronomer", "Keep exploring the universe!")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = badgeColor.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = badgeText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ActionButtonsCard(
    onRetakeQuiz: () -> Unit,
    onNavigateHome: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1B3A).copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Retake Quiz Button
            Button(
                onClick = onRetakeQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retake",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Take Quiz Again",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Home Button
            OutlinedButton(
                onClick = onNavigateHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF8B5CF6), Color(0xFFFCD34D))
                    ),
                    width = 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back to Home",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DebugCard(
    onRetakeQuiz: () -> Unit,
    onNavigateHome: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1B3A).copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "No Results Available",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "It seems like there was an issue retrieving your quiz results. Please try again later.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Retake Quiz Button
            Button(
                onClick = onRetakeQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retake",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Retry Quiz",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Home Button
            OutlinedButton(
                onClick = onNavigateHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF8B5CF6), Color(0xFFFCD34D))
                    ),
                    width = 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back to Home",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
