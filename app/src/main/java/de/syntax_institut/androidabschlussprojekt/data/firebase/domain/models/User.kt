package de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String? = null,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String? = null,
    val phone: String? = null,
    val mobile: String? = null,

    val shippingAddress: Address = Address(),
    val billingAddress: Address = Address(),

    val paymentMethod: PaymentMethod? = null,

    val favorites: List<String> = emptyList(),

    val orderHistory: List<Order> = emptyList()
)

data class Address(
    val street: String = "",
    val houseNumber: String = "",
    val addressAddition: String? = null,
    val postalCode: String = "",
    val city: String = "",
    val country: String = "DE"
)

data class PaymentMethod(
    val type: String = "None",
    val email: String = "",
    val iban: String = ""
) {
    companion object {
        fun none() = PaymentMethod("None")
        fun paypal(email: String) = PaymentMethod("PayPal", email = email)
        fun iban(iban: String) = PaymentMethod("IBAN", iban = iban)
    }


}

data class Order(
    val orderId: String = "",
    val products: List<String> = emptyList(),
    val orderDate: Long = 0L,
    val status: OrderStatus = OrderStatus.PENDING,
    val trackingNumber: String? = null,
    val carrier: String? = null
)

enum class OrderStatus {
    PENDING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}