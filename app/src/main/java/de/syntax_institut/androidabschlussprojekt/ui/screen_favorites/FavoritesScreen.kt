package de.syntax_institut.androidabschlussprojekt.ui.screen_favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.FavoritesViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.ui.screen_home.components.ProductCard
import de.syntax_institut.androidabschlussprojekt.viewmodel.HomeViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.CartViewModel

@Composable
fun FavoritesScreen(
    authViewModel: AuthViewModel = koinViewModel(),
    favoritesViewModel: FavoritesViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel()
) {
    val user by authViewModel.user.collectAsState()
    val favorites by favoritesViewModel.favorites.collectAsState()
    val favoriteProducts by favoritesViewModel.favoriteProducts.collectAsState()
    val allProducts by homeViewModel.allProducts.collectAsState()
    //val cartItems by cartViewModel.cartItems.collectAsState()

    LaunchedEffect(user?.id, allProducts) {
        user?.id?.let { favoritesViewModel.loadFavorites(it, allProducts) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Keine Favoriten vorhanden")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(favoriteProducts) { product ->
                    ProductCard(
                        product = product,
                        onClick = {},
                        onAddToCart = { cartViewModel.addToCart(product) },
                        isInCart = cartViewModel.isInCart(product.id)
                    )
                }
            }
        }
    }
}