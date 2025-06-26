package de.syntax_institut.androidabschlussprojekt.ui.screen_login.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    PrimaryButton(
        text = "Mit Google anmelden",
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    )
}