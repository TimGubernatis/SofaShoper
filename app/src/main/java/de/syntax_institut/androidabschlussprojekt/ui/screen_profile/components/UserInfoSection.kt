package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun UserInfoSection(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    mobile: String,
    onMobileChange: (String) -> Unit,
) {
    @Composable
    fun sexyOutlinedTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        keyboardType: KeyboardType = KeyboardType.Text,
        modifier: Modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
            modifier = modifier,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        Spacer(modifier = Modifier.height(8.dp))
    }

    sexyOutlinedTextField(firstName, onFirstNameChange, "Vorname")
    sexyOutlinedTextField(lastName, onLastNameChange, "Nachname")
    sexyOutlinedTextField(email, onEmailChange, "Email", KeyboardType.Email)
    sexyOutlinedTextField(phone, onPhoneChange, "Telefon", KeyboardType.Phone)
    sexyOutlinedTextField(mobile, onMobileChange, "Handy", KeyboardType.Phone)
}