package de.syntax_institut.androidabschlussprojekt.data.model

data class Product(
    val id: Int,
    val title: String,
    val price: Int,
    val description: String,
    val images: List<String>,
    val category: Category?
)

