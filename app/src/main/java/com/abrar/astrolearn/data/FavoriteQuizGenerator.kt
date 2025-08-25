package com.abrar.astrolearn.data

import com.abrar.astrolearn.model.QuizQuestion
import kotlin.random.Random

/**
 * Generates quiz questions based on favorite topic explanations
 */
class FavoriteQuizGenerator {

    fun generateQuizFromExplanation(
        topicName: String,
        explanation: String,
        questionCount: Int = 4
    ): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        // Generate different types of questions based on the explanation content
        val sentences = explanation.split(". ", ".", "!", "?").filter { it.trim().length > 20 }

        if (sentences.isEmpty()) {
            return getDefaultQuestionsForTopic(topicName)
        }

        // Question type 1: Fill-in-the-blank style questions
        questions.addAll(generateFillInTheBlankQuestions(topicName, sentences, questionCount / 2))

        // Question type 2: Definition/concept questions
        questions.addAll(generateDefinitionQuestions(topicName, explanation, questionCount / 2))

        // Question type 3: True/False questions based on content
        questions.addAll(generateTrueFalseQuestions(topicName, sentences, questionCount - questions.size))

        return questions.take(questionCount).mapIndexed { index, question ->
            question.copy(id = index + 1)
        }
    }

    private fun generateFillInTheBlankQuestions(
        topicName: String,
        sentences: List<String>,
        count: Int
    ): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()
        val usedSentences = mutableSetOf<String>()

        repeat(count) {
            val availableSentences = sentences.filter { it !in usedSentences && it.length > 30 }
            if (availableSentences.isEmpty()) return@repeat

            val sentence = availableSentences.random()
            usedSentences.add(sentence)

            // Find key terms to make into multiple choice
            val keyTerms = extractKeyTerms(sentence)
            if (keyTerms.isNotEmpty()) {
                val correctTerm = keyTerms.random()
                val questionText = sentence.replace(correctTerm, "______", ignoreCase = true)

                val wrongAnswers = generateWrongAnswers(correctTerm, topicName)
                val allOptions = (wrongAnswers + correctTerm).shuffled()
                val correctIndex = allOptions.indexOf(correctTerm)

                questions.add(
                    QuizQuestion(
                        id = 0, // Will be set later
                        question = "Complete the statement about $topicName: $questionText",
                        options = allOptions,
                        correctAnswerIndex = correctIndex,
                        explanation = "The correct answer is '$correctTerm' based on the explanation about $topicName."
                    )
                )
            }
        }

        return questions
    }

    private fun generateDefinitionQuestions(
        topicName: String,
        explanation: String,
        count: Int
    ): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        // Generate questions based on topic-specific patterns
        when {
            topicName.contains("Black Hole", ignoreCase = true) -> {
                questions.add(
                    QuizQuestion(
                        id = 0,
                        question = "Based on the explanation, what is the main characteristic of a black hole?",
                        options = listOf(
                            "It emits bright light",
                            "It has extremely strong gravity",
                            "It is very cold",
                            "It spins very slowly"
                        ),
                        correctAnswerIndex = 1,
                        explanation = "According to the explanation about $topicName: black holes have extremely strong gravitational fields."
                    )
                )
            }
            topicName.contains("Planet", ignoreCase = true) -> {
                questions.add(
                    QuizQuestion(
                        id = 0,
                        question = "According to the explanation, what makes $topicName unique?",
                        options = listOf(
                            "Its size and composition",
                            "Its distance from Earth",
                            "Its number of moons",
                            "Its orbital period"
                        ),
                        correctAnswerIndex = 0,
                        explanation = "Based on the explanation about $topicName, its unique characteristics relate to size and composition."
                    )
                )
            }
            topicName.contains("Star", ignoreCase = true) -> {
                questions.add(
                    QuizQuestion(
                        id = 0,
                        question = "What process powers stars according to the explanation?",
                        options = listOf(
                            "Chemical burning",
                            "Nuclear fusion",
                            "Gravitational collapse",
                            "Magnetic fields"
                        ),
                        correctAnswerIndex = 1,
                        explanation = "Stars are powered by nuclear fusion, as described in the explanation about $topicName."
                    )
                )
            }
            else -> {
                // Generic question based on the explanation
                questions.add(
                    QuizQuestion(
                        id = 0,
                        question = "What is the most important concept mentioned in the explanation about $topicName?",
                        options = generateGenericOptions(topicName),
                        correctAnswerIndex = 0,
                        explanation = "This concept is central to understanding $topicName as described in the explanation."
                    )
                )
            }
        }

        return questions.take(count)
    }

    private fun generateTrueFalseQuestions(
        topicName: String,
        sentences: List<String>,
        count: Int
    ): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        repeat(count) {
            val sentence = sentences.randomOrNull() ?: return@repeat
            val isTrue = Random.nextBoolean()

            val questionText = if (isTrue) {
                "True or False: $sentence"
            } else {
                // Modify sentence to make it false
                val modifiedSentence = modifySentenceToMakeFalse(sentence, topicName)
                "True or False: $modifiedSentence"
            }

            questions.add(
                QuizQuestion(
                    id = 0,
                    question = questionText,
                    options = listOf("True", "False"),
                    correctAnswerIndex = if (isTrue) 0 else 1,
                    explanation = if (isTrue) {
                        "This statement is true according to the explanation about $topicName."
                    } else {
                        "This statement is false. The correct information about $topicName is in the explanation."
                    }
                )
            )
        }

        return questions
    }

    private fun extractKeyTerms(sentence: String): List<String> {
        val commonWords = setOf("the", "is", "are", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by")
        return sentence.split(" ")
            .filter { it.length > 3 && !commonWords.contains(it.lowercase()) }
            .map { it.replace(Regex("[^a-zA-Z]"), "") }
            .filter { it.length > 3 }
    }

    private fun generateWrongAnswers(correctTerm: String, topicName: String): List<String> {
        val spaceTerms = listOf(
            "nebula", "galaxy", "comet", "asteroid", "meteorite", "supernova", "quasar",
            "pulsar", "neutron star", "red giant", "white dwarf", "plasma", "cosmic ray",
            "dark matter", "dark energy", "antimatter", "hydrogen", "helium", "fusion",
            "orbit", "gravity", "radiation", "electromagnetic", "spectrum", "wavelength"
        )

        return spaceTerms.filter { it != correctTerm.lowercase() }.shuffled().take(3)
    }

    private fun generateGenericOptions(topicName: String): List<String> {
        return listOf(
            "Its formation and structure",
            "Its color and brightness",
            "Its age and temperature",
            "Its location and movement"
        )
    }

    private fun modifySentenceToMakeFalse(sentence: String, topicName: String): String {
        // Simple modifications to make statements false
        return when {
            sentence.contains("large", ignoreCase = true) -> sentence.replace("large", "small", ignoreCase = true)
            sentence.contains("hot", ignoreCase = true) -> sentence.replace("hot", "cold", ignoreCase = true)
            sentence.contains("bright", ignoreCase = true) -> sentence.replace("bright", "dim", ignoreCase = true)
            sentence.contains("fast", ignoreCase = true) -> sentence.replace("fast", "slow", ignoreCase = true)
            sentence.contains("many", ignoreCase = true) -> sentence.replace("many", "few", ignoreCase = true)
            else -> "This statement about $topicName is incorrect"
        }
    }

    private fun getDefaultQuestionsForTopic(topicName: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                question = "What field of study does $topicName belong to?",
                options = listOf("Astronomy", "Biology", "Chemistry", "Geology"),
                correctAnswerIndex = 0,
                explanation = "$topicName is related to astronomy and space science."
            ),
            QuizQuestion(
                id = 2,
                question = "Why is $topicName important to study?",
                options = listOf(
                    "It helps us understand the universe",
                    "It affects weather on Earth",
                    "It helps with agriculture",
                    "It improves transportation"
                ),
                correctAnswerIndex = 0,
                explanation = "Studying $topicName helps us better understand the universe and our place in it."
            ),
            QuizQuestion(
                id = 3,
                question = "How do scientists study $topicName?",
                options = listOf(
                    "With telescopes and observations",
                    "With microscopes only",
                    "With submarines",
                    "With time machines"
                ),
                correctAnswerIndex = 0,
                explanation = "Scientists use telescopes and various observation methods to study $topicName."
            )
        )
    }
}
