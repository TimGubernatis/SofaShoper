package de.syntax_institut.androidabschlussprojekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.syntax_institut.androidabschlussprojekt.ui.screen_home.HomeScreen
import de.syntax_institut.androidabschlussprojekt.ui.theme.AndroidAbschlussprojektTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidAbschlussprojektTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    Surface(modifier = Modifier.fillMaxSize()) {
        // Navigation kannst du später einbauen – aktuell nur HomeScreen
        HomeScreen(
            onProductClick = { productId ->
                // TODO: Implementiere Navigation zu DetailScreen
            },
            onCartClick = {
                // TODO: Navigation zum Warenkorb
            },
            onProfileClick = {
                // TODO: Navigation zum Profil
            }
        )
    }
}