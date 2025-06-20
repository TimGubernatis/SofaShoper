package de.syntax_institut.androidabschlussprojekt.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale

@Composable
fun ImageCarousel(
    imageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(initialPage = 0) { imageUrls.size }

    Box(
        modifier = modifier
    ) {
        HorizontalPager(state = pagerState) { page ->
            AsyncImage(
                model = imageUrls[page],
                contentDescription = "Produktbild $page",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentScale = ContentScale.Crop
            )
        }


        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(imageUrls.size) { index ->
                val isSelected = pagerState.currentPage == index

                val indicatorSize by animateDpAsState(
                    targetValue = if (isSelected) 12.dp else 8.dp,
                    label = "IndicatorSize"
                )
                val indicatorColor by animateColorAsState(
                    targetValue = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.LightGray.copy(alpha = 0.5f),
                    label = "IndicatorColor"
                )

                Spacer(modifier = Modifier.width(4.dp))

                Canvas(
                    modifier = Modifier.size(indicatorSize)
                ) {
                    drawCircle(color = indicatorColor)
                }

                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}