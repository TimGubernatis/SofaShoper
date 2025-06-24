package de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String? = "",
    val email: String = "",
    val fullName: String = ""
)