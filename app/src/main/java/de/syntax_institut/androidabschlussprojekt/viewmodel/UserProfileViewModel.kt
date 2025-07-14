package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address
import kotlinx.coroutines.tasks.await

class UserProfileViewModel(
    val authViewModel: AuthViewModel,
    private val userRepository: UserRepository
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _accountDeleted = MutableStateFlow(false)
    val accountDeleted: StateFlow<Boolean> = _accountDeleted

    private val _shippingAddresses = MutableStateFlow<List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address>>>(emptyList())
    val shippingAddresses: StateFlow<List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address>>> = _shippingAddresses

    private val _billingAddresses = MutableStateFlow<List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address>>>(emptyList())
    val billingAddresses: StateFlow<List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address>>> = _billingAddresses


    enum class AddressDialogType { NONE, ADD_SHIPPING, EDIT_SHIPPING, ADD_BILLING, EDIT_BILLING }
    private val _addressDialogType = MutableStateFlow(AddressDialogType.NONE)
    val addressDialogType: StateFlow<AddressDialogType> = _addressDialogType

    private val _addressForm = MutableStateFlow(Address())
    val addressForm: StateFlow<Address> = _addressForm

    private val _editAddressId = MutableStateFlow<String?>(null)
    val editAddressId: StateFlow<String?> = _editAddressId

    private val _paymentMethods = MutableStateFlow<List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod>>>(emptyList())
    val paymentMethods: StateFlow<List<Pair<String, de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod>>> = _paymentMethods
    private val _defaultPaymentMethodId = MutableStateFlow<String?>(null)
    val defaultPaymentMethodId: StateFlow<String?> = _defaultPaymentMethodId

    private val _defaultShippingAddressId = MutableStateFlow<String?>(null)
    val defaultShippingAddressId: StateFlow<String?> = _defaultShippingAddressId
    private val _defaultBillingAddressId = MutableStateFlow<String?>(null)
    val defaultBillingAddressId: StateFlow<String?> = _defaultBillingAddressId

    private val _orders = MutableStateFlow<List<de.syntax_institut.androidabschlussprojekt.data.model.OrderFirestoreModel>>(emptyList())
    val orders: StateFlow<List<de.syntax_institut.androidabschlussprojekt.data.model.OrderFirestoreModel>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _isOrdersLoading = MutableStateFlow(false)
    val isOrdersLoading: StateFlow<Boolean> = _isOrdersLoading

    init {
        viewModelScope.launch {
            authViewModel.user.collect { currentUser ->
                _isLoading.value = true
                if (currentUser != null) {
                    val loadedUser = userRepository.getUser(currentUser.id ?: "")
                    _user.value = loadedUser
                } else {
                    _user.value = null
                }
                _isLoading.value = false
            }
        }
    }

    fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            try {
                userRepository.saveUser(updatedUser)
                _user.value = updatedUser
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Fehler beim Speichern"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun signOut() {
        println("UserProfileViewModel: signOut aufgerufen")
        authViewModel.signOut()
    }

    fun deleteUserAccount() {
        viewModelScope.launch {
            try {
                authViewModel.deleteAccount()
                _accountDeleted.value = true
            } catch (e: Exception) {
                _accountDeleted.value = false
            }
        }
    }

    fun loadAddresses(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _shippingAddresses.value = userRepository.getShippingAddresses(userId)
            _billingAddresses.value = userRepository.getBillingAddresses(userId)
            checkAndSetDefaultAddresses(userId)
            _isLoading.value = false
        }
    }

    private fun checkAndSetDefaultAddresses(userId: String) {
        val user = _user.value
        if (user != null) {
            if (user.defaultShippingAddressId == null && _shippingAddresses.value.isNotEmpty()) {
                setDefaultShippingAddress(userId, _shippingAddresses.value.first().first)
            } else {
                _defaultShippingAddressId.value = user.defaultShippingAddressId
            }
            if (user.defaultBillingAddressId == null && _billingAddresses.value.isNotEmpty()) {
                setDefaultBillingAddress(userId, _billingAddresses.value.first().first)
            } else {
                _defaultBillingAddressId.value = user.defaultBillingAddressId
            }
        }
    }

    fun addShippingAddress(userId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        viewModelScope.launch {
            val id = userRepository.addShippingAddress(userId, address)
            loadAddresses(userId)
            if (_shippingAddresses.value.size == 1) {
                setDefaultShippingAddress(userId, id)
            }
        }
    }

    fun updateShippingAddress(userId: String, addressId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        viewModelScope.launch {
            userRepository.updateShippingAddress(userId, addressId, address)
            loadAddresses(userId)
        }
    }

    fun deleteShippingAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            userRepository.deleteShippingAddress(userId, addressId)
            loadAddresses(userId)
        }
    }

    fun addBillingAddress(userId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        viewModelScope.launch {
            val id = userRepository.addBillingAddress(userId, address)
            loadAddresses(userId)
            if (_billingAddresses.value.size == 1) {
                setDefaultBillingAddress(userId, id)
            }
        }
    }

    fun updateBillingAddress(userId: String, addressId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        viewModelScope.launch {
            userRepository.updateBillingAddress(userId, addressId, address)
            loadAddresses(userId)
        }
    }

    fun deleteBillingAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            userRepository.deleteBillingAddress(userId, addressId)
            loadAddresses(userId)
        }
    }

    fun openAddShippingAddress() {
        _addressDialogType.value = AddressDialogType.ADD_SHIPPING
        _addressForm.value = Address()
        _editAddressId.value = null
    }
    fun openEditShippingAddress(id: String, address: Address) {
        _addressDialogType.value = AddressDialogType.EDIT_SHIPPING
        _addressForm.value = address
        _editAddressId.value = id
    }
    fun openAddBillingAddress() {
        _addressDialogType.value = AddressDialogType.ADD_BILLING
        _addressForm.value = Address()
        _editAddressId.value = null
    }
    fun openEditBillingAddress(id: String, address: Address) {
        _addressDialogType.value = AddressDialogType.EDIT_BILLING
        _addressForm.value = address
        _editAddressId.value = id
    }
    fun closeAddressDialog() {
        _addressDialogType.value = AddressDialogType.NONE
        _editAddressId.value = null
    }
    fun setAddressFormField(
        recipientFirstName: String? = null,
        recipientLastName: String? = null,
        street: String? = null,
        houseNumber: String? = null,
        addressAddition: String? = null,
        postalCode: String? = null,
        city: String? = null,
        country: String? = null
    ) {
        _addressForm.value = _addressForm.value.copy(
            recipientFirstName = recipientFirstName ?: _addressForm.value.recipientFirstName,
            recipientLastName = recipientLastName ?: _addressForm.value.recipientLastName,
            street = street ?: _addressForm.value.street,
            houseNumber = houseNumber ?: _addressForm.value.houseNumber,
            addressAddition = addressAddition ?: _addressForm.value.addressAddition,
            postalCode = postalCode ?: _addressForm.value.postalCode,
            city = city ?: _addressForm.value.city,
            country = country ?: _addressForm.value.country
        )
    }
    fun saveAddress(userId: String) {
        when (_addressDialogType.value) {
            AddressDialogType.ADD_SHIPPING -> addShippingAddress(userId, _addressForm.value)
            AddressDialogType.EDIT_SHIPPING -> {
                _editAddressId.value?.let { updateShippingAddress(userId, it, _addressForm.value) }
            }
            AddressDialogType.ADD_BILLING -> addBillingAddress(userId, _addressForm.value)
            AddressDialogType.EDIT_BILLING -> {
                _editAddressId.value?.let { updateBillingAddress(userId, it, _addressForm.value) }
            }
            else -> {}
        }
        closeAddressDialog()
    }
    fun validateAddress(): Boolean {
        val a = _addressForm.value
        return a.street.isNotBlank() && a.houseNumber.isNotBlank() && a.postalCode.isNotBlank() && a.city.isNotBlank() && a.country.isNotBlank()
    }

    fun loadPayments(userId: String) {
        viewModelScope.launch {
            val snapshot = userRepository.getPayments(userId)
            val paymentDocs = firestore.collection("users").document(userId).collection("payments").get().await().documents
            _paymentMethods.value = paymentDocs.mapNotNull { doc ->
                val payment = doc.toObject(de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod::class.java)
                if (payment != null) Pair(doc.id, payment) else null
            }
            if (_defaultPaymentMethodId.value == null && _paymentMethods.value.isNotEmpty()) {
                _defaultPaymentMethodId.value = _paymentMethods.value.first().first
            }
        }
    }

    fun addPayment(userId: String, payment: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod) {
        viewModelScope.launch {
            userRepository.addPayment(userId, payment)
            loadPayments(userId)
        }
    }

    fun updatePayment(userId: String, paymentId: String, payment: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.PaymentMethod) {
        viewModelScope.launch {
            firestore.collection("users").document(userId).collection("payments").document(paymentId).set(payment).await()
            loadPayments(userId)
        }
    }

    fun deletePayment(userId: String, paymentId: String) {
        viewModelScope.launch {
            firestore.collection("users").document(userId).collection("payments").document(paymentId).delete().await()
            loadPayments(userId)
        }
    }

    fun setDefaultPaymentMethod(paymentId: String) {
        _defaultPaymentMethodId.value = paymentId
    }

    fun setDefaultShippingAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            userRepository.setDefaultShippingAddress(userId, addressId)
            _defaultShippingAddressId.value = addressId
            _user.value = _user.value?.copy(defaultShippingAddressId = addressId)
        }
    }

    fun setDefaultBillingAddress(userId: String, addressId: String) {
        viewModelScope.launch {
            userRepository.setDefaultBillingAddress(userId, addressId)
            _defaultBillingAddressId.value = addressId
            _user.value = _user.value?.copy(defaultBillingAddressId = addressId)
        }
    }

    fun loadOrders(userId: String) {
        viewModelScope.launch {
            _isOrdersLoading.value = true
            _orders.value = userRepository.getOrders(userId)
            _isOrdersLoading.value = false
        }
    }
}