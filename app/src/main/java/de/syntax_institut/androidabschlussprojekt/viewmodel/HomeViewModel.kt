package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.model.Category
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import de.syntax_institut.androidabschlussprojekt.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _products

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            val allProducts = repository.getAllProducts()
            _products.value = allProducts
            _categories.value = repository.getAllCategories(allProducts)
        }
    }

    fun updateQuery(query: String) {
        _searchQuery.value = query
        filterProducts()
    }

    fun selectCategory(category: Category?) {
        _selectedCategory.value = category
        filterProducts()
    }

    private fun filterProducts() {
        viewModelScope.launch {
            val all = repository.getAllProducts()
            val filtered = all.filter { product ->
                val matchesQuery = product.title.contains(_searchQuery.value, ignoreCase = true)
                val matchesCategory = _selectedCategory.value?.id == null ||
                        product.category?.id == _selectedCategory.value?.id

                matchesQuery && matchesCategory
            }
            _products.value = filtered
        }
    }
}