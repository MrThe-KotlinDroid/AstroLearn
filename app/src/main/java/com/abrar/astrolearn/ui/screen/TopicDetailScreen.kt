package com.abrar.astrolearn.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.abrar.astrolearn.model.SpaceTopic
import com.abrar.astrolearn.service.OpenRouterService
import com.abrar.astrolearn.viewmodel.FavoritesViewModel
import com.abrar.astrolearn.ui.utils.parseMarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    topic: SpaceTopic,
    onBackClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var aiExplanation by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val openRouterService = remember { OpenRouterService() }
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Load AI explanation when screen opens
    LaunchedEffect(topic.name) {
        isLoading = true
        errorMessage = null
        openRouterService.explainTopicForChild(topic.name) { response, error ->
            isLoading = false
            if (response != null) {
                aiExplanation = response
            } else {
                errorMessage = error ?: "Unknown error occurred"
            }
        }
    }

    // Space-themed emoji mapping
    val topicEmojis = mapOf(
        "Black Holes" to "🕳️",
        "Planets" to "🪐",
        "Big Bang" to "💥",
        "Milky Way" to "🌌",
        "Solar System" to "☀️",
        "Stars" to "⭐",
        "Galaxies" to "🌠",
        "Nebulae" to "🌟",
        "Dark Matter" to "🌑",
        "Exoplanets" to "🌍",
        "Space Exploration" to "🚀",
        "Constellations" to "✨"
    )

    val emoji = topicEmojis[topic.name] ?: "🌌"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topic.name,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Intro card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "You've selected $emoji ${topic.name}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    Text(
                        text = topic.description,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // AI Explanation card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "AI Explanation for Learners",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Getting AI explanation...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        errorMessage != null -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Text(
                                    text = "Error: $errorMessage",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        else -> {
                            Text(
                                text = parseMarkdownText(aiExplanation),
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Save to Favorites button
                            Button(
                                onClick = {
                                    if (aiExplanation.isNotEmpty()) {
                                        coroutineScope.launch {
                                            val wasAdded = favoritesViewModel.addToFavorites(topic.name, aiExplanation)
                                            val message = if (wasAdded) {
                                                "Saved to Favorites ✅"
                                            } else {
                                                "Already in Favorites"
                                            }
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = aiExplanation.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Save to Favorites")
                            }
                        }
                    }
                }
            }
        }
    }
}
