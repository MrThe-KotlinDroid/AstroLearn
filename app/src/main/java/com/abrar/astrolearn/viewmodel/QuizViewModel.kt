package com.abrar.astrolearn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrar.astrolearn.data.QuizRepository
import com.abrar.astrolearn.data.QuizResultStore
import com.abrar.astrolearn.model.QuizAnswer
import com.abrar.astrolearn.model.QuizQuestion
import com.abrar.astrolearn.model.QuizResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val isLoading: Boolean = false,
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val userAnswers: List<QuizAnswer> = emptyList(),
    val showResult: Boolean = false,
    val quizResult: QuizResult? = null,
    val showFeedback: Boolean = false,
    val isQuizCompleted: Boolean = false
)

class QuizViewModel : ViewModel() {

    private val quizRepository = QuizRepository()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun startQuiz() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Clear any previous quiz results when starting a new quiz
            QuizResultStore.clearQuizResult()

            val questions = quizRepository.getRandomQuiz(10)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                questions = questions,
                currentQuestionIndex = 0,
                selectedAnswerIndex = null,
                userAnswers = emptyList(),
                showResult = false,
                quizResult = null,
                showFeedback = false,
                isQuizCompleted = false
            )
        }
    }

    /**
     * Start a custom quiz based on a favorite topic's explanation
     */
    fun startCustomQuiz(topicName: String, explanation: String, questionCount: Int = 4) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Clear any previous quiz results when starting a new quiz
            QuizResultStore.clearQuizResult()

            val questions = quizRepository.generateCustomQuiz(topicName, explanation, questionCount)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                questions = questions,
                currentQuestionIndex = 0,
                selectedAnswerIndex = null,
                userAnswers = emptyList(),
                showResult = false,
                quizResult = null,
                showFeedback = false,
                isQuizCompleted = false
            )
        }
    }

    fun selectAnswer(answerIndex: Int) {
        _uiState.value = _uiState.value.copy(
            selectedAnswerIndex = answerIndex,
            showFeedback = true
        )
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        val currentQuestion = currentState.questions[currentState.currentQuestionIndex]
        val selectedAnswer = currentState.selectedAnswerIndex ?: return

        // Record the answer
        val answer = QuizAnswer(
            questionId = currentQuestion.id,
            selectedAnswerIndex = selectedAnswer,
            isCorrect = selectedAnswer == currentQuestion.correctAnswerIndex
        )

        val updatedAnswers = currentState.userAnswers + answer

        if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
            // Move to next question
            _uiState.value = currentState.copy(
                currentQuestionIndex = currentState.currentQuestionIndex + 1,
                selectedAnswerIndex = null,
                userAnswers = updatedAnswers,
                showFeedback = false
            )
        } else {
            // Quiz completed
            val correctCount = updatedAnswers.count { it.isCorrect }
            val quizResult = QuizResult(
                totalQuestions = currentState.questions.size,
                correctAnswers = correctCount,
                score = (correctCount * 100) / currentState.questions.size,
                answers = updatedAnswers
            )

            // Save quiz result to the store for access by QuizResultsScreen
            QuizResultStore.saveQuizResult(quizResult)

            _uiState.value = currentState.copy(
                userAnswers = updatedAnswers,
                isQuizCompleted = true,
                quizResult = quizResult,
                showResult = true,
                showFeedback = false
            )
        }
    }

    fun resetQuiz() {
        _uiState.value = QuizUiState()
    }

    fun getCurrentQuestion(): QuizQuestion? {
        val currentState = _uiState.value
        return if (currentState.questions.isNotEmpty() &&
                   currentState.currentQuestionIndex < currentState.questions.size) {
            currentState.questions[currentState.currentQuestionIndex]
        } else null
    }

    fun getProgress(): Float {
        val currentState = _uiState.value
        return if (currentState.questions.isNotEmpty()) {
            currentState.currentQuestionIndex.toFloat() / currentState.questions.size.toFloat()
        } else 0f
    }

    fun hideFeedback() {
        _uiState.value = _uiState.value.copy(showFeedback = false)
    }

    fun loadQuizResult() {
        val savedResult = QuizResultStore.getLastQuizResult()
        if (savedResult != null) {
            _uiState.value = _uiState.value.copy(
                quizResult = savedResult,
                showResult = true,
                isQuizCompleted = true
            )
        }
    }
}
