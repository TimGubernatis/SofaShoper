package de.syntax_institut.androidabschlussprojekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import de.syntax_institut.androidabschlussprojekt.ui.theme.AndroidAbschlussprojektTheme
import com.google.firebase.FirebaseApp
import de.syntax_institut.androidabschlussprojekt.ui.navigation.MainApp

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase initialisieren (falls noch nicht geschehen)
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            // Firebase bereits initialisiert
        }

        enableEdgeToEdge()
        setContent {
            AndroidAbschlussprojektTheme {
                MainApp()
            }
        }
    }
}

