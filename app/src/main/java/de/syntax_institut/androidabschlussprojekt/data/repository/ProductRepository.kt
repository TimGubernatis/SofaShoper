package de.syntax_institut.androidabschlussprojekt.data.repository

import de.syntax_institut.androidabschlussprojekt.data.model.Category
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import de.syntax_institut.androidabschlussprojekt.data.remote.ProductApiService

class ProductRepository(
    private val api: ProductApiService
) {

    suspend fun getAllProducts(): List<Product> = api.getProducts()

    suspend fun getAllCategoriesFromApi(): List<Category> = api.getCategories()

    suspend fun getProductById(id: Int): Product? {
        return try {
            api.getProductById(id)
        } catch (e: Exception) {
            null
        }
    }

    fun getAllCategories(products: List<Product>): List<Category> {
        return products.mapNotNull { it.category }.distinctBy { it.id }
    }
}