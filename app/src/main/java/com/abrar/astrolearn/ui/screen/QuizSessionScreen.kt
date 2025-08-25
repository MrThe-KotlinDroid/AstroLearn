package com.abrar.astrolearn.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.abrar.astrolearn.model.QuizQuestion
import com.abrar.astrolearn.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSessionScreen(
    viewModel: QuizViewModel = viewModel(),
    onQuizComplete: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Space-themed colors (only keeping what's actually used)
    val nebulaPurple = Color(0xFF8B5CF6)

    LaunchedEffect(uiState.isQuizCompleted) {
        if (uiState.isQuizCompleted) {
            // Immediate navigation for better performance - no artificial delay
            onQuizComplete()
        }
    }

    // Reset scroll position when moving to next question
    LaunchedEffect(uiState.currentQuestionIndex) {
        scrollState.animateScrollTo(0)
    }

    // Auto-scroll to feedback when answer is selected and feedback is shown
    LaunchedEffect(uiState.showFeedback, uiState.selectedAnswerIndex) {
        if (uiState.showFeedback && uiState.selectedAnswerIndex != null) {
            // Add delay for smooth UX after answer selection
            kotlinx.coroutines.delay(150)

            // Calculate scroll position to show feedback area
            // This ensures the feedback card is visible and doesn't overlap with the bottom button
            val feedbackPosition = scrollState.maxValue * 0.75f // Scroll to about 75% down
            scrollState.animateScrollTo(
                value = feedbackPosition.toInt(),
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

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
        if (uiState.isLoading) {
            LoadingScreen()
        } else {
            val currentQuestion = viewModel.getCurrentQuestion()

            if (currentQuestion != null) {
                // Use Box with scrollable content and fixed bottom button
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Scrollable content area
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(20.dp)
                            .padding(bottom = if (uiState.showFeedback) 100.dp else 0.dp) // Add bottom padding when feedback is shown
                    ) {
                        // Top Bar with Progress
                        QuizTopBar(
                            currentQuestionNumber = uiState.currentQuestionIndex + 1,
                            totalQuestions = uiState.questions.size,
                            onBackClick = onNavigateBack
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { (uiState.currentQuestionIndex + 1).toFloat() / uiState.questions.size.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = nebulaPurple,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Question Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1A1B3A).copy(alpha = 0.9f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            ) {
                                Text(
                                    text = "Question ${uiState.currentQuestionIndex + 1}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = nebulaPurple
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = currentQuestion.question,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    lineHeight = 28.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Answer Options
                        currentQuestion.options.forEachIndexed { index, option ->
                            AnswerOptionCard(
                                option = option,
                                index = index,
                                isSelected = uiState.selectedAnswerIndex == index,
                                isCorrect = index == currentQuestion.correctAnswerIndex,
                                showFeedback = uiState.showFeedback,
                                onClick = {
                                    if (!uiState.showFeedback) {
                                        viewModel.selectAnswer(index)
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Feedback and Explanation (without the Next button)
                        AnimatedVisibility(
                            visible = uiState.showFeedback,
                            enter = slideInVertically(animationSpec = tween(400)) + fadeIn(),
                            exit = slideOutVertically(animationSpec = tween(300)) + fadeOut()
                        ) {
                            FeedbackDisplayCard(
                                question = currentQuestion,
                                isCorrect = uiState.selectedAnswerIndex == currentQuestion.correctAnswerIndex
                            )
                        }

                        // Extra bottom spacing to ensure content is not hidden behind fixed button
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    // Fixed Next/Finish Button at bottom
                    AnimatedVisibility(
                        visible = uiState.showFeedback,
                        enter = slideInVertically(
                            animationSpec = tween(400),
                            initialOffsetY = { it }
                        ) + fadeIn(),
                        exit = slideOutVertically(
                            animationSpec = tween(300),
                            targetOffsetY = { it }
                        ) + fadeOut(),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        FixedBottomButton(
                            onNextQuestion = { viewModel.nextQuestion() },
                            isLastQuestion = uiState.currentQuestionIndex == uiState.questions.size - 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizTopBar(
    currentQuestionNumber: Int,
    totalQuestions: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onBackClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.White.copy(alpha = 0.8f)
            )
        ) {
            Text("← Exit Quiz")
        }

        Text(
            text = "$currentQuestionNumber / $totalQuestions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun AnswerOptionCard(
    option: String,
    index: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    showFeedback: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showFeedback && isSelected && isCorrect -> Color(0xFF10B981).copy(alpha = 0.3f)
        showFeedback && isSelected && !isCorrect -> Color(0xFFEF4444).copy(alpha = 0.3f)
        showFeedback && !isSelected && isCorrect -> Color(0xFF10B981).copy(alpha = 0.2f)
        isSelected -> Color(0xFF8B5CF6).copy(alpha = 0.3f)
        else -> Color(0xFF1A1B3A).copy(alpha = 0.6f)
    }

    val borderColor = when {
        showFeedback && isSelected && isCorrect -> Color(0xFF10B981)
        showFeedback && isSelected && !isCorrect -> Color(0xFFEF4444)
        showFeedback && !isSelected && isCorrect -> Color(0xFF10B981)
        isSelected -> Color(0xFF8B5CF6)
        else -> Color.White.copy(alpha = 0.2f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option letter (A, B, C, D)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = borderColor.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ('A' + index).toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = option,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            // Show correct/incorrect icon when feedback is shown
            if (showFeedback) {
                when {
                    isSelected && isCorrect -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Correct",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    isSelected && !isCorrect -> {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Incorrect",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    !isSelected && isCorrect -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Correct Answer",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackDisplayCard(
    question: QuizQuestion,
    isCorrect: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect)
                Color(0xFF10B981).copy(alpha = 0.2f)
            else
                Color(0xFFEF4444).copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = if (isCorrect) "Correct" else "Incorrect",
                    tint = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isCorrect) "Correct!" else "Incorrect",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (question.explanation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = question.explanation,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun FixedBottomButton(
    onNextQuestion: () -> Unit,
    isLastQuestion: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8B5CF6)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Button(
            onClick = onNextQuestion,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = if (isLastQuestion) "Finish Quiz" else "Next Question",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF8B5CF6),
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🚀 Preparing your quiz...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
