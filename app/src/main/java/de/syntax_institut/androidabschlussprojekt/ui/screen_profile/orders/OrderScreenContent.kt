package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.orders

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.orders.components.OrderList
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment

@Composable
fun OrderScreenContent(
    userProfileViewModel: UserProfileViewModel = koinViewModel()
) {
    val orders = userProfileViewModel.orders.collectAsState().value
    val isOrdersLoading = userProfileViewModel.isOrdersLoading.collectAsState().value
    if (isOrdersLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    OrderList(orders = orders, modifier = Modifier.padding(16.dp))
}