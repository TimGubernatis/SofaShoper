package de.syntax_institut.androidabschlussprojekt.ui.screen_favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.FavoritesViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.ui.screen_main.components.ProductCard
import de.syntax_institut.androidabschlussprojekt.viewmodel.MainViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.CartViewModel
import de.syntax_institut.androidabschlussprojekt.ui.screen_favorites.components.FavoritesSortDropdown
import de.syntax_institut.androidabschlussprojekt.ui.screen_favorites.components.FavoritesSortType
import de.syntax_institut.androidabschlussprojekt.ui.screen_favorites.components.EmptyFavoritesView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit,
    authViewModel: AuthViewModel = koinViewModel(),
    favoritesViewModel: FavoritesViewModel = koinViewModel(),
    mainViewModel: MainViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel()
) {
    val user by authViewModel.user.collectAsState()
    val userId = user?.id
    val favoriteProducts by favoritesViewModel.favoriteProducts.collectAsState()
    val allProducts by mainViewModel.allProducts.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    var sortType by remember { mutableStateOf(FavoritesSortType.ALPHA_ASC) }
    var favoritesLoaded by remember(userId) { mutableStateOf(false) }

    LaunchedEffect(userId, allProducts) {
        if (userId != null && allProducts.isNotEmpty() && !favoritesLoaded) {
            favoritesViewModel.loadFavorites(userId, allProducts)
            favoritesLoaded = true
        }
    }

    val sortedFavorites = remember(favoriteProducts, sortType) {
        when (sortType) {
            FavoritesSortType.PRICE_ASC -> favoriteProducts.sortedBy { it.price }
            FavoritesSortType.PRICE_DESC -> favoriteProducts.sortedByDescending { it.price }
            FavoritesSortType.ALPHA_ASC -> favoriteProducts.sortedBy { it.title }
            FavoritesSortType.ALPHA_DESC -> favoriteProducts.sortedByDescending { it.title }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoriten") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FavoritesSortDropdown(
                selected = sortType,
                onSortChange = { sortType = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            if (sortedFavorites.isEmpty()) {
                EmptyFavoritesView()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedFavorites, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            onAddToCart = { cartViewModel.addToCart(product) },
                            isInCart = cartViewModel.isInCart(product.id),
                            isFavorite = true,
                            onFavoriteClick = userId?.let { uid ->
                                { favoritesViewModel.removeFavorite(uid, product.id, allProducts) }
                            },
                            onLoginRequired = null,
                            cartTotal = cartItems.sumOf { it.product.price * it.quantity }
                        )
                    }
                }
            }
        }
    }
}