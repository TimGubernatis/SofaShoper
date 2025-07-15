package de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models


data class User(
    val id: String? = null,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val displayName: String? = null,
    val phone: String? = null,
    val mobile: String? = null,
    val defaultShippingAddressId: String? = null,
    val defaultBillingAddressId: String? = null
)

enum class PaymentMethodType {
    ABBUCHUNG, UEBERWEISUNG, NACHNAHME, PAYPAL, VISA, AMAZON_PAY;
    
    companion object {
        fun fromString(value: String): PaymentMethodType {
            return when (value.uppercase()) {
                "CASH_ON_DELIVERY" -> NACHNAHME
                "CREDIT_CARD" -> VISA
                "BANK_TRANSFER" -> UEBERWEISUNG
                "PAYPAL" -> PAYPAL
                "VISA" -> VISA
                "AMAZON_PAY" -> AMAZON_PAY
                "ABBUCHUNG" -> ABBUCHUNG
                "UEBERWEISUNG" -> UEBERWEISUNG
                "NACHNAHME" -> NACHNAHME
                else -> ABBUCHUNG
            }
        }
    }
}

data class PaymentMethod(
    val type: PaymentMethodType = PaymentMethodType.ABBUCHUNG,
    val email: String = "",
    val iban: String = ""
) {
    companion object {
        fun none() = PaymentMethod(PaymentMethodType.ABBUCHUNG)
        fun paypal(email: String) = PaymentMethod(PaymentMethodType.PAYPAL, email = email)
        fun iban(iban: String) = PaymentMethod(PaymentMethodType.ABBUCHUNG, iban = iban)
        fun cashOnDelivery() = PaymentMethod(PaymentMethodType.NACHNAHME)
        fun visa() = PaymentMethod(PaymentMethodType.VISA)
        fun amazonPay() = PaymentMethod(PaymentMethodType.AMAZON_PAY)
        fun bankTransfer() = PaymentMethod(PaymentMethodType.UEBERWEISUNG)
        

        fun fromMap(map: Map<String, Any>): PaymentMethod {
            val typeString = map["type"] as? String ?: "ABBUCHUNG"
            val type = PaymentMethodType.fromString(typeString)
            val email = map["email"] as? String ?: ""
            val iban = map["iban"] as? String ?: ""
            return PaymentMethod(type, email, iban)
        }
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

