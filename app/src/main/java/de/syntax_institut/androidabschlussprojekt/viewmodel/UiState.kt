package de.syntax_institut.androidabschlussprojekt.viewmodel

import de.syntax_institut.androidabschlussprojekt.data.model.Product
import de.syntax_institut.androidabschlussprojekt.data.model.Category
import androidx.annotation.StringRes

sealed class UiState {
    object Loading : UiState()
    data class Success(
        val products: List<Product>,
        val categories: List<Category>
    ) : UiState()
    data class Error(@StringRes val messageRes: Int) : UiState()
}