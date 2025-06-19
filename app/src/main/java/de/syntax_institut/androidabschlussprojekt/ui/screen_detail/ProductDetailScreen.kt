package de.syntax_institut.androidabschlussprojekt.ui.screen_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.ui.components.ErrorMessage
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton
import de.syntax_institut.androidabschlussprojekt.ui.screen_detail.components.ProductDetailContent
import de.syntax_institut.androidabschlussprojekt.viewmodel.HomeViewModel
import de.syntax_institut.androidabschlussprojekt.viewmodel.UiState
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue

@Composable
fun ProductDetailScreen(
    productId: Int,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            ErrorMessage(
                message = state.message,
                showBackButton = true
            )
        }

        is UiState.Success -> {
            val product = viewModel.getProductById(productId)
            if (product == null) {
                ErrorMessage(
                    message = "❌ Produkt mit ID $productId nicht gefunden.",
                    showBackButton = true
                )
            } else {
                Scaffold(
                    bottomBar = {
                        PrimaryButton(
                            text = "In den Warenkorb",
                            onClick = { /* TODO */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                ) { paddingValues ->
                    ProductDetailContent(
                        product = product,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}