package com.abrar.astrolearn.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.abrar.astrolearn.model.SpaceTopic
import com.abrar.astrolearn.viewmodel.HomeViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onTopicClick: (SpaceTopic) -> Unit,
    onFavoritesClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val topics by viewModel.topics.collectAsState()
    var searchText by remember { mutableStateOf("") }

    // Filter topics based on search
    val filteredTopics = remember(topics, searchText) {
        if (searchText.isBlank()) {
            topics
        } else {
            topics.filter { it.name.contains(searchText, ignoreCase = true) }
        }
    }

    // Color palette - cosmic inspired
    val deepIndigo = Color(0xFF3F51B5)
    val cosmicPurple = Color(0xFF6A4C93)
    val starGold = Color(0xFFFFD700)
    val nebulaPink = Color(0xFFFF6B9D)
    val spaceBlue = Color(0xFF1E3A8A)
    val softGold = Color(0xFFFFF3E0)
    val mutedText = Color(0xFF757575)

    // Dynamic greeting based on time
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..21 -> "Good Evening"
            else -> "Good Night"
        }
    }

    // Floating icon animation
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatingOffset"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(), // This ensures the entire scaffold adjusts for the keyboard
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // Minimal Floating Favorites Button
            FloatingActionButton(
                onClick = onFavoritesClick,
                containerColor = softGold,
                contentColor = deepIndigo,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorites",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 100.dp) // Extra padding for FAB
        ) {
            item {
                // Cosmic Header Section
                CosmicHeaderSection(
                    greeting = greeting,
                    searchText = searchText,
                    onSearchTextChange = { searchText = it },
                    floatingOffset = floatingOffset,
                    deepIndigo = deepIndigo,
                    cosmicPurple = cosmicPurple,
                    starGold = starGold,
                    nebulaPink = nebulaPink,
                    spaceBlue = spaceBlue,
                    mutedText = mutedText
                )
            }

            items(filteredTopics) { topic ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ImageBackedTopicCard(
                        topic = topic,
                        onClick = { onTopicClick(topic) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CosmicHeaderSection(
    greeting: String,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    floatingOffset: Float,
    deepIndigo: Color,
    cosmicPurple: Color,
    starGold: Color,
    nebulaPink: Color,
    spaceBlue: Color,
    mutedText: Color
) {
    // Glassmorphic background with cosmic gradient
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        spaceBlue.copy(alpha = 0.9f),
                        cosmicPurple.copy(alpha = 0.7f),
                        deepIndigo.copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = 800f
                )
            )
            .padding(top = 40.dp, bottom = 32.dp)
    ) {
        // Floating stars background pattern
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("✨", fontSize = 16.sp, color = starGold.copy(alpha = 0.6f))
            Text("⭐", fontSize = 12.sp, color = starGold.copy(alpha = 0.4f))
            Text("🌟", fontSize = 14.sp, color = starGold.copy(alpha = 0.5f))
            Text("✨", fontSize = 10.sp, color = starGold.copy(alpha = 0.3f))
            Text("⭐", fontSize = 16.sp, color = starGold.copy(alpha = 0.6f))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // App Title with Floating Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Floating telescope icon
                Text(
                    text = "🔭",
                    fontSize = 32.sp,
                    modifier = Modifier
                        .offset(y = (-floatingOffset).dp)
                        .padding(end = 12.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(50),
                            ambientColor = starGold,
                            spotColor = starGold
                        )
                )

                // App title with glow effect
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AstroLearn",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.2.sp,
                            fontSize = 32.sp
                        ),
                        modifier = Modifier.shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(8.dp),
                            ambientColor = starGold.copy(alpha = 0.5f),
                            spotColor = starGold.copy(alpha = 0.5f)
                        )
                    )

                    // Floating planet accent
                    Text(
                        text = "🪐",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .offset(x = 40.dp, y = (-8).dp)
                            .offset(y = floatingOffset.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic Greeting with Pill Background
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                nebulaPink.copy(alpha = 0.3f),
                                starGold.copy(alpha = 0.2f),
                                cosmicPurple.copy(alpha = 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "$greeting, Explorer! 🌌",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.8.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Discover the infinite wonders of the cosmos",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Light,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Cosmic Search Bar
            CosmicSearchBar(
                searchText = searchText,
                onSearchTextChange = onSearchTextChange,
                mutedText = mutedText,
                spaceBlue = spaceBlue,
                starGold = starGold
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CosmicSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    mutedText: Color,
    spaceBlue: Color,
    starGold: Color
) {
    // Glassmorphic search bar
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        placeholder = {
            Text(
                text = "Search the cosmos...",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = starGold.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(50),
                ambientColor = Color.Black.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = starGold.copy(alpha = 0.8f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedContainerColor = Color.White.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = starGold
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
private fun ImageBackedTopicCard(
    topic: SpaceTopic,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    // Topic data with image URLs and fallback colors
    val topicData = remember(topic.name) {
        when (topic.name) {
            "Black Holes" -> Triple(
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop",
                "🕳️",
                Color(0xFF1A1A2E)
            )
            "Planets" -> Triple(
                "https://images.unsplash.com/photo-1614730321146-b6fa6a46bcb4?w=800&h=400&fit=crop",
                "🪐",
                Color(0xFF2D1B69)
            )
            "Big Bang" -> Triple(
                "https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?w=800&h=400&fit=crop",
                "💥",
                Color(0xFF8B0000)
            )
            "Milky Way" -> Triple(
                "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?w=800&h=400&fit=crop",
                "🌌",
                Color(0xFF483D8B)
            )
            "Constellations" -> Triple(
                "https://images.unsplash.com/photo-1446776653964-20c1d3a81b06?w=800&h=400&fit=crop",
                "✨",
                Color(0xFF191970)
            )
            "Stars" -> Triple(
                "https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?w=800&h=400&fit=crop",
                "⭐",
                Color(0xFF000080)
            )
            "Galaxies" -> Triple(
                "https://images.unsplash.com/photo-1543722530-d2c3201371e7?w=800&h=400&fit=crop",
                "🌀",
                Color(0xFF4B0082)
            )
            "Comets" -> Triple(
                "https://images.unsplash.com/photo-1560837754-b3a5c8d7e651?w=800&h=400&fit=crop",
                "☄️",
                Color(0xFF36454F)
            )
            "Solar System" -> Triple(
                "https://images.unsplash.com/photo-1614730321146-b6fa6a46bcb4?w=800&h=400&fit=crop",
                "🌞",
                Color(0xFFFF4500)
            )
            "Nebulae" -> Triple(
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop",
                "🌟",
                Color(0xFF9932CC)
            )
            "Exoplanets" -> Triple(
                "https://images.unsplash.com/photo-1608178398319-48f814d0750c?w=800&h=400&fit=crop",
                "🌍",
                Color(0xFF228B22)
            )
            "Space Exploration" -> Triple(
                "https://images.unsplash.com/photo-1611273426858-450d8e3c9fce?w=800&h=400&fit=crop",
                "🚀",
                Color(0xFF1E3A8A)
            )
            else -> Triple(
                "https://images.unsplash.com/photo-1502134249126-9f3755a50d78?w=800&h=400&fit=crop",
                "🔭",
                Color(0xFF483D8B)
            )
        }
    }

    val (imageUrl, emoji, fallbackColor) = topicData
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    // Shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background: Image or fallback color
            if (hasError) {
                // Fallback cosmic background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    fallbackColor.copy(alpha = 0.8f),
                                    fallbackColor.copy(alpha = 1f),
                                    Color.Black.copy(alpha = 0.9f)
                                ),
                                radius = 400f
                            )
                        )
                ) {
                    // Large emoji watermark
                    Text(
                        text = emoji,
                        fontSize = 80.sp,
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = 30.dp, y = (-20).dp)
                    )
                }
            } else {
                // Image with loading shimmer
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = topic.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onSuccess = { isLoading = false },
                    onError = {
                        isLoading = false
                        hasError = true
                    }
                )

                // Loading shimmer overlay
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Color.Gray.copy(alpha = shimmerAlpha)
                                    .compositeOver(fallbackColor.copy(alpha = 0.3f))
                            )
                    )
                }
            }

            // Dark gradient overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Emoji accent (top-left)
            Text(
                text = emoji,
                fontSize = 28.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // Text content (bottom-left)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = topic.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = topic.description.ifBlank { "Explore the mysteries of ${topic.name.lowercase()}" },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    ),
                    maxLines = 2
                )
            }

            // Glassmorphism overlay for text background
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f),
                                Color.Black.copy(alpha = 0.4f)
                            )
                        ),
                        RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
            )
        }
    }
}