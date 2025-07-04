package de.syntax_institut.androidabschlussprojekt.data.model

data class Order(
    val id: String = generateOrderId(),
    val items: List<CartItem>,
    val total: Double,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val shippingAddress: ShippingAddress? = null,
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

data class ShippingAddress(
    val firstName: String,
    val lastName: String,
    val street: String,
    val city: String,
    val postalCode: String,
    val country: String = "Deutschland",
    val phone: String? = null,
    val houseNumber: String? = null,
    val addressAddition: String? = null
)

enum class PaymentMethod {
    CREDIT_CARD,
    PAYPAL,
    BANK_TRANSFER,
    CASH_ON_DELIVERY
} 