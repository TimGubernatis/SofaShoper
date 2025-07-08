package de.syntax_institut.androidabschlussprojekt.data.model

import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod

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

data class OrderFirestoreModel(
    val id: String = "",
    val items: List<OrderItemFirestoreModel> = emptyList(),
    val total: Double = 0.0,
    val shippingAddressId: String = "",
    val billingAddressId: String = "",
    val status: String = "PENDING",
    val timestamp: Long = 0L
)

data class OrderItemFirestoreModel(
    val productId: Int = 0,
    val title: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0
) 