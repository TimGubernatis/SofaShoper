package de.syntax_institut.androidabschlussprojekt.ui.screen_profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, onLogout: () -> Unit)  {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Profilseite")
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onLogout) {
                Text(text = "Logout")
            }
        }
    }
}