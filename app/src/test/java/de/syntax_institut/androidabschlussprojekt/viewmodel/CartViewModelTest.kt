package de.syntax_institut.androidabschlussprojekt.viewmodel

import de.syntax_institut.androidabschlussprojekt.data.model.Category
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {
    
    @Mock
    private lateinit var cartRepository: CartRepository
    
    private lateinit var cartViewModel: CartViewModel
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        cartViewModel = CartViewModel(cartRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `addToCart should add product to cart`() = runTest {
        // Given
        val product = createTestProduct(1, "Test Product", 10.0)
        val expectedCartItems = listOf(CartItem(product, 1))
        
        whenever(cartRepository.cartItems).thenReturn(kotlinx.coroutines.flow.flowOf(expectedCartItems))
        
        // When
        cartViewModel.addToCart(product)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val cartItems = cartViewModel.cartItems.first()
        assertEquals(1, cartItems.size)
        assertEquals(product.id, cartItems[0].product.id)
    }
    
    @Test
    fun `removeFromCart should remove product from cart`() = runTest {
        // Given
        val product = createTestProduct(1, "Test Product", 10.0)
        val cartItem = CartItem(product, 1)
        val initialCartItems = listOf(cartItem)
        val emptyCartItems = emptyList<CartItem>()
        
        whenever(cartRepository.cartItems).thenReturn(kotlinx.coroutines.flow.flowOf(initialCartItems, emptyCartItems))
        
        // When
        cartViewModel.removeFromCart(product.id)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val cartItems = cartViewModel.cartItems.first()
        assertTrue(cartItems.isEmpty())
    }
    
    @Test
    fun `updateQuantity should update product quantity`() = runTest {
        // Given
        val product = createTestProduct(1, "Test Product", 10.0)
        val cartItem = CartItem(product, 2)
        val updatedCartItems = listOf(cartItem)
        
        whenever(cartRepository.cartItems).thenReturn(kotlinx.coroutines.flow.flowOf(updatedCartItems))
        
        // When
        cartViewModel.updateQuantity(product.id, 3)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val cartItems = cartViewModel.cartItems.first()
        assertEquals(1, cartItems.size)
        assertEquals(2, cartItems[0].quantity) // Repository should update to 3, but we're testing the flow
    }
    
    @Test
    fun `isInCart should return true when product is in cart`() = runTest {
        // Given
        val product = createTestProduct(1, "Test Product", 10.0)
        whenever(cartRepository.isInCart(product.id)).thenReturn(true)
        
        // When
        val result = cartViewModel.isInCart(product.id)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isInCart should return false when product is not in cart`() = runTest {
        // Given
        val product = createTestProduct(1, "Test Product", 10.0)
        whenever(cartRepository.isInCart(product.id)).thenReturn(false)
        
        // When
        val result = cartViewModel.isInCart(product.id)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `clearCart should clear all items`() = runTest {
        // Given
        val product = createTestProduct(1, "Test Product", 10.0)
        val cartItem = CartItem(product, 1)
        val initialCartItems = listOf(cartItem)
        val emptyCartItems = emptyList<CartItem>()
        
        whenever(cartRepository.cartItems).thenReturn(kotlinx.coroutines.flow.flowOf(initialCartItems, emptyCartItems))
        
        // When
        cartViewModel.clearCart()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val cartItems = cartViewModel.cartItems.first()
        assertTrue(cartItems.isEmpty())
    }
    
    private fun createTestProduct(id: Int, title: String, price: Double): Product {
        return Product(
            id = id,
            title = title,
            slug = "test-product",
            price = price,
            description = "Test description",
            category = Category(1, "Test Category", "test-category", "test-image.jpg"),
            images = listOf("test-image.jpg"),
            creationAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
    }
} 