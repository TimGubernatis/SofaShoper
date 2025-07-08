package de.syntax_institut.androidabschlussprojekt.ui.screen_login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.AlertDialog

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCancel: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }
    var passwordRepeat by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordRepeatVisible by remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val user by authViewModel.user.collectAsState()
    val passwordResetMessage by authViewModel.passwordResetMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(user) {
        if (user != null) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(isRegisterMode) {
        authViewModel.clearError()
    }

    LaunchedEffect(passwordResetMessage) {
        passwordResetMessage?.let {
            snackbarHostState.showSnackbar(it)
            authViewModel.clearPasswordResetMessage()
        }
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
            },
            passwordVisible = passwordVisible,
            onPasswordVisibleChange = { passwordVisible = it }
        )
        if (isRegisterMode) {
            Spacer(Modifier.height(8.dp))
            TextField(
                value = passwordRepeat,
                onValueChange = { passwordRepeat = it },
                label = { Text("Passwort wiederholen") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordRepeatVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordRepeatVisible = !passwordRepeatVisible }) {
                        Icon(
                            imageVector = if (passwordRepeatVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordRepeatVisible) "Verbergen" else "Anzeigen"
                        )
                    }
                }
            )
        }
        if (showPasswordDialog) {
            Text(
                text = "Die Passwörter stimmen nicht überein!",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        AuthButtons(
            isRegisterMode = isRegisterMode,
            onToggleMode = { isRegisterMode = !isRegisterMode },
            onLogin = { authViewModel.signIn(email, password) },
            onRegister = {
                if (password != passwordRepeat) {
                    showPasswordDialog = true
                } else {
                    showPasswordDialog = false
                    authViewModel.register(email, password)
                }
            },
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
        Spacer(Modifier.height(4.dp))
        TextButton(
            onClick = {
                authViewModel.sendPasswordResetEmail(email)
            },
            enabled = !isLoading && email.isNotBlank()
        ) {
            Text("Passwort vergessen?")
        }
    }


    if (passwordResetMessage != null) {
        AlertDialog(
            onDismissRequest = { authViewModel.clearPasswordResetMessage() },
            confirmButton = {
                TextButton(onClick = { authViewModel.clearPasswordResetMessage() }) {
                    Text("OK")
                }
            },
            title = { Text("Passwort zurücksetzen") },
            text = { Text(passwordResetMessage ?: "") }
        )
    }

    SnackbarHost(hostState = snackbarHostState)
}
