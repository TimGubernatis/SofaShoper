package de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models

// Address-Modell für Liefer- und Rechnungsadressen (Firestore: Subcollections)
data class Address(
    val recipientFirstName: String = "",
    val recipientLastName: String = "",
    val street: String = "",
    val houseNumber: String = "",
    val addressAddition: String? = null,
    val postalCode: String = "",
    val city: String = "",
    val country: String = "",
    val phone: String? = null,
    val mobile: String? = null
) 