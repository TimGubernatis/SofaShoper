package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.User
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.ObserveCurrentUserUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.SignInWithGoogleUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.SignOutUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { currentUser ->
                _user.value = currentUser
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val firebaseUser: FirebaseUser? = signInWithGoogleUseCase(idToken)
                firebaseUser?.let {
                    val user = User(
                        id = it.uid,
                        email = it.email ?: "",
                        displayName = it.displayName
                    )
                    userRepository.saveUser(user)
                    _user.value = user
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unbekannter Fehler"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                val currentUser = auth.currentUser
                currentUser?.let {
                    val user = userRepository.getUser(it.uid)
                    _user.value = user
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Fehler beim Login"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, firstName: String = "", lastName: String = "", displayName: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                auth.signInWithEmailAndPassword(email, password).await()
                val currentUser = auth.currentUser
                currentUser?.let {
                    val user = User(
                        id = it.uid,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        displayName = displayName
                    )
                    userRepository.saveUser(user)
                    _user.value = user
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Fehler bei der Registrierung"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _user.value = null
        }
    }
}