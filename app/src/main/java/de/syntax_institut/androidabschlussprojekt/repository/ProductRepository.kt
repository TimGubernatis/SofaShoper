package de.syntax_institut.androidabschlussprojekt.data.repository

import de.syntax_institut.androidabschlussprojekt.data.remote.ProductApiService
import de.syntax_institut.androidabschlussprojekt.data.model.Category
import de.syntax_institut.androidabschlussprojekt.data.model.Product

class ProductRepository(
    private val api: ProductApiService
) {
    suspend fun getAllProducts(): List<Product> = api.getProducts()

    fun getAllCategories(products: List<Product>): List<Category> {
        return products.mapNotNull { it.category }.distinctBy { it.id }
    }
}