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

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
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

        Spacer(modifier = Modifier.height(16.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally),
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

                Canvas(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(indicatorSize)
                ) {
                    drawRoundRect(
                        color = indicatorColor,
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(x = size.minDimension / 2, y = size.minDimension / 2)
                    )
                }
            }
        }
    }
}