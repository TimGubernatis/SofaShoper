package de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.OrderList

@Composable
fun OrderScreenContent(
    userProfileViewModel: UserProfileViewModel = koinViewModel()
) {
    val orders = userProfileViewModel.orders.collectAsState().value
    OrderList(orders = orders, modifier = Modifier.padding(16.dp))
}