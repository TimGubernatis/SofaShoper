package de.syntax_institut.androidabschlussprojekt.data.model

import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address

data class Order(
    val id: String = generateOrderId(),
    val items: List<CartItem>,
    val total: Double,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val shippingAddress: Address? = null,
    val paymentMethod: PaymentMethod? = null
) {
    companion object {
        private fun generateOrderId(): String {
            return "ORD-${System.currentTimeMillis()}"
        }
    }
}

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

enum class PaymentMethod {
    CREDIT_CARD,
    PAYPAL,
    BANK_TRANSFER,
    CASH_ON_DELIVERY
} 