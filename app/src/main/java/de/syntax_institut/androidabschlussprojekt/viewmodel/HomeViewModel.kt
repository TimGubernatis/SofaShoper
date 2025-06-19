package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.model.Category
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import de.syntax_institut.androidabschlussprojekt.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ProductRepository) : ViewModel() {


    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var filterJob: Job? = null


    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    private val _productLoading = MutableStateFlow(false)
    val productLoading: StateFlow<Boolean> = _productLoading

    private val _productError = MutableStateFlow<String?>(null)
    val productError: StateFlow<String?> = _productError

    init {
        fetchData()
    }


    fun fetchData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val products = repository.getAllProducts()
                val categories = repository.getAllCategoriesFromApi()

                _allProducts.value = products
                _categories.value = categories
                _uiState.value = UiState.Success(products, categories)

                scheduleFilter()
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Fehler beim Laden: ${e.message ?: "Unbekannter Fehler"}")
            }
        }
    }


    fun updateQuery(query: String) {
        _searchQuery.value = query
        scheduleFilter()
    }

    fun selectCategory(category: Category?) {
        _selectedCategory.value = category
        scheduleFilter()
    }

    private fun scheduleFilter() {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            delay(300)
            val query = _searchQuery.value
            val category = _selectedCategory.value
            val filtered = _allProducts.value.filter { product ->
                val matchesQuery = product.title.contains(query, ignoreCase = true)
                val matchesCategory = category == null || product.category?.id == category.id
                matchesQuery && matchesCategory
            }
            _filteredProducts.value = filtered
        }
    }


    fun getProductById(id: Int): Product? {
        return _allProducts.value.find { it.id == id }
    }


    fun loadProductById(id: Int) {
        viewModelScope.launch {
            _productLoading.value = true
            _productError.value = null
            _selectedProduct.value = null
            try {
                val product = repository.getProductById(id)
                if (product != null) {
                    _selectedProduct.value = product
                } else {
                    _productError.value = "❌ Produkt mit ID $id nicht gefunden."
                }
            } catch (e: Exception) {
                _productError.value = "❌ Fehler bei Produkt-ID $id: ${e.message}"
            } finally {
                _productLoading.value = false
            }
        }
    }
}