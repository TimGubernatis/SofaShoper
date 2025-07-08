package de.syntax_institut.androidabschlussprojekt.ui.screen_main.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    isSearching: Boolean,
    userName: String?,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    TopAppBar(
        title = {
            if (!isSearching) {
                val welcomeText = if (userName != null) "Willkommen zurück $userName" else "Willkommen Gast"
                Text(
                    text = welcomeText
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
            if (!isSearching) {
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                }
                IconButton(onClick = onFavoritesClick) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                }
            }
        }
    )
} 