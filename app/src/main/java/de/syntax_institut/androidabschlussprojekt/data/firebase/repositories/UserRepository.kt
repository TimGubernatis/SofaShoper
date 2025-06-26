package de.syntax_institut.androidabschlussprojekt.data.firebase.repositories

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = Firebase.firestore
    private val collection = db.collection("users")

    suspend fun saveUser(user: User) {
        collection.document(user.id!!).set(user).await()
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
}