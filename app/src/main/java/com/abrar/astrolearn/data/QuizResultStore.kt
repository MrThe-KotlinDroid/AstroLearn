package com.abrar.astrolearn.data

import com.abrar.astrolearn.model.QuizResult

/**
 * Simple singleton store for quiz results that survives navigation changes
 * This ensures quiz results are available when navigating to QuizResultsScreen
 */
object QuizResultStore {
    private var _lastQuizResult: QuizResult? = null

    fun saveQuizResult(result: QuizResult) {
        _lastQuizResult = result
    }

    fun getLastQuizResult(): QuizResult? {
        return _lastQuizResult
    }

    fun clearQuizResult() {
        _lastQuizResult = null
    }
}
