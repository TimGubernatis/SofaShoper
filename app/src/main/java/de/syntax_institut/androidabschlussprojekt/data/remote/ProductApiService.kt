package de.syntax_institut.androidabschlussprojekt.data.remote

import de.syntax_institut.androidabschlussprojekt.data.model.Product
import de.syntax_institut.androidabschlussprojekt.data.model.Category
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("categories")
    suspend fun getCategories(): List<Category>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product
}