package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val userRepository: UserRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        viewModelScope.launch {
            authViewModel.user.collect { currentUser ->
                if (currentUser != null) {
                    val loadedUser = userRepository.getUser(currentUser.id ?: "")
                    _user.value = loadedUser
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
        authViewModel.signOut()
    }
}