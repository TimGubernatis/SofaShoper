package de.syntax_institut.androidabschlussprojekt.ui.screen_home.components

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import de.syntax_institut.androidabschlussprojekt.util.formatPrice
import kotlinx.coroutines.launch
import androidx.compose.material3.AlertDialog

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    isInCart: Boolean = false,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    onLoginRequired: (() -> Unit)? = null
) {
    val context: Context = LocalContext.current
    val buttonColor by animateColorAsState(
        targetValue = if (isInCart) 
            MaterialTheme.colorScheme.secondary 
        else 
            MaterialTheme.colorScheme.primary,
        animationSpec = tween(durationMillis = 300),
        label = "buttonColor"
    )
    var showLoginDialog by remember { mutableStateOf(false) }
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(product.images.firstOrNull() ?: "")
                            .crossfade(true)
                            .build(),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = painterResource(R.drawable.placeholder),
                        error = painterResource(R.drawable.ic_broken_image)
                    )
                    IconButton(
                        onClick = {
                            if (onFavoriteClick != null) {
                                onFavoriteClick()
                            } else if (onLoginRequired != null) {
                                showLoginDialog = true
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Favorit" else "Als Favorit markieren",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))


                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onClick)
                )

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(
                            text = formatPrice(product.price),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (product.price > 0) {
                            Text(
                                text = "inkl. MwSt.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }


                    FloatingActionButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(48.dp),
                        containerColor = buttonColor
                    ) {
                        Icon(
                            imageVector = if (isInCart) Icons.Default.ShoppingCart else Icons.Default.AddShoppingCart,
                            contentDescription = if (isInCart) "Im Warenkorb" else "In Warenkorb hinzufügen",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
        if (showLoginDialog) {
            AlertDialog(
                onDismissRequest = { showLoginDialog = false },
                title = { Text("Nicht eingeloggt") },
                text = { Text("Für Favoriten musst du dich einloggen.") },
                confirmButton = {
                    Button(onClick = {
                        showLoginDialog = false
                        onLoginRequired?.invoke()
                    }) {
                        Text("Anmelden")
                    }
                },
                dismissButton = {
                    Button(onClick = { showLoginDialog = false }) {
                        Text("Abbrechen")
                    }
                }
            )
        }
    }
}