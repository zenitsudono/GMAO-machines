package com.app.gmao_machines.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.gmao_machines.data.OnboardingPage
import com.app.gmao_machines.ui.viewModel.OnboardingViewModel
import com.app.gmao_machines.R
import com.app.gmao_machines.ui.components.NavigationButtons
import com.app.gmao_machines.ui.components.OnboardingPageContent
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    onComplete: () -> Unit
) {
    // Define onboarding pages
    val pages = listOf(
        OnboardingPage(
            title = "Welcome",
            description = "Welcome to our app! Swipe to learn more.",
            imageRes = R.drawable.splash_icon
        ),
        OnboardingPage(
            title = "Features",
            description = "Discover all the amazing features our app offers.",
            imageRes = R.drawable.splash_icon
        ),
        OnboardingPage(
            title = "Get Started",
            description = "You're all set! Start using the app now.",
            imageRes = R.drawable.splash_icon
        )
    )

    val pagerState = rememberPagerState(initialPage = 0) { pages.size }
    val coroutineScope = rememberCoroutineScope()

    // Sync pagerState with viewModel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.goToPage(pagerState.currentPage)
    }

    LaunchedEffect(viewModel.currentPage.value) {
        if (pagerState.currentPage != viewModel.currentPage.value) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(viewModel.currentPage.value)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // ViewPager with onboarding content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            OnboardingPageContent(
                page = pages[page]
            )
        }

        // Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { pageIndex ->
                val isSelected = pageIndex == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 12.dp else 10.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                )
            }
        }

        // Navigation buttons
        NavigationButtons(
            isFirstPage = viewModel.isFirstPage(),
            isLastPage = viewModel.isLastPage(),
            onPrevious = { viewModel.previousPage() },
            onNext = {
                if (viewModel.isLastPage()) {
                    onComplete()
                } else {
                    viewModel.nextPage()
                }
            },
            onSkip = { onComplete() }
        )
    }
}
