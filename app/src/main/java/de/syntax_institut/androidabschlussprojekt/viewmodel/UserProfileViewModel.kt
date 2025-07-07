package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
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

    init {
        viewModelScope.launch {
            authViewModel.user.collect { currentUser ->
                if (currentUser != null) {
                    val loadedUser = userRepository.getUser(currentUser.id ?: "")
                    _user.value = loadedUser
                } else {
                    _user.value = null
                }
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
            _shippingAddresses.value = userRepository.getShippingAddresses(userId)
            _billingAddresses.value = userRepository.getBillingAddresses(userId)
        }
    }

    fun addShippingAddress(userId: String, address: de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address) {
        viewModelScope.launch {
            userRepository.addShippingAddress(userId, address)
            loadAddresses(userId)
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
            userRepository.addBillingAddress(userId, address)
            loadAddresses(userId)
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
        street: String? = null,
        houseNumber: String? = null,
        addressAddition: String? = null,
        postalCode: String? = null,
        city: String? = null,
        country: String? = null
    ) {
        _addressForm.value = _addressForm.value.copy(
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
}