package de.syntax_institut.androidabschlussprojekt.data.repository

import de.syntax_institut.androidabschlussprojekt.data.model.CartItem
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CartRepository {
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val mutex = Mutex()
    
    val cartTotal: StateFlow<Double>
        get() = MutableStateFlow(_cartItems.value.sumOf { it.totalPrice }).asStateFlow()
    
    val itemCount: StateFlow<Int>
        get() = MutableStateFlow(_cartItems.value.sumOf { it.quantity }).asStateFlow()
    
    suspend fun addToCart(product: Product, quantity: Int = 1) {
        mutex.withLock {
            _cartItems.update { currentItems ->
                val existingItem = currentItems.find { it.product.id == product.id }
                if (existingItem != null) {

                    currentItems.map { item ->
                        if (item.product.id == product.id) {
                            item.updateQuantity(item.quantity + quantity)
                        } else {
                            item
                        }
                    }
                } else {

                    currentItems + CartItem(product, quantity)
                }
            }
        }
    }
    
    suspend fun removeFromCart(productId: Int) {
        mutex.withLock {
            _cartItems.update { currentItems ->
                currentItems.filter { it.product.id != productId }
            }
        }
    }
    
    suspend fun updateQuantity(productId: Int, newQuantity: Int) {
        mutex.withLock {
            if (newQuantity <= 0) {
                removeFromCart(productId)
                return
            }
            
            _cartItems.update { currentItems ->
                currentItems.map { item ->
                    if (item.product.id == productId) {
                        item.updateQuantity(newQuantity)
                    } else {
                        item
                    }
                }
            }
        }
    }
    
    suspend fun clearCart() {
        mutex.withLock {
            _cartItems.value = emptyList()
        }
    }
    
    fun getCartItem(productId: Int): CartItem? {
        return _cartItems.value.find { it.product.id == productId }
    }
    
    fun isInCart(productId: Int): Boolean {
        return _cartItems.value.any { it.product.id == productId }
    }
    
    fun getCartSize(): Int {
        return _cartItems.value.size
    }
    
    fun getTotalItems(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }
    
    fun getTotalPrice(): Double {
        return _cartItems.value.sumOf { it.totalPrice }
    }
} 