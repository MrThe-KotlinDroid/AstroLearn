package com.abrar.astrolearn.viewmodel

import androidx.lifecycle.ViewModel
import com.abrar.astrolearn.model.SpaceTopic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _topics = MutableStateFlow(getSpaceTopics())
    val topics: StateFlow<List<SpaceTopic>> = _topics.asStateFlow()

    private fun getSpaceTopics(): List<SpaceTopic> {
        return listOf(
            SpaceTopic(1, "Black Holes", "Mysterious objects with gravitational pull so strong that nothing can escape"),
            SpaceTopic(2, "Planets", "Celestial bodies orbiting stars, including the planets in our solar system"),
            SpaceTopic(3, "Big Bang", "The theoretical beginning of the universe about 13.8 billion years ago"),
            SpaceTopic(4, "Milky Way", "Our home galaxy containing billions of stars and solar systems"),
            SpaceTopic(5, "Solar System", "Our sun and the planets, moons, and other objects that orbit it"),
            SpaceTopic(6, "Stars", "Massive balls of gas that produce light and heat through nuclear fusion"),
            SpaceTopic(7, "Galaxies", "Vast collections of stars, gas, dust, and dark matter"),
            SpaceTopic(8, "Nebulae", "Clouds of gas and dust in space where new stars are born"),
            SpaceTopic(9, "Dark Matter", "Invisible matter that makes up most of the universe's mass"),
            SpaceTopic(10, "Exoplanets", "Planets that orbit stars outside our solar system"),
            SpaceTopic(11, "Space Exploration", "Human missions and robotic probes exploring the cosmos"),
            SpaceTopic(12, "Constellations", "Patterns of stars as seen from Earth")
        )
    }
}
