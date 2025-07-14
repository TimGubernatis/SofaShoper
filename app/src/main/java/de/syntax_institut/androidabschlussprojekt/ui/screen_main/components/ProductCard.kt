package de.syntax_institut.androidabschlussprojekt.ui.screen_main.components

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
import androidx.compose.material3.AlertDialog
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration

import kotlinx.coroutines.CoroutineScope

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    isInCart: Boolean = false,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    onLoginRequired: (() -> Unit)? = null,
    cartTotal: Double = 0.0
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
    var animateAdd by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (animateAdd) 1.08f else 1f, label = "addToCartScale")
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 360.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(product.images.firstOrNull() ?: "")
                            .crossfade(true)
                            .build(),
                        contentDescription = product.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clickable(onClick = onClick),
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
                        modifier = Modifier
                            .padding(8.dp)
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Favorit" else "Als Favorit markieren",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
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


                    Button(
                        onClick = {
                            animateAdd = true
                            onAddToCart()
                        },
                        modifier = Modifier
                            .scale(scale)
                            .height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "In den Warenkorb",
                            modifier = Modifier.size(24.dp)
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