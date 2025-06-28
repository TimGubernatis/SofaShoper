package de.syntax_institut.androidabschlussprojekt.data.model

data class CartItem(
    val product: Product,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
) {
    val totalPrice: Double
        get() = product.price * quantity
    
    fun updateQuantity(newQuantity: Int): CartItem {
        return copy(quantity = newQuantity.coerceAtLeast(1))
    }
} 