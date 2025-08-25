package com.abrar.astrolearn.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
    var isLoading by remember { mutableStateOf(false) } // Changed from true to false
    var fullAiExplanation by remember { mutableStateOf("") }
    var displayedText by remember { mutableStateOf("") }
    var isStreaming by remember { mutableStateOf(false) }
    var showCursor by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAskDialog by remember { mutableStateOf(false) }
    var customQuestion by remember { mutableStateOf("") }
    var typingJob by remember { mutableStateOf<Job?>(null) }
    var hasGeneratedExplanation by remember { mutableStateOf(false) }

    val openRouterService = remember { OpenRouterService() }
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Space-themed data mapping with enhanced visuals
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
                hasGeneratedExplanation = true
            }
        }
    }

    // Function to fetch AI explanation - now manual only
    fun fetchAiExplanation(question: String? = null) {
        isLoading = true
        errorMessage = null

        val prompt = question ?: "explain ${topic.name} simply for a learner in easy words"

        openRouterService.explainTopicForChild(prompt) { response, error ->
            isLoading = false
            if (response != null) {
                fullAiExplanation = response
                startTypingEffect(response)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("✨ AI explanation generated successfully!")
                }
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
        // Enhanced Space-themed Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0B1426), // Deep space blue
                            Color(0xFF1A1B3A), // Dark purple
                            Color(0xFF2D1B69), // Rich purple
                            Color.Black
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
            ) {
                // Enhanced Hero Banner Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight() // Changed from fixed height to wrap content
                        .defaultMinSize(minHeight = 200.dp) // Reduced from 300.dp to 200.dp for more compact layout
                ) {
                    // Background Image with enhanced overlay
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = topic.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Enhanced Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.3f),
                                        Color.Black.copy(alpha = 0.8f)
                                    )
                                )
                            )
                    )

                    // Enhanced Content Overlay
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp), // Reduced vertical padding from 40.dp to 24.dp
                        verticalArrangement = Arrangement.spacedBy(12.dp), // Reduced spacing from 16.dp to 12.dp
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 72.sp, // Slightly reduced from 80.sp to 72.sp
                            modifier = Modifier.padding(bottom = 4.dp) // Reduced from 8.dp to 4.dp
                        )
                        Text(
                            text = topic.name,
                            fontSize = 32.sp, // Slightly reduced from 36.sp to 32.sp
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = topic.description,
                            fontSize = 16.sp, // Reduced from 18.sp to 16.sp
                            color = Color.White.copy(alpha = 0.95f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp, // Reduced from 24.sp to 22.sp
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp) // Reduced vertical padding from 8.dp to 4.dp
                        )
                    }
                }

                // Enhanced Content Section with Space Background
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.15f),
                                    Color.Transparent,
                                    Color(0xFF0B1426).copy(alpha = 0.3f)
                                )
                            )
                        )
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Generate AI Explanation Button (Main Feature)
                    if (!hasGeneratedExplanation && fullAiExplanation.isEmpty()) {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                animationSpec = tween(600),
                                initialOffsetY = { it / 2 }
                            ) + fadeIn(animationSpec = tween(600))
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = primaryColor.copy(alpha = 0.2f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🚀 Ready to Explore?",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    Text(
                                        text = "Generate an AI-powered explanation tailored for learners",
                                        fontSize = 16.sp,
                                        color = Color.White.copy(alpha = 0.9f),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 22.sp,
                                        modifier = Modifier.padding(bottom = 24.dp)
                                    )

                                    Button(
                                        onClick = { fetchAiExplanation() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp),
                                        enabled = !isLoading && !isStreaming,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = primaryColor,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 6.dp,
                                            pressedElevation = 8.dp
                                        )
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 3.dp,
                                                color = Color.White
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                "Generating...",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Text(
                                                text = "✨",
                                                fontSize = 24.sp
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                "Generate AI Explanation",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Enhanced AI Explanation Area
                    AnimatedVisibility(
                        visible = hasGeneratedExplanation || isStreaming || fullAiExplanation.isNotEmpty(),
                        enter = slideInVertically(
                            animationSpec = tween(800),
                            initialOffsetY = { it / 3 }
                        ) + fadeIn(animationSpec = tween(800))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Enhanced AI Explorer's Note Header
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = primaryColor.copy(alpha = 0.25f)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = "🤖",
                                        fontSize = 32.sp
                                    )
                                    Column {
                                        Text(
                                            text = "AI Explorer's Note",
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = "Generated for ${topic.name}",
                                            fontSize = 14.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            // Enhanced AI Explanation Content
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1A1B3A).copy(alpha = 0.95f) // Dark space theme instead of white
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp)
                                ) {
                                    when {
                                        isLoading -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 60.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                                ) {
                                                    CircularProgressIndicator(
                                                        color = primaryColor,
                                                        strokeWidth = 4.dp,
                                                        modifier = Modifier.size(48.dp)
                                                    )
                                                    Text(
                                                        text = "🌟 AI is crafting your explanation...",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = Color.White, // Changed to white for visibility on dark background
                                                        fontWeight = FontWeight.Medium
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
                                                Column(
                                                    modifier = Modifier.padding(20.dp)
                                                ) {
                                                    Text(
                                                        text = "❌ Something went wrong",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(bottom = 8.dp)
                                                    )
                                                    Text(
                                                        text = errorMessage!!,
                                                        fontSize = 14.sp
                                                    )
                                                    Spacer(modifier = Modifier.height(16.dp))
                                                    Button(
                                                        onClick = {
                                                            errorMessage = null
                                                            fetchAiExplanation()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = primaryColor
                                                        )
                                                    ) {
                                                        Text("Try Again")
                                                    }
                                                }
                                            }
                                        }

                                        else -> {
                                            // Enhanced text rendering
                                            if (isStreaming) {
                                                val textWithCursor = if (showCursor) "$displayedText▌" else displayedText
                                                Text(
                                                    text = textWithCursor,
                                                    fontSize = 17.sp,
                                                    lineHeight = 26.sp,
                                                    textAlign = TextAlign.Justify,
                                                    color = Color.White.copy(alpha = 0.95f), // Changed to white for dark background
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            } else if (fullAiExplanation.isNotEmpty()) {
                                                Text(
                                                    text = parseMarkdownText(fullAiExplanation),
                                                    fontSize = 17.sp,
                                                    lineHeight = 26.sp,
                                                    textAlign = TextAlign.Justify,
                                                    color = Color.White.copy(alpha = 0.95f), // Changed to white for dark background
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Regenerate button after explanation is complete
                            if (hasGeneratedExplanation && !isStreaming && !isLoading) {
                                Button(
                                    onClick = {
                                        fullAiExplanation = ""
                                        displayedText = ""
                                        hasGeneratedExplanation = false
                                        fetchAiExplanation()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryColor.copy(alpha = 0.8f),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("🔄 Generate New Explanation", fontWeight = FontWeight.Medium)
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
}
