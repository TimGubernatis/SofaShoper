package de.syntax_institut.androidabschlussprojekt.ui.screen_main.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@Composable
fun DealOfTheDayBanner() {
    //TODO: fullText1 und fullText2 sollen aus der DB kommen damit ich auf der seite die aktionen Planen kann ohne die app zu überarbeiten !
    val fullText1 = "Deal of the Day"
    val fullText2 = "Bis zu 40% auf Top-Produkte"

    var displayedText1 by remember { mutableStateOf("") }
    var displayedText2 by remember { mutableStateOf("") }


    val infiniteTransition = rememberInfiniteTransition()
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color.Black,
        targetValue = Color.Red,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        while (true) {
            displayedText1 = ""
            for (i in fullText1.indices) {
                displayedText1 = fullText1.substring(0, i + 1)
                delay(100)
            }
            displayedText2 = ""
            for (i in fullText2.indices) {
                displayedText2 = fullText2.substring(0, i + 1)
                delay(50)
            }
            delay(3000)
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFC107).copy(alpha = 0.9f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🔥",
                fontSize = 30.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = displayedText1,
                    style = MaterialTheme.typography.titleMedium.copy(color = animatedColor)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = displayedText2,
                    style = MaterialTheme.typography.bodyMedium.copy(color = animatedColor)
                )
            }
        }
    }
}