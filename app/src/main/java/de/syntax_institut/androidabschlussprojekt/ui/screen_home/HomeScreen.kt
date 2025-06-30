package de.syntax_institut.androidabschlussprojekt.ui.screen_home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.screen_home.components.CategoryRow
import de.syntax_institut.androidabschlussprojekt.ui.screen_home.components.CollapsibleSearchBar
import de.syntax_institut.androidabschlussprojekt.ui.screen_home.components.*
import de.syntax_institut.androidabschlussprojekt.ui.components.ErrorMessage
import de.syntax_institut.androidabschlussprojekt.viewmodel.HomeViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.CartViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.UiState
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel(),
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val itemCount by cartViewModel.itemCount.collectAsState()
    var isSearching by remember { mutableStateOf(false) }

    val filteredProducts by homeViewModel.filteredProducts.collectAsState()
    val user by authViewModel.user.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (!isSearching) {
                        if (user == null) {
                            Text(stringResource(id = R.string.welcome_guest))
                        } else {
                            val name = user?.firstName?.takeIf { it.isNotBlank() } ?: user?.displayName ?: "User"
                            Text(stringResource(id = R.string.welcome_user, name))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { isSearching = !isSearching }) {
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCartClick) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                if (itemCount > 0) {
                    Text(itemCount.toString(), modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (isSearching) {
                CollapsibleSearchBar(
                    query = searchQuery,
                    onQueryChange = homeViewModel::updateQuery,
                    isSearching = isSearching,
                    onSearchToggle = { isSearching = !isSearching }
                )
            }

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    ErrorMessage(
                        message = stringResource(state.messageRes),
                        showRetryButton = true,
                        onRetryClick = { homeViewModel.retryFetchData() }
                    )
                }

                is UiState.Success -> {
                    val categories = state.categories
                    CategoryRow(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategoryClick = { homeViewModel.selectCategory(it) }
                    )
                    DealOfTheDayBanner()
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onAddToCart = { cartViewModel.addToCart(product) },
                                isInCart = cartViewModel.isInCart(product.id)
                            )
                        }
                    }
                }
            }
        }
    }
}