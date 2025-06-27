package de.syntax_institut.androidabschlussprojekt.data.firebase.domain.usecases

import android.util.Log
import de.syntax_institut.androidabschlussprojekt.data.firebase.services.AuthenticationService

class SignOutUseCase(
    private val authenticationService: AuthenticationService
) {

    companion object {
        const val TAG = "SignOutUseCase"
    }

    suspend operator fun invoke() {
        Log.i(TAG, "invoke: logging out user")
        authenticationService.signOut()
    }
}