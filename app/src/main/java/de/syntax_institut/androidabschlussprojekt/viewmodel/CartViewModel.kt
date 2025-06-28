package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.model.CartItem
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import de.syntax_institut.androidabschlussprojekt.data.repository.CartRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {
    
    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItems
    val cartTotal: StateFlow<Double> = cartRepository.cartTotal
    val itemCount: StateFlow<Int> = cartRepository.itemCount
    
    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            cartRepository.addToCart(product, quantity)
        }
    }
    
    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
        }
    }
    
    fun updateQuantity(productId: Int, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(productId, newQuantity)
        }
    }
    
    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
    
    fun isInCart(productId: Int): Boolean {
        return cartRepository.isInCart(productId)
    }
    
    fun getCartItem(productId: Int): CartItem? {
        return cartRepository.getCartItem(productId)
    }
}

