package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address
import de.syntax_institut.androidabschlussprojekt.ui.components.CountryPicker
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel

@Composable
fun AddressDialog(
    dialogType: UserProfileViewModel.AddressDialogType,
    addressForm: Address,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onFieldChange: (Address) -> Unit,
    isValid: Boolean
) {
    if (dialogType == UserProfileViewModel.AddressDialogType.NONE) return

    val isEdit = dialogType == UserProfileViewModel.AddressDialogType.EDIT_SHIPPING || 
                 dialogType == UserProfileViewModel.AddressDialogType.EDIT_BILLING
    val isBilling = dialogType == UserProfileViewModel.AddressDialogType.ADD_BILLING || 
                    dialogType == UserProfileViewModel.AddressDialogType.EDIT_BILLING
    
    val title = when {
        isEdit && isBilling -> "Rechnungsadresse bearbeiten"
        isEdit && !isBilling -> "Lieferadresse bearbeiten"
        !isEdit && isBilling -> "Rechnungsadresse hinzufügen"
        else -> "Lieferadresse hinzufügen"
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, tonalElevation = 8.dp) {
            Column(Modifier.padding(24.dp).widthIn(min = 320.dp, max = 480.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = addressForm.recipientFirstName,
                    onValueChange = { onFieldChange(addressForm.copy(recipientFirstName = it)) },
                    label = { Text("Vorname") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = addressForm.recipientLastName,
                    onValueChange = { onFieldChange(addressForm.copy(recipientLastName = it)) },
                    label = { Text("Nachname") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = addressForm.street,
                    onValueChange = { onFieldChange(addressForm.copy(street = it)) },
                    label = { Text("Straße") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = addressForm.houseNumber,
                    onValueChange = { onFieldChange(addressForm.copy(houseNumber = it)) },
                    label = { Text("Hausnummer") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = addressForm.addressAddition ?: "",
                    onValueChange = { onFieldChange(addressForm.copy(addressAddition = it.takeIf { it.isNotBlank() })) },
                    label = { Text("Adresszusatz") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = addressForm.postalCode,
                    onValueChange = { onFieldChange(addressForm.copy(postalCode = it)) },
                    label = { Text("PLZ") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = addressForm.city,
                    onValueChange = { onFieldChange(addressForm.copy(city = it)) },
                    label = { Text("Stadt") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                
                CountryPicker(
                    value = addressForm.country,
                    onValueChange = { onFieldChange(addressForm.copy(country = it)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onDismiss) { Text("Abbrechen") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onSave, enabled = isValid) { Text("Speichern") }
                }
            }
        }
    }
} 