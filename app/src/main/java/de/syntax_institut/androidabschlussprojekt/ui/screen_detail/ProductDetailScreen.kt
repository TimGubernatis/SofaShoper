package de.syntax_institut.androidabschlussprojekt.ui.screen_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.components.ErrorMessage
import de.syntax_institut.androidabschlussprojekt.ui.screen_detail.components.ProductDetailContent
import de.syntax_institut.androidabschlussprojekt.viewmodel.HomeViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.CartViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    homeViewModel: HomeViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel(),
    onBackClick: () -> Unit = {}
) {
    val product by homeViewModel.selectedProduct.collectAsState()
    val isLoading by homeViewModel.productLoading.collectAsState()
    val error by homeViewModel.productError.collectAsState()
    val isInCart by remember { derivedStateOf { cartViewModel.isInCart(productId) } }

    LaunchedEffect(productId) {
        homeViewModel.loadProductById(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produktdetails") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                error != null -> {
                    ErrorMessage(
                        message = error!!,
                        showBackButton = true,
                        showRetryButton = true,
                        onBackClick = onBackClick,
                        onRetryClick = { homeViewModel.loadProductById(productId) }
                    )
                }
                
                product != null -> {
                    ProductDetailContent(
                        product = product!!,
                        isInCart = isInCart,
                        onAddToCart = {
                            product?.let { cartViewModel.addToCart(it) }
                        }
                    )
                }
                
                else -> {
                    ErrorMessage(
                        message = "Produkt nicht gefunden",
                        showBackButton = true,
                        onBackClick = onBackClick
                    )
                }
            }
        }
    }
}