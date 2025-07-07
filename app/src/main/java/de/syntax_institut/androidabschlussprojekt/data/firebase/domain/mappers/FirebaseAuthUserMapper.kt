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
        return User(
            id = data["id"] as? String,
            email = data["email"] as? String ?: "",
            firstName = data["firstName"] as? String ?: "",
            lastName = data["lastName"] as? String ?: "",
            displayName = data["displayName"] as? String,
            phone = data["phone"] as? String,
            mobile = data["mobile"] as? String
        )
    }

    fun toMap(user: User): Map<String, Any?> {
        return mapOf(
            "id" to user.id,
            "email" to user.email,
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "displayName" to user.displayName,
            "phone" to user.phone,
            "mobile" to user.mobile
        )
    }
}