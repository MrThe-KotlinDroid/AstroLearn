package com.abrar.astrolearn.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abrar.astrolearn.data.entity.FavoriteTopic
import com.abrar.astrolearn.viewmodel.FavoritesViewModel
import com.abrar.astrolearn.ui.utils.parseMarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    val favorites by favoritesViewModel.allFavorites.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Favorite Topics",
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
        }
    ) { innerPadding ->
        if (favorites.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💫",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "No Favorites Yet",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Save your favorite space topics with AI explanations for offline access",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favorites) { favorite ->
                    FavoriteTopicCard(
                        favorite = favorite,
                        onDelete = { favoritesViewModel.removeFromFavorites(it) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteTopicCard(
    favorite: FavoriteTopic,
    onDelete: (FavoriteTopic) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

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

    val emoji = topicEmojis[favorite.name] ?: "🌌"

    Card(
        onClick = { isExpanded = !isExpanded },
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
            // Header with title and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = emoji,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = favorite.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete favorite",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Expandable explanation
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "AI Explanation:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = parseMarkdownText(favorite.explanation),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = parseMarkdownText(favorite.explanation),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isExpanded) "Tap to collapse" else "Tap to expand",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Delete Favorite")
            },
            text = {
                Text("Are you sure you want to remove \"${favorite.name}\" from your favorites?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(favorite)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
