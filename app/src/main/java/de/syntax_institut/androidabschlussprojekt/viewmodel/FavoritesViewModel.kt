package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _favorites = MutableStateFlow<List<Int>>(emptyList())
    val favorites: StateFlow<List<Int>> = _favorites

    private val _favoriteProducts = MutableStateFlow<List<Product>>(emptyList())
    val favoriteProducts: StateFlow<List<Product>> = _favoriteProducts

    fun loadFavorites(userId: String, allProducts: List<Product>) {
        viewModelScope.launch {
            val ids = userRepository.getFavorites(userId)
            _favorites.value = ids
            _favoriteProducts.value = allProducts.filter { ids.contains(it.id) }
        }
    }

    fun addFavorite(userId: String, productId: Int, allProducts: List<Product>) {
        viewModelScope.launch {
            userRepository.addFavorite(userId, productId)
            loadFavorites(userId, allProducts)
        }
    }

    fun removeFavorite(userId: String, productId: Int, allProducts: List<Product>) {
        viewModelScope.launch {
            userRepository.removeFavorite(userId, productId)
            loadFavorites(userId, allProducts)
        }
    }
} 