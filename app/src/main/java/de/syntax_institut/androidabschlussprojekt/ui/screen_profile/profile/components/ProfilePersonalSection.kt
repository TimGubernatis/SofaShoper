package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfilePersonalSection(
    firstName: String,
    lastName: String,
    email: String,
    phone: String,
    mobile: String,
    isEditing: Boolean,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onMobileChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    isSaving: Boolean
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Persönliche Daten", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (isEditing) {
                OutlinedTextField(value = firstName, onValueChange = onFirstNameChange, label = { Text("Vorname") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = lastName, onValueChange = onLastNameChange, label = { Text("Nachname") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("E-Mail") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Telefon") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = mobile, onValueChange = onMobileChange, label = { Text("Mobil") }, modifier = Modifier.fillMaxWidth())
            } else {
                Text("Vorname: $firstName")
                Text("Nachname: $lastName")
                Text("E-Mail: $email")
                if (phone.isNotBlank()) Text("Telefon: $phone")
                if (mobile.isNotBlank()) Text("Mobil: $mobile")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!isEditing) {
                    Button(onClick = onEditClick, modifier = Modifier.weight(1f)) {
                        Text("Bearbeiten")
                    }
                } else {
                    Button(
                        onClick = onSaveClick,
                        enabled = !isSaving,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Speichern")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onCancelClick, modifier = Modifier.weight(1f)) {
                        Text("Abbrechen")
                    }
                }
            }
        }
    }
} 