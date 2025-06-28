package de.syntax_institut.androidabschlussprojekt.ui.screen_login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.components.AuthButtons
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.components.CancelButton
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.components.EmailPasswordInput
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.components.ErrorMessageLogin
import de.syntax_institut.androidabschlussprojekt.ui.screen_login.components.GoogleSignInButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCancel: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val user by authViewModel.user.collectAsState()

    LaunchedEffect(user) {
        if (user != null) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(isRegisterMode) {
        authViewModel.clearError()
    }

    val context = LocalContext.current
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    authViewModel.signInWithGoogle(account.idToken ?: "")
                }
            } catch (e: ApiException) {
                // Google Sign-In Fehler wird bereits im AuthViewModel behandelt
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        EmailPasswordInput(
            email = email,
            onEmailChange = { 
                email = it
                if (errorMessage != null) {
                    authViewModel.clearError()
                }
            },
            password = password,
            onPasswordChange = { 
                password = it
                if (errorMessage != null) {
                    authViewModel.clearError()
                }
            }
        )
        Spacer(Modifier.height(16.dp))

        AuthButtons(
            isRegisterMode = isRegisterMode,
            onToggleMode = { isRegisterMode = !isRegisterMode },
            onLogin = { authViewModel.signIn(email, password) },
            onRegister = { authViewModel.register(email, password) },
            isLoading = isLoading
        )
        Spacer(Modifier.height(8.dp))
        GoogleSignInButton(
            onClick = { launcher.launch(googleSignInClient.signInIntent) },
            enabled = !isLoading
        )
        Spacer(Modifier.height(8.dp))
        CancelButton(
            onClick = onCancel,
            enabled = !isLoading
        )
        ErrorMessageLogin(
            errorMessage = errorMessage,
            onDismiss = { authViewModel.clearError() }
        )
    }
}
