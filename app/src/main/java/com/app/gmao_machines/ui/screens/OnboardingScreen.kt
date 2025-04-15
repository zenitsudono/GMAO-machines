package com.app.gmao_machines.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.gmao_machines.data.OnboardingPage
import com.app.gmao_machines.data.onboardingPages
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { onboardingPages.size }
    )
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pager (Swipe between pages)
        HorizontalPager(
            beyondViewportPageCount = onboardingPages.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPage(
                imageRes = onboardingPages[page].imageRes,
                title = onboardingPages[page].title,
                description = onboardingPages[page].description
            )
        }
        // Dots Indicator
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            repeat(onboardingPages.size) { index ->
                Indicator(isSelected = index == pagerState.currentPage)
            }
        }
        // Buttons Row (Skip, Next, Get Started)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (pagerState.currentPage < onboardingPages.size - 1) {
                TextButton(onClick = onFinish) {
                    Text("Skip")
                }
            }

            if (pagerState.currentPage == onboardingPages.size - 1) {
                Button(onClick = onFinish) {
                    Text("Get Started")
                }
            } else {
                Button(onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }) {
                    Text("Next")
                }
            }
        }
    }
}
// Page Indicator Dots
@Composable
fun Indicator(isSelected: Boolean) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(if (isSelected) 12.dp else 8.dp)
            .background(
                if (isSelected) Color.Blue else Color.Gray.copy(alpha = 0.5f),
                shape = CircleShape
            )
    )
}