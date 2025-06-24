package de.syntax_institut.androidabschlussprojekt.data.firebase.repositories

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepository {

    private val db = Firebase.firestore
    private val collection = db.collection("users")

    fun saveUser(user: User) {
        collection.document(user.id!!).set(user)
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