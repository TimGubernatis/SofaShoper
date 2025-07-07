package de.syntax_institut.androidabschlussprojekt.ui.screen_cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.viewmodel.CartViewModel
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.ui.screen_cart.components.CartItemCard
import de.syntax_institut.androidabschlussprojekt.ui.screen_cart.components.EmptyCart
import de.syntax_institut.androidabschlussprojekt.util.responsivePadding
import de.syntax_institut.androidabschlussprojekt.util.responsiveSpacing
import de.syntax_institut.androidabschlussprojekt.util.formatPrice
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartTotal by cartViewModel.cartTotal.collectAsState()
    val itemCount by cartViewModel.itemCount.collectAsState()
    val total = cartItems.sumOf { it.product.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Warenkorb") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { cartViewModel.clearCart() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Warenkorb leeren")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            EmptyCart(
                modifier = Modifier.padding(padding),
                onBackClick = onBackClick
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(responsivePadding()),
                    verticalArrangement = Arrangement.spacedBy(responsiveSpacing())
                ) {
                    items(cartItems, key = { it.product.id }) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onQuantityChange = { newQuantity ->
                                cartViewModel.updateQuantity(cartItem.product.id, newQuantity)
                            },
                            onRemove = {
                                cartViewModel.removeFromCart(cartItem.product.id)
                            }
                        )
                    }
                }
                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gesamt (${cartItems.sumOf { it.quantity }} Artikel):", style = MaterialTheme.typography.titleMedium)
                            Text(formatPrice(total), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        PrimaryButton(
                            text = "Zur Kasse",
                            onClick = onCheckoutClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}





