package de.syntax_institut.androidabschlussprojekt.ui.screen_cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.viewmodel.CartViewModel
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.ui.screen_cart.components.CartItemCard
import de.syntax_institut.androidabschlussprojekt.ui.screen_cart.components.CartSummary
import de.syntax_institut.androidabschlussprojekt.ui.screen_cart.components.EmptyCart
import de.syntax_institut.androidabschlussprojekt.util.responsivePadding
import de.syntax_institut.androidabschlussprojekt.util.responsiveSpacing
import de.syntax_institut.androidabschlussprojekt.util.isTablet
import de.syntax_institut.androidabschlussprojekt.util.responsiveMaxWidth

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
                    .padding(horizontal = responsivePadding())
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
                
                CartSummary(
                    itemCount = itemCount,
                    total = cartTotal,
                    onCheckoutClick = onCheckoutClick
                )
            }
        }
    }
}





