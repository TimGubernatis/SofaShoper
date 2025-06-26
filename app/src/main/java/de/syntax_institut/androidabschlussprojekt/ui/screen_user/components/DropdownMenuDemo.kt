package de.syntax_institut.androidabschlussprojekt.ui.screen_user.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropdownMenuDemo(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(
            text = selectedMethod,
            modifier = Modifier
                .clickable { expanded = true }
                .padding(8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    onMethodSelected("None")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("PayPal") },
                onClick = {
                    onMethodSelected("PayPal")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("IBAN") },
                onClick = {
                    onMethodSelected("IBAN")
                    expanded = false
                }
            )
        }
    }
}