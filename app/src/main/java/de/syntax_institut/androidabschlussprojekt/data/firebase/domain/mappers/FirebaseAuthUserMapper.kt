package de.syntax_institut.androidabschlussprojekt.data.firebase.domain.mappers

import com.google.firebase.auth.FirebaseUser
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.*

object FirebaseAuthUserMapper {

    fun toDomain(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName,

        )
    }

    fun fromMap(data: Map<String, Any?>): User {

        val shippingMap = data["shippingAddress"] as? Map<String, Any?> ?: emptyMap()
        val billingMap = data["billingAddress"] as? Map<String, Any?> ?: emptyMap()
        val paymentMap = data["paymentMethod"] as? Map<String, Any?>

        val shippingAddress = Address(
            street = shippingMap["street"] as? String ?: "",
            houseNumber = shippingMap["houseNumber"] as? String ?: "",
            addressAddition = shippingMap["addressAddition"] as? String,
            postalCode = shippingMap["postalCode"] as? String ?: "",
            city = shippingMap["city"] as? String ?: "",
            country = shippingMap["country"] as? String ?: "DE"
        )

        val billingAddress = Address(
            street = billingMap["street"] as? String ?: "",
            houseNumber = billingMap["houseNumber"] as? String ?: "",
            addressAddition = billingMap["addressAddition"] as? String,
            postalCode = billingMap["postalCode"] as? String ?: "",
            city = billingMap["city"] as? String ?: "",
            country = billingMap["country"] as? String ?: "DE"
        )

        val paymentMethod = when (paymentMap?.get("type")) {
            "PayPal" -> PaymentMethod.PayPal(paymentMap["email"] as? String ?: "")
            "IBAN" -> PaymentMethod.IBAN(paymentMap["iban"] as? String ?: "")
            else -> PaymentMethod.None
        }

        val favorites = data["favorites"] as? List<String> ?: emptyList()

        val ordersList = data["orderHistory"] as? List<Map<String, Any?>> ?: emptyList()
        val orderHistory = ordersList.map { orderMap ->
            Order(
                orderId = orderMap["orderId"] as? String ?: "",
                products = orderMap["products"] as? List<String> ?: emptyList(),
                orderDate = (orderMap["orderDate"] as? Long) ?: 0L,
                status = OrderStatus.valueOf(orderMap["status"] as? String ?: "PENDING"),
                trackingNumber = orderMap["trackingNumber"] as? String,
                carrier = orderMap["carrier"] as? String
            )
        }

        return User(
            id = data["id"] as? String,
            email = data["email"] as? String ?: "",
            firstName = data["firstName"] as? String ?: "",
            lastName = data["lastName"] as? String ?: "",
            displayName = data["displayName"] as? String,
            phone = data["phone"] as? String,
            mobile = data["mobile"] as? String,
            shippingAddress = shippingAddress,
            billingAddress = billingAddress,
            paymentMethod = paymentMethod,
            favorites = favorites,
            orderHistory = orderHistory
        )
    }

    fun toMap(user: User): Map<String, Any?> {
        val paymentMap = when (user.paymentMethod) {
            is PaymentMethod.PayPal -> mapOf("type" to "PayPal", "email" to user.paymentMethod.email)
            is PaymentMethod.IBAN -> mapOf("type" to "IBAN", "iban" to user.paymentMethod.iban)
            else -> null
        }

        return mapOf(
            "id" to user.id,
            "email" to user.email,
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "displayName" to user.displayName,
            "phone" to user.phone,
            "mobile" to user.mobile,
            "shippingAddress" to mapOf(
                "street" to user.shippingAddress.street,
                "houseNumber" to user.shippingAddress.houseNumber,
                "addressAddition" to user.shippingAddress.addressAddition,
                "postalCode" to user.shippingAddress.postalCode,
                "city" to user.shippingAddress.city,
                "country" to user.shippingAddress.country
            ),
            "billingAddress" to mapOf(
                "street" to user.billingAddress.street,
                "houseNumber" to user.billingAddress.houseNumber,
                "addressAddition" to user.billingAddress.addressAddition,
                "postalCode" to user.billingAddress.postalCode,
                "city" to user.billingAddress.city,
                "country" to user.billingAddress.country
            ),
            "paymentMethod" to paymentMap,
            "favorites" to user.favorites,
            "orderHistory" to user.orderHistory.map { order ->
                mapOf(
                    "orderId" to order.orderId,
                    "products" to order.products,
                    "orderDate" to order.orderDate,
                    "status" to order.status.name,
                    "trackingNumber" to order.trackingNumber,
                    "carrier" to order.carrier
                )
            }
        )
    }
}