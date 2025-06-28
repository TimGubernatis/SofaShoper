package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.model.*
import de.syntax_institut.androidabschlussprojekt.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {
    
    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Initial)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState.asStateFlow()
    
    private val _shippingAddress = MutableStateFlow(ShippingAddress("", "", "", "", ""))
    val shippingAddress: StateFlow<ShippingAddress> = _shippingAddress.asStateFlow()
    
    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedPaymentMethod: StateFlow<PaymentMethod?> = _selectedPaymentMethod.asStateFlow()
    
    val cartItems = cartRepository.cartItems
    val cartTotal = cartRepository.cartTotal
    
    fun updateShippingAddress(address: ShippingAddress) {
        _shippingAddress.value = address
    }
    
    fun selectPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }
    
    fun validateCheckout(): Boolean {
        val address = _shippingAddress.value
        val paymentMethod = _selectedPaymentMethod.value
        
        return address.firstName.isNotBlank() &&
                address.lastName.isNotBlank() &&
                address.street.isNotBlank() &&
                address.city.isNotBlank() &&
                address.postalCode.isNotBlank() &&
                paymentMethod != null
    }
    
    fun placeOrder() {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            
            try {
                kotlinx.coroutines.delay(2000)
                
                val order = Order(
                    items = cartRepository.cartItems.value,
                    total = cartRepository.cartTotal.value,
                    shippingAddress = _shippingAddress.value,
                    paymentMethod = _selectedPaymentMethod.value
                )
                
                cartRepository.clearCart()
                
                _checkoutState.value = CheckoutState.Success(order)
            } catch (e: Exception) {
                _checkoutState.value = CheckoutState.Error("Fehler beim Bestellen: ${e.message}")
            }
        }
    }
    
    fun resetCheckout() {
        _checkoutState.value = CheckoutState.Initial
        _shippingAddress.value = ShippingAddress("", "", "", "", "")
        _selectedPaymentMethod.value = null
    }
}

sealed class CheckoutState {
    object Initial : CheckoutState()
    object Loading : CheckoutState()
    data class Success(val order: Order) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}

