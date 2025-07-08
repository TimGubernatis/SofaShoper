package de.syntax_institut.androidabschlussprojekt.ui.screen_favorites.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

enum class FavoritesSortType(val label: String) {
    PRICE_ASC("Preis aufsteigend"),
    PRICE_DESC("Preis absteigend"),
    ALPHA_ASC("A-Z"),
    ALPHA_DESC("Z-A")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesSortDropdown(
    selected: FavoritesSortType,
    onSortChange: (FavoritesSortType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Sortieren") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            FavoritesSortType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.label) },
                    onClick = {
                        onSortChange(type)
                        expanded = false
                    }
                )
            }
        }
    }
} 