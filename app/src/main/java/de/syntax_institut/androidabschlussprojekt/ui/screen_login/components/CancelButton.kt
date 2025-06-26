package de.syntax_institut.androidabschlussprojekt.ui.screen_login.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.syntax_institut.androidabschlussprojekt.ui.components.PrimaryButton


@Composable
fun CancelButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    PrimaryButton(
        text = "Cancel",
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    )
}