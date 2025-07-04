package de.syntax_institut.androidabschlussprojekt.ui.screen_detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import de.syntax_institut.androidabschlussprojekt.ui.components.ImageCarousel
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton
import de.syntax_institut.androidabschlussprojekt.util.formatPrice
import de.syntax_institut.androidabschlussprojekt.util.responsivePadding
import de.syntax_institut.androidabschlussprojekt.util.responsiveSpacing
import de.syntax_institut.androidabschlussprojekt.util.responsiveButtonHeight
import de.syntax_institut.androidabschlussprojekt.util.responsiveImageHeight
import de.syntax_institut.androidabschlussprojekt.util.responsiveMaxWidth
import de.syntax_institut.androidabschlussprojekt.util.isTablet
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import de.syntax_institut.androidabschlussprojekt.util.responsiveTextFieldSpacing
import kotlinx.coroutines.launch

@Composable
fun ProductDetailContent(
    product: Product,
    isInCart: Boolean = false,
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit = {},
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    onLoginRequired: (() -> Unit)? = null
) {
    var showLoginDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = responsivePadding())
            .verticalScroll(rememberScrollState())
            .widthIn(max = responsiveMaxWidth()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(responsiveSpacing()))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            ImageCarousel(
                imageUrls = product.images,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(responsiveImageHeight())
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

        Spacer(modifier = Modifier.height(responsiveSpacing()))

        Text(
            text = product.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))

        product.category?.name?.let {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(responsiveTextFieldSpacing()))
        }

        Text(
            text = formatPrice(product.price),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(responsiveSpacing()))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(responsiveSpacing() * 2))

        if (isInCart) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "✅ Produkt ist im Warenkorb",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            PrimaryButton(
                text = "In den Warenkorb",
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(responsiveButtonHeight())
            )
        }

        Spacer(modifier = Modifier.height(responsiveSpacing()))
    }
}