package com.abrar.astrolearn.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import coil.compose.AsyncImage
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
    var fullAiExplanation by remember { mutableStateOf("") }
    var displayedText by remember { mutableStateOf("") }
    var isStreaming by remember { mutableStateOf(false) }
    var showCursor by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAskDialog by remember { mutableStateOf(false) }
    var customQuestion by remember { mutableStateOf("") }
    var typingJob by remember { mutableStateOf<Job?>(null) }

    val openRouterService = remember { OpenRouterService() }
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Space-themed data mapping
    val topicData = mapOf(
        "Black Holes" to Triple("🕳️", "https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=800&h=600&fit=crop", Color(0xFF1A0033)),
        "Planets" to Triple("🪐", "https://images.unsplash.com/photo-1614728894747-a83421e2b9c9?w=800&h=600&fit=crop", Color(0xFF2D1B69)),
        "Big Bang" to Triple("💥", "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=800&h=600&fit=crop", Color(0xFF4A0E4E)),
        "Milky Way" to Triple("🌌", "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?w=800&h=600&fit=crop", Color(0xFF0F0F23)),
        "Solar System" to Triple("☀️", "https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=800&h=600&fit=crop", Color(0xFF1A1A2E)),
        "Stars" to Triple("⭐", "https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?w=800&h=600&fit=crop", Color(0xFF0F3460)),
        "Galaxies" to Triple("🌠", "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?w=800&h=600&fit=crop", Color(0xFF16537E)),
        "Nebulae" to Triple("🌟", "https://images.unsplash.com/photo-1464802686167-b939a6910659?w=800&h=600&fit=crop", Color(0xFF2A2D34)),
        "Dark Matter" to Triple("🌑", "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?w=800&h=600&fit=crop", Color(0xFF000000)),
        "Exoplanets" to Triple("🌍", "https://images.unsplash.com/photo-1614732414444-096e5f1122d5?w=800&h=600&fit=crop", Color(0xFF0B5345)),
        "Space Exploration" to Triple("🚀", "https://images.unsplash.com/photo-1446776877081-d282a0f896e2?w=800&h=600&fit=crop", Color(0xFF1B2951)),
        "Constellations" to Triple("✨", "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?w=800&h=600&fit=crop", Color(0xFF2C1810))
    )

    val (emoji, imageUrl, primaryColor) = topicData[topic.name] ?: Triple("🌌", "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?w=800&h=600&fit=crop", Color(0xFF0F0F23))

    // Cursor blinking effect - runs while streaming
    LaunchedEffect(isStreaming) {
        while (isStreaming) {
            showCursor = true
            delay(500)
            showCursor = false
            delay(500)
        }
        showCursor = false
    }

    // Auto-scroll while streaming to keep latest text visible
    LaunchedEffect(displayedText) {
        if (isStreaming && scrollState.maxValue > 0) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    // Typing effect function with job management
    fun startTypingEffect(newText: String) {
        // Cancel any previous typing job
        typingJob?.cancel()

        // Trim overly long responses for performance
        val trimmedText = newText.take(8000)

        typingJob = coroutineScope.launch {
            try {
                // Reset states
                displayedText = ""
                isStreaming = true
                showCursor = true

                // Split text into words for smooth effect
                val words = trimmedText.split(" ")
                val delayPerWord = 80L

                for (i in words.indices) {
                    // Check if job is still active
                    if (!isActive) break

                    val currentText = words.take(i + 1).joinToString(" ")
                    displayedText = currentText

                    delay(delayPerWord)
                }
            } finally {
                // Always ensure proper state cleanup
                displayedText = trimmedText
                isStreaming = false
                showCursor = false
            }
        }
    }

    // Function to fetch AI explanation
    fun fetchAiExplanation(question: String? = null) {
        isLoading = true
        errorMessage = null

        val prompt = question ?: "explain ${topic.name} simply for a learner in easy words"

        openRouterService.explainTopicForChild(prompt) { response, error ->
            isLoading = false
            if (response != null) {
                fullAiExplanation = response
                startTypingEffect(response)
            } else {
                errorMessage = error ?: "Unknown error occurred"
                isStreaming = false
                showCursor = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Failed to get AI response: ${error ?: "Unknown error"}")
                }
            }
        }
    }

    // Load AI explanation when screen opens
    LaunchedEffect(topic.name) {
        fetchAiExplanation()
    }

    // Ask AI Dialog
    if (showAskDialog) {
        Dialog(onDismissRequest = { showAskDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🤖",
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Ask AI About ${topic.name}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }

                    OutlinedTextField(
                        value = customQuestion,
                        onValueChange = { customQuestion = it },
                        label = { Text("Your question...") },
                        placeholder = { Text("What would you like to know about ${topic.name}?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        enabled = !isStreaming // Disable while streaming
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = {
                                showAskDialog = false
                                customQuestion = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (customQuestion.isNotBlank()) {
                                    fetchAiExplanation(customQuestion)
                                    showAskDialog = false
                                    customQuestion = ""
                                }
                            },
                            enabled = customQuestion.isNotBlank() && !isStreaming, // Disable while streaming
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isStreaming) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Send")
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topic.name,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Ask AI FAB
                FloatingActionButton(
                    onClick = { showAskDialog = true },
                    containerColor = primaryColor,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ask AI",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Favorites FAB (only show when explanation is complete)
                if (!isStreaming && fullAiExplanation.isNotEmpty() && errorMessage == null) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                val wasAdded = favoritesViewModel.addToFavorites(topic.name, fullAiExplanation)
                                val message = if (wasAdded) {
                                    "⭐ Saved to Favorites!"
                                } else {
                                    "Already in Favorites"
                                }
                                snackbarHostState.showSnackbar(message)
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Save to Favorites",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Hero Banner Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                // Background Image
                AsyncImage(
                    model = imageUrl,
                    contentDescription = topic.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Content Overlay
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = emoji,
                        fontSize = 64.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = topic.name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = topic.description,
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            // AI Explanation Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.1f), Color.Transparent)
                        )
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // AI Explorer's Note Header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = primaryColor.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "🤖",
                            fontSize = 28.sp
                        )
                        Text(
                            text = "AI Explorer's Note",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // AI Explanation Content
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        when {
                            isLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = primaryColor,
                                            strokeWidth = 3.dp
                                        )
                                        Text(
                                            text = "🤖 AI is thinking...",
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
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "❌ Error: $errorMessage",
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }

                            else -> {
                                // Render strategy: plain text while streaming, parsed markdown when complete
                                if (isStreaming) {
                                    // While streaming: show plain text with cursor
                                    val textWithCursor = if (showCursor) "$displayedText▌" else displayedText
                                    Text(
                                        text = textWithCursor,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        textAlign = TextAlign.Justify,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    // After streaming: show full parsed markdown
                                    Text(
                                        text = parseMarkdownText(fullAiExplanation),
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        textAlign = TextAlign.Justify,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom spacing for FABs
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}
