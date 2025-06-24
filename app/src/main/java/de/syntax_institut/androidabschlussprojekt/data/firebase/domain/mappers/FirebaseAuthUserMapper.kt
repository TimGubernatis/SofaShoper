package de.syntax_institut.androidabschlussprojekt.data.firebase.domain.mappers

import com.google.firebase.auth.FirebaseUser
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User

object FirebaseAuthUserMapper {

    fun toDomain(dto: FirebaseUser): User {
        return User(
            id = dto.uid,
            email = dto.email ?: "",
            fullName = dto.displayName ?: ""
        )
    }
}