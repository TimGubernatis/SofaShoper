package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.firebase.domain.models.Address

@Composable
fun ProfileAddressSection(
    title: String,
    addresses: List<Pair<String, Address>>,
    defaultAddressId: String?,
    onSetDefault: (String) -> Unit,
    onAdd: () -> Unit,
    onEdit: (String, Address) -> Unit,
    onDelete: (String) -> Unit,
    showDelete: Boolean = true
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (addresses.isEmpty()) {
                Text("Keine Adresse vorhanden.")
            } else {
                addresses.forEach { (id, address) ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        RadioButton(
                            selected = id == defaultAddressId,
                            onClick = { onSetDefault(id) }
                        )
                        Column(Modifier.weight(1f)) {
                            Text("Name: ${address.recipientFirstName} ${address.recipientLastName}")
                            Text("Straße: ${address.street} ${address.houseNumber}")
                            if (!address.addressAddition.isNullOrBlank()) Text("Zusatz: ${address.addressAddition}")
                            Text("PLZ: ${address.postalCode}")
                            Text("Stadt: ${address.city}")
                            Text("Land: ${address.country}")
                        }
                        IconButton(onClick = { onEdit(id, address) }) { Icon(Icons.Default.Edit, contentDescription = "Bearbeiten") }
                        if (showDelete && addresses.size > 1) {
                            IconButton(onClick = { onDelete(id) }) { Icon(Icons.Default.Delete, contentDescription = "Löschen") }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onAdd) { Text("Hinzufügen") }
        }
    }
} 