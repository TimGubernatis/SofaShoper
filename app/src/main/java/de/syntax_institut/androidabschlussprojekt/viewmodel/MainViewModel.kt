package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.model.Category
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import de.syntax_institut.androidabschlussprojekt.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import de.syntax_institut.androidabschlussprojekt.R

class MainViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

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

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    val allProducts: StateFlow<List<Product>> = _allProducts

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts

    init {
        fetchCategories()
        fetchProducts()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val categories = repository.getAllCategoriesFromApi()
                _categories.value = categories
                _uiState.value = UiState.Success(emptyList(), categories)
            } catch (e: UnknownHostException) {
                _uiState.value = UiState.Error(R.string.error_no_internet)
            } catch (e: SocketTimeoutException) {
                _uiState.value = UiState.Error(R.string.error_timeout)
            } catch (e: Exception) {
                val errorRes = when {
                    e.message?.contains("404") == true -> R.string.error_not_found
                    e.message?.contains("500") == true -> R.string.error_server
                    e.message?.contains("403") == true -> R.string.error_forbidden
                    else -> R.string.error_unknown
                }
                _uiState.value = UiState.Error(errorRes)
            }
        }
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            _productLoading.value = true
            try {
                val products = repository.getAllProducts()
                _allProducts.value = products
                filterProducts()
                _uiState.value = UiState.Success(products, _categories.value)
            } catch (e: Exception) {
                _productError.value = e.message
                _uiState.value = UiState.Error(R.string.error_order)
            } finally {
                _productLoading.value = false
            }
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
        val products = _allProducts.value
        val query = _searchQuery.value
        val category = _selectedCategory.value
        val filtered = products.filter { product ->
            val matchesQuery = product.title.contains(query, ignoreCase = true)
            val matchesCategory = category == null || product.category?.id == category.id
            matchesQuery && matchesCategory
        }
        _filteredProducts.value = filtered
    }

    suspend fun getProductById(id: Int): Product? {
        return repository.getAllProducts().find { it.id == id }
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
            } catch (e: UnknownHostException) {
                _productError.value = "❌ Keine Internetverbindung. Bitte überprüfen Sie Ihre Verbindung."
            } catch (e: SocketTimeoutException) {
                _productError.value = "❌ Zeitüberschreitung bei der Verbindung. Bitte versuchen Sie es erneut."
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("404") == true -> "❌ Produkt nicht gefunden."
                    e.message?.contains("500") == true -> "❌ Serverfehler. Bitte versuchen Sie es später erneut."
                    else -> "❌ Fehler beim Laden des Produkts: ${e.message ?: "Unbekannter Fehler"}"
                }
                _productError.value = errorMessage
            } finally {
                _productLoading.value = false
            }
        }
    }

    fun retryFetchData() {
        fetchCategories()
    }
}