package de.syntax_institut.androidabschlussprojekt.ui.screen_login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextButton

@Composable
fun AuthButtons(
    isRegisterMode: Boolean,
    onToggleMode: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    isLoading: Boolean
) {
    Column {
        if (isRegisterMode) {
            PrimaryButton(
                text = "Registrieren",
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onToggleMode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Bereits ein Konto? Login hier")
            }
        } else {
            PrimaryButton(
                text = "Login",
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onToggleMode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Noch kein Konto? Registrieren")
            }
        }
    }
}