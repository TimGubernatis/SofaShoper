package de.syntax_institut.androidabschlussprojekt.ui.screen_profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SupportAgent
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.ProfileScreenContent
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.OrderScreenContent
import de.syntax_institut.androidabschlussprojekt.ui.screen_profile.components.SupportScreenContent
import org.koin.androidx.compose.koinViewModel

@Composable
fun AccountScreen(
    navController: NavController
) {
    val userProfileViewModel: de.syntax_institut.androidabschlussprojekt.viewmodel.UserProfileViewModel = org.koin.androidx.compose.koinViewModel()
    val user by userProfileViewModel.user.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    
    LaunchedEffect(selectedTab, user?.id) {
        if (selectedTab == 1 && user?.id != null) {
            userProfileViewModel.loadOrders(user!!.id!!)
        }
        if (selectedTab == 0 && user?.id != null) {
            userProfileViewModel.loadAddresses(user!!.id!!)
            userProfileViewModel.loadPayments(user!!.id!!)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { selectedTab = 0 }) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profil",
                        tint = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text("Profil", style = MaterialTheme.typography.labelSmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { selectedTab = 1 }) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = "Bestellungen",
                        tint = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text("Bestellungen", style = MaterialTheme.typography.labelSmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { selectedTab = 2 }) {
                    Icon(
                        imageVector = Icons.Filled.SupportAgent,
                        contentDescription = "Support",
                        tint = if (selectedTab == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                Text("Support", style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (selectedTab) {
            0 -> ProfileScreenContent(navController)
            1 -> OrderScreenContent()
            2 -> SupportScreenContent(navController)
        }
    }
} 