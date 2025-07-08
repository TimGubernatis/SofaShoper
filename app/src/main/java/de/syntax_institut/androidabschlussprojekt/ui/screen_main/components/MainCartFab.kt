package de.syntax_institut.androidabschlussprojekt.ui.screen_main.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainCartFab(
    itemCount: Int,
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = onClick) {
        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
        if (itemCount > 0) {
            Text(itemCount.toString(), modifier = Modifier.padding(start = 4.dp))
        }
    }
} 