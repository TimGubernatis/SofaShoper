package de.syntax_institut.androidabschlussprojekt.ui.screen_login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import de.syntax_institut.androidabschlussprojekt.viewmodel.AuthViewModel
import de.syntax_institut.androidabschlussprojekt.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onCancel: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val user by authViewModel.user.collectAsState()


    LaunchedEffect(user) {
        if (user != null) {
            onLoginSuccess()
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
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { authViewModel.signIn(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Login")
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { launcher.launch(googleSignInClient.signInIntent) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Mit Google anmelden")
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Cancel")
        }
        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}