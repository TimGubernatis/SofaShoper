package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import de.syntax_institut.androidabschlussprojekt.data.model.Product

class FavoritesPagingSource(
    private val allProducts: List<Product>,
    private val favoriteIds: List<Int>
) : PagingSource<Int, Product>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val favProducts = allProducts.filter { favoriteIds.contains(it.id) }
            val paged = favProducts.drop((page - 1) * pageSize).take(pageSize)
            LoadResult.Page(
                data = paged,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (paged.size < pageSize) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: androidx.paging.PagingState<Int, Product>): Int? = 1
}

fun getFavoritesPagingSource(allProducts: List<Product>, favoriteIds: List<Int>): PagingSource<Int, Product> =
    FavoritesPagingSource(allProducts, favoriteIds)

class FavoritesViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _favorites = MutableStateFlow<List<Int>>(emptyList())
    val favorites: StateFlow<List<Int>> = _favorites

    // Paging Flow für Favoriten
    fun pagedFavorites(allProducts: List<Product>): Flow<PagingData<Product>> = Pager(
        PagingConfig(pageSize = 20)
    ) {
        getFavoritesPagingSource(allProducts, _favorites.value)
    }.flow.cachedIn(viewModelScope)

    fun loadFavorites(userId: String) {
        viewModelScope.launch {
            _favorites.value = userRepository.getFavorites(userId)
        }
    }

    fun addFavorite(userId: String, productId: Int) {
        viewModelScope.launch {
            userRepository.addFavorite(userId, productId)
            loadFavorites(userId)
        }
    }

    fun removeFavorite(userId: String, productId: Int) {
        viewModelScope.launch {
            userRepository.removeFavorite(userId, productId)
            loadFavorites(userId)
        }
    }
} 