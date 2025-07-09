package de.syntax_institut.androidabschlussprojekt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close

@Composable
fun SupportChatDialog(
    onDismiss: () -> Unit
) {
    var messages by remember { mutableStateOf(listOf("Willkommen beim Support! Wie können wir helfen?")) }
    var input by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(Modifier.fillMaxWidth()) {
                Text("Support-Chat", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.CenterStart))
                IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(Icons.Default.Close, contentDescription = "Schließen")
                }
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false).heightIn(max = 300.dp)
                ) {
                    items(messages) { msg ->
                        Text(msg, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Nachricht eingeben") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        messages = messages + "Du: $input"
                        messages = messages + "Support: Wir melden uns schnellstmöglich!"
                        input = ""
                    }
                },
                enabled = input.isNotBlank()
            ) {
                Text("Senden")
            }
        },
        dismissButton = {}
    )
} 