package de.syntax_institut.androidabschlussprojekt.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
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

class ProductPagingSource(
    private val api: ProductApiService
) : PagingSource<Int, Product>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val products = api.getProducts() // Passe ggf. an, falls API Paging unterstützt
            val paged = products.drop((page - 1) * pageSize).take(pageSize)
            LoadResult.Page(
                data = paged,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (paged.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? = 1
}

fun getProductPagingSource(): PagingSource<Int, Product> = ProductPagingSource(api)