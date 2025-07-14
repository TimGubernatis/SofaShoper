package de.syntax_institut.androidabschlussprojekt.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.syntax_institut.androidabschlussprojekt.ui.screen_detail.ProductDetailScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen_main.MainScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.LoginScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen_favorites.FavoritesScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen_cart.CartScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen_checkout.CheckoutScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.AccountScreen
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainApp(authViewModel: AuthViewModel = koinViewModel()) {
    val navController = rememberNavController()
    val user by authViewModel.user.collectAsState()
    val isUserLoggedIn = user != null

    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "home") {

            composable("home") {
                MainScreen(
                    onProductClick = { productId -> navController.navigate("productDetail/$productId") },
                    onCartClick = { navController.navigate("cart") },
                    onProfileClick = {
                        if (isUserLoggedIn) navController.navigate("profile") else navController.navigate("login")
                    },
                    onFavoritesClick = {
                        if (isUserLoggedIn) navController.navigate("favorites") else navController.navigate("login")
                    }
                )
            }

            composable("productDetail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                productId?.let { 
                    ProductDetailScreen(
                        productId = it,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable("profile") {
                if (isUserLoggedIn) {
                    AccountScreen(navController = navController)
                } else {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() }
                    )
                }
            }

            composable("favorites") {
                if (isUserLoggedIn) {
                    FavoritesScreen(
                        onBackClick = { navController.popBackStack() },
                        onProductClick = { productId -> navController.navigate("productDetail/$productId") }
                    )
                } else {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() }
                    )
                }
            }

            composable("cart") {
                CartScreen(
                    onBackClick = { navController.popBackStack() },
                    onCheckoutClick = { navController.navigate("checkout") }
                )
            }

            composable("checkout") {
                CheckoutScreen(
                    onBackClick = { navController.popBackStack() },
                    onOrderSuccess = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                )
            }
        }
    }
}