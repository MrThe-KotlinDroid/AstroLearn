package com.abrar.astrolearn.model

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String = ""
)

data class QuizAnswer(
    val questionId: Int,
    val selectedAnswerIndex: Int,
    val isCorrect: Boolean
)

data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int,
    val answers: List<QuizAnswer>
) {
    val percentage: Float
        get() = if (totalQuestions > 0) (correctAnswers.toFloat() / totalQuestions) * 100f else 0f
}
