package de.syntax_institut.androidabschlussprojekt.data.firebase.repositories

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.model.Favorite
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = Firebase.firestore
    private val collection = db.collection("users")

    suspend fun saveUser(user: User) {
        val userMap = de.syntax_institut.androidabschlussprojekt.data.firebase.domain.mappers.FirebaseAuthUserMapper.toMap(user)
        collection.document(user.id!!).set(userMap).await()
    }

    suspend fun getUser(id: String): User? {
        val doc = collection.document(id).get().await()
        return doc.toObject(User::class.java)
    }

    suspend fun updateUserFields(id: String, fields: Map<String, Any>) {
        collection.document(id).update(fields).await()
    }

    fun observeUser(id: String): Flow<User?> = callbackFlow {
        val listenerRegistration = collection.document(id).addSnapshotListener { documentSnapshot, error ->
            if (documentSnapshot == null) {
                trySend(null)
            } else {
                val maybeUser = documentSnapshot.toObject(User::class.java)
                trySend(maybeUser)
            }
        }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    suspend fun addFavorite(userId: String, productId: Int) {
        collection.document(userId)
            .collection("Favorites")
            .document(productId.toString())
            .set(Favorite(productId))
            .await()
    }

    suspend fun removeFavorite(userId: String, productId: Int) {
        collection.document(userId)
            .collection("Favorites")
            .document(productId.toString())
            .delete()
            .await()
    }

    suspend fun getFavorites(userId: String): List<Int> {
        val snapshot = collection.document(userId).collection("Favorites").get().await()
        return snapshot.documents.mapNotNull { it.getLong("productId")?.toInt() }
    }

    suspend fun deleteUserCompletely(userId: String) {
        val userDoc = collection.document(userId)

        val favorites = userDoc.collection("Favorites").get().await()
        for (fav in favorites.documents) {
            fav.reference.delete().await()
        }

        userDoc.delete().await()
    }


    suspend fun addShippingAddress(userId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address): String {
        val docRef = collection.document(userId)
            .collection("LieferAddresses")
            .add(address)
            .await()
        return docRef.id
    }

    suspend fun getShippingAddresses(userId: String): List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address>> {
        val snapshot = collection.document(userId).collection("LieferAddresses").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val address = doc.toObject(de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address::class.java)
            if (address != null) Pair(doc.id, address) else null
        }
    }

    suspend fun updateShippingAddress(userId: String, addressId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        collection.document(userId)
            .collection("LieferAddresses")
            .document(addressId)
            .set(address)
            .await()
    }

    suspend fun deleteShippingAddress(userId: String, addressId: String) {
        collection.document(userId)
            .collection("LieferAddresses")
            .document(addressId)
            .delete()
            .await()
    }


    suspend fun addBillingAddress(userId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address): String {
        val docRef = collection.document(userId)
            .collection("RechnungsAdresses")
            .add(address)
            .await()
        return docRef.id
    }

    suspend fun getBillingAddresses(userId: String): List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address>> {
        val snapshot = collection.document(userId).collection("RechnungsAdresses").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val address = doc.toObject(de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address::class.java)
            if (address != null) Pair(doc.id, address) else null
        }
    }

    suspend fun updateBillingAddress(userId: String, addressId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        collection.document(userId)
            .collection("RechnungsAdresses")
            .document(addressId)
            .set(address)
            .await()
    }

    suspend fun deleteBillingAddress(userId: String, addressId: String) {
        collection.document(userId)
            .collection("RechnungsAdresses")
            .document(addressId)
            .delete()
            .await()
    }

    suspend fun addPayment(userId: String, payment: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod) {
        collection.document(userId)
            .collection("payments")
            .add(payment)
            .await()
    }

    suspend fun getPayments(userId: String): List<de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod> {
        val snapshot = collection.document(userId).collection("payments").get().await()
        return snapshot.documents.mapNotNull { doc ->
            try {
                
                doc.toObject(de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod::class.java)
            } catch (e: Exception) {

                try {
                    val data = doc.data
                    if (data != null) {
                        de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod.fromMap(data)
                    } else null
                } catch (e2: Exception) {
                    null
                }
            }
        }
    }

    suspend fun setDefaultShippingAddress(userId: String, addressId: String) {
        collection.document(userId).update("defaultShippingAddressId", addressId).await()
    }

    suspend fun setDefaultBillingAddress(userId: String, addressId: String) {
        collection.document(userId).update("defaultBillingAddressId", addressId).await()
    }

    suspend fun addOrder(
        userId: String,
        items: List<de.syntax_institut.androidabschlussprojekt.data.model.CartItem>,
        total: Double,
        shippingAddressId: String,
        billingAddressId: String,
        status: String = "PENDING",
        timestamp: Long = System.currentTimeMillis()
    ) {
        val orderData = hashMapOf(
            "items" to items.map { item ->
                hashMapOf(
                    "productId" to item.product.id,
                    "title" to item.product.title,
                    "price" to item.product.price,
                    "quantity" to item.quantity
                )
            },
            "total" to total,
            "shippingAddressId" to shippingAddressId,
            "billingAddressId" to billingAddressId,
            "status" to status,
            "timestamp" to timestamp
        )
        collection.document(userId)
            .collection("Bestellungen")
            .add(orderData)
            .await()
    }

    suspend fun getOrders(userId: String): List<de.syntax_institut.androidabschlussprojekt.data.model.OrderFirestoreModel> {
        val snapshot = collection.document(userId).collection("Bestellungen").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            val items = (data["items"] as? List<Map<String, Any?>>)?.map { itemMap ->
                de.syntax_institut.androidabschlussprojekt.data.model.OrderItemFirestoreModel(
                    productId = (itemMap["productId"] as? Long)?.toInt() ?: 0,
                    title = itemMap["title"] as? String ?: "",
                    price = (itemMap["price"] as? Double) ?: 0.0,
                    quantity = (itemMap["quantity"] as? Long)?.toInt() ?: 0
                )
            } ?: emptyList()
            de.syntax_institut.androidabschlussprojekt.data.model.OrderFirestoreModel(
                id = doc.id,
                items = items,
                total = (data["total"] as? Double) ?: 0.0,
                shippingAddressId = data["shippingAddressId"] as? String ?: "",
                billingAddressId = data["billingAddressId"] as? String ?: "",
                status = data["status"] as? String ?: "PENDING",
                timestamp = (data["timestamp"] as? Long) ?: 0L
            )
        }
    }
}