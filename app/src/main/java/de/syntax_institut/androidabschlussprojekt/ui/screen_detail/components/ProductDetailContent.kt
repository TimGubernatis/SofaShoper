package de.syntax_institut.androidabschlussprojekt.ui.screen_detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.model.Product
import java.text.NumberFormat
import java.util.*
import de.syntax_institut.androidabschlussprojekt.ui.components.ImageCarousel

@Composable
fun ProductDetailContent(
    product: Product,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locale = remember { context.resources.configuration.locales[0] }
    val currency = Currency.getInstance(locale)
    val formattedPrice = NumberFormat.getCurrencyInstance(locale).apply {
        this.currency = currency
    }.format(product.price)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        ImageCarousel(
            imageUrls = product.images,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = product.title,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        product.category?.name?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = formattedPrice,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}