package de.syntax_institut.androidabschlussprojekt.ui.screen_main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.syntax_institut.androidabschlussprojekt.ui.screen_main.components.CategoryRow
import de.syntax_institut.androidabschlussprojekt.ui.screen_main.components.CollapsibleSearchBar
import de.syntax_institut.androidabschlussprojekt.ui.screen_main.components.ProductCard
import de.syntax_institut.androidabschlussprojekt.ui.screen_main.components.DealOfTheDayBanner
import de.syntax_institut.androidabschlussprojekt.ui.components.ErrorMessage
import de.syntax_institut.androidabschlussprojekt.viewmodel.CartViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.UiState
import org.koin.androidx.compose.koinViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel
import androidx.compose.ui.res.stringResource
import de.syntax_institut.androidabschlussprojekt.viewmodel.FavoritesViewModel
import de.syntax_institut.androidabschlussprojekt.util.responsivePadding
import de.syntax_institut.androidabschlussprojekt.util.responsiveSpacing
import de.syntax_institut.androidabschlussprojekt.util.responsiveTextFieldSpacing
import de.syntax_institut.androidabschlussprojekt.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.items
import de.syntax_institut.androidabschlussprojekt.data.model.Category
import de.syntax_institut.androidabschlussprojekt.ui.screen_main.components.MainTopBar
import de.syntax_institut.androidabschlussprojekt.ui.screen_main.components.MainCartFab
import de.syntax_institut.androidabschlussprojekt.ui.screen_main.components.MainCartSnackbar
import de.syntax_institut.androidabschlussprojekt.viewmodel.NotificationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import de.syntax_institut.androidabschlussprojekt.data.notification.OfferNotificationWorker
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import de.syntax_institut.androidabschlussprojekt.ui.components.NotificationOptInDialog
import androidx.activity.compose.BackHandler
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel(),
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel(),
    favoritesViewModel: FavoritesViewModel = koinViewModel()
) {
    val activity = LocalContext.current as? Activity
    BackHandler {
        activity?.finish()
    }
    val uiState by mainViewModel.uiState.collectAsState()
    val selectedCategory by mainViewModel.selectedCategory.collectAsState()
    val selectedCategoryId = selectedCategory?.id ?: -1
    val searchQuery by mainViewModel.searchQuery.collectAsState()
    val itemCount by cartViewModel.itemCount.collectAsState()
    var isSearching by remember { mutableStateOf(false) }

    val filteredProducts by mainViewModel.filteredProducts.collectAsState()
    val user by authViewModel.user.collectAsState()
    val favorites by favoritesViewModel.favorites.collectAsState()
    val allProducts by mainViewModel.allProducts.collectAsState()

    val cartTotal by cartViewModel.cartTotal.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    var bottomBarTotal by remember { mutableDoubleStateOf(0.0) }
    var bottomBarCount by remember { mutableIntStateOf(0) }
    var pendingProductName by remember { mutableStateOf<String?>(null) }
    var showCartBar by remember { mutableStateOf(false) }

    val currentUser = user
    val userName = if (currentUser == null) null else (currentUser.firstName.takeIf { it.isNotBlank() } ?: currentUser.displayName ?: "User")

    LaunchedEffect(currentUser?.id, allProducts) {
        currentUser?.id?.let { userId ->
            favoritesViewModel.loadFavorites(userId, allProducts)
        }
    }


    fun onProductAddedToCart(productName: String, productPrice: Double) {
        pendingProductName = productName
        bottomBarTotal += productPrice
        bottomBarCount += 1
    }


    LaunchedEffect(cartItems) {
        if (cartItems.isEmpty()) {
            bottomBarTotal = 0.0
            bottomBarCount = 0
        }
    }


    LaunchedEffect(cartTotal, cartItems) {
        if (pendingProductName != null) {
            showCartBar = true
            delay(5000)
            showCartBar = false
            pendingProductName = null
        }
    }

    val categories = when (val state = uiState) {
        is UiState.Success -> listOf(Category(-1, "Alle", "all", "", "", "")) + state.categories
        else -> emptyList()
    }
    val selectedCategoryOrNull = if (selectedCategoryId == -1) null else selectedCategory

    val notificationViewModel: NotificationViewModel = viewModel()
    val showOptInDialog by notificationViewModel.askAgain.collectAsState()
    var dialogVisible by remember { mutableStateOf(showOptInDialog) }

    Scaffold(
        topBar = {
            MainTopBar(
                isSearching = isSearching,
                userName = userName,
                onSearchClick = { isSearching = !isSearching },
                onProfileClick = onProfileClick,
                onFavoritesClick = onFavoritesClick
            )
        },
        floatingActionButton = {
            MainCartFab(itemCount = itemCount, onClick = onCartClick)
        },
        bottomBar = {
            if (showCartBar && pendingProductName != null) {
                MainCartSnackbar(
                    productName = pendingProductName,
                    total = bottomBarTotal,
                    count = bottomBarCount
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = responsivePadding())
        ) {
            if (dialogVisible && showOptInDialog) {
                NotificationOptInDialog(
                    viewModel = notificationViewModel,
                    onDismiss = { dialogVisible = false }
                )
            }
            if (isSearching) {
                CollapsibleSearchBar(
                    query = searchQuery,
                    onQueryChange = mainViewModel::updateQuery,
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
                        onRetryClick = { mainViewModel.retryFetchData() }
                    )
                }

                is UiState.Success -> {
                    CategoryRow(
                        categories = categories,
                        selectedCategory = categories.find { it.id == selectedCategoryId } ?: categories.firstOrNull(),
                        onCategoryClick = {
                            if (it.id == -1) mainViewModel.selectCategory(null) else mainViewModel.selectCategory(it)
                        }
                    )
                    DealOfTheDayBanner()
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = responsivePadding(), vertical = responsiveTextFieldSpacing()),
                        verticalArrangement = Arrangement.spacedBy(responsiveSpacing())
                    ) {
                        items(filteredProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onAddToCart = {
                                    cartViewModel.addToCart(product)
                                    onProductAddedToCart(product.title, product.price)
                                },
                                isInCart = cartViewModel.isInCart(product.id),
                                isFavorite = currentUser?.id != null && favorites.contains(product.id),
                                onFavoriteClick = currentUser?.id?.let { userId ->
                                    {
                                        if (favorites.contains(product.id)) {
                                            favoritesViewModel.removeFavorite(userId, product.id, allProducts)
                                        } else {
                                            favoritesViewModel.addFavorite(userId, product.id, allProducts)
                                        }
                                    }
                                },
                                onLoginRequired = if (currentUser?.id == null) {
                                    { onProfileClick() }
                                } else null,
                                cartTotal = cartTotal
                            )
                        }
                    }
                }
            }
        }
    }
}