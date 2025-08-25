package com.abrar.astrolearn.data

import com.abrar.astrolearn.model.QuizQuestion

class QuizRepository {

    fun getAstronomyQuiz(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                question = "What is the largest planet in our solar system?",
                options = listOf("Earth", "Jupiter", "Saturn", "Mars"),
                correctAnswerIndex = 1,
                explanation = "Jupiter is the largest planet in our solar system, with a mass greater than all other planets combined."
            ),
            QuizQuestion(
                id = 2,
                question = "How many moons does Earth have?",
                options = listOf("0", "1", "2", "3"),
                correctAnswerIndex = 1,
                explanation = "Earth has one natural satellite, the Moon, which orbits our planet approximately every 27.3 days."
            ),
            QuizQuestion(
                id = 3,
                question = "What is the closest star to Earth (other than the Sun)?",
                options = listOf("Alpha Centauri", "Proxima Centauri", "Sirius", "Vega"),
                correctAnswerIndex = 1,
                explanation = "Proxima Centauri is the closest star to Earth at about 4.24 light-years away."
            ),
            QuizQuestion(
                id = 4,
                question = "Which galaxy is Earth located in?",
                options = listOf("Andromeda Galaxy", "Whirlpool Galaxy", "Milky Way Galaxy", "Triangulum Galaxy"),
                correctAnswerIndex = 2,
                explanation = "Earth is located in the Milky Way Galaxy, a barred spiral galaxy containing billions of stars."
            ),
            QuizQuestion(
                id = 5,
                question = "What causes the phases of the Moon?",
                options = listOf(
                    "Earth's shadow on the Moon",
                    "The Moon's changing position relative to Earth and Sun",
                    "Clouds covering the Moon",
                    "The Moon rotating on its axis"
                ),
                correctAnswerIndex = 1,
                explanation = "Moon phases are caused by the changing positions of the Moon, Earth, and Sun, which affects how much of the Moon's illuminated side we can see."
            ),
            QuizQuestion(
                id = 6,
                question = "What is a black hole?",
                options = listOf(
                    "A hole in space",
                    "A region where gravity is so strong that nothing can escape",
                    "A dark planet",
                    "An empty area between galaxies"
                ),
                correctAnswerIndex = 1,
                explanation = "A black hole is a region of spacetime where gravity is so strong that nothing, not even light, can escape once it crosses the event horizon."
            ),
            QuizQuestion(
                id = 7,
                question = "How long does it take for light from the Sun to reach Earth?",
                options = listOf("8 minutes", "1 hour", "1 day", "Instantly"),
                correctAnswerIndex = 0,
                explanation = "Light from the Sun takes approximately 8 minutes and 20 seconds to travel the 93 million miles to Earth."
            ),
            QuizQuestion(
                id = 8,
                question = "What is the hottest planet in our solar system?",
                options = listOf("Mercury", "Venus", "Mars", "Jupiter"),
                correctAnswerIndex = 1,
                explanation = "Venus is the hottest planet due to its thick atmosphere that traps heat, with surface temperatures around 900°F (475°C)."
            ),
            QuizQuestion(
                id = 9,
                question = "What causes a supernova?",
                options = listOf(
                    "A planet exploding",
                    "Two stars colliding",
                    "A massive star running out of fuel and collapsing",
                    "A comet hitting a star"
                ),
                correctAnswerIndex = 2,
                explanation = "A supernova occurs when a massive star exhausts its nuclear fuel and collapses under its own gravity, then explodes outward."
            ),
            QuizQuestion(
                id = 10,
                question = "What is the Great Red Spot on Jupiter?",
                options = listOf(
                    "A volcano",
                    "A giant storm",
                    "A moon shadow",
                    "An impact crater"
                ),
                correctAnswerIndex = 1,
                explanation = "The Great Red Spot is a giant storm on Jupiter that has been raging for hundreds of years and is larger than Earth."
            ),
            QuizQuestion(
                id = 11,
                question = "Which planet has the most extensive ring system?",
                options = listOf("Jupiter", "Saturn", "Uranus", "Neptune"),
                correctAnswerIndex = 1,
                explanation = "Saturn has the most extensive and visible ring system, made up of countless ice and rock particles."
            ),
            QuizQuestion(
                id = 12,
                question = "What is the main component of the Sun?",
                options = listOf("Helium", "Hydrogen", "Oxygen", "Carbon"),
                correctAnswerIndex = 1,
                explanation = "The Sun is primarily composed of hydrogen (about 73%) which is converted to helium through nuclear fusion in its core."
            ),
            QuizQuestion(
                id = 13,
                question = "How many planets are in our solar system?",
                options = listOf("7", "8", "9", "10"),
                correctAnswerIndex = 1,
                explanation = "There are 8 planets in our solar system: Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, and Neptune."
            ),
            QuizQuestion(
                id = 14,
                question = "What is a light-year?",
                options = listOf(
                    "The time it takes light to travel one year",
                    "The distance light travels in one year",
                    "The age of starlight",
                    "A unit of time in space"
                ),
                correctAnswerIndex = 1,
                explanation = "A light-year is the distance that light travels in one year, approximately 6 trillion miles or 9.5 trillion kilometers."
            ),
            QuizQuestion(
                id = 15,
                question = "What creates the beautiful colors in nebulae?",
                options = listOf(
                    "Different colored stars",
                    "Glowing gases excited by nearby stars",
                    "Reflected sunlight",
                    "Space dust"
                ),
                correctAnswerIndex = 1,
                explanation = "Nebulae get their colors from gases that glow when energized by radiation from nearby hot stars. Different gases produce different colors."
            )
        )
    }

    fun getRandomQuiz(questionCount: Int = 10): List<QuizQuestion> {
        return getAstronomyQuiz().shuffled().take(questionCount)
    }
}
