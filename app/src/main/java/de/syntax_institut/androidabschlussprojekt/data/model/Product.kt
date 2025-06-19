package de.syntax_institut.androidabschlussprojekt.data.model

data class Product(
    val id: Int,
    val title: String,
    val slug: String,
    val price: Int,
    val description: String,
    val category: Category?,
    val images: List<String>,
    val creationAt: String,
    val updatedAt: String
)