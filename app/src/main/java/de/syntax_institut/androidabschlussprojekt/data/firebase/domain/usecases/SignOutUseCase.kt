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
        try {
            Log.i(TAG, "invoke: logging out user")
            authenticationService.signOut()
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign out: ${e.message}")
        }
    }
}