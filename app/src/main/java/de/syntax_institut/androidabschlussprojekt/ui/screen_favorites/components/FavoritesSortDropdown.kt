package de.syntax_institut.androidabschlussprojekt.ui.screen_favorites.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            leadingIcon = {
                Icon(
                    imageVector = when (selected) {
                        FavoritesSortType.PRICE_ASC, FavoritesSortType.PRICE_DESC -> Icons.Default.AttachMoney
                        FavoritesSortType.ALPHA_ASC, FavoritesSortType.ALPHA_DESC -> Icons.Default.SortByAlpha
                    },
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            FavoritesSortType.entries.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = when (type) {
                                    FavoritesSortType.PRICE_ASC, FavoritesSortType.PRICE_DESC -> Icons.Default.AttachMoney
                                    FavoritesSortType.ALPHA_ASC, FavoritesSortType.ALPHA_DESC -> Icons.Default.SortByAlpha
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(type.label)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = when (type) {
                                    FavoritesSortType.PRICE_ASC, FavoritesSortType.ALPHA_ASC -> Icons.Default.ArrowUpward
                                    FavoritesSortType.PRICE_DESC, FavoritesSortType.ALPHA_DESC -> Icons.Default.ArrowDownward
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    onClick = {
                        onSortChange(type)
                        expanded = false
                    }
                )
            }
        }
    }
}