package de.syntax_institut.androidabschlussprojekt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.ObserveCurrentUserUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases.SignOutUseCase
import de.syntax_institut.androidabschlussprojekt.data.firebase.services.AuthenticationService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val signOutUseCase: SignOutUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    val currentUser = observeCurrentUserUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun onSignOutClick() {
        signOutUseCase()
    }
}