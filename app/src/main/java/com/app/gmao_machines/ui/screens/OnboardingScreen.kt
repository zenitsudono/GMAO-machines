package com.app.gmao_machines.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.gmao_machines.R
import com.app.gmao_machines.data.OnboardingPage
import com.app.gmao_machines.ui.components.NavigationButtons
import com.app.gmao_machines.ui.components.OnboardingPageContent
import com.app.gmao_machines.ui.viewModel.OnboardingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    onComplete: () -> Unit
) {
    // Define onboarding pages
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to GMAO",
            description = "This application helps you monitor the status of your machines and receive real-time alerts in case of a problem.",
            imageRes = R.drawable.firstonboarding
        ),
        OnboardingPage(
            title = "Get alerted instantly",
            description = "Receive notifications as soon as an issue is detected on a machine. Access the intervention history and plan repairs efficiently.",
            imageRes = R.drawable.secondonboarding
        ),
        OnboardingPage(
            title = "Get Started",
            description = "You're all set! Start using the app now and enjoy all the features it offers.",
            imageRes = R.drawable.lastonboarding
        )
    )

    val isComplete = viewModel.isComplete.value
    val pagerState = rememberPagerState(initialPage = 0) { pages.size }
    val coroutineScope = rememberCoroutineScope()

    // Calculate progress percentage for the linear progress indicator
    val progress by remember {
        derivedStateOf {
            (pagerState.currentPage.toFloat() + pagerState.currentPageOffsetFraction) /
                    (pages.size - 1).coerceAtLeast(1).toFloat()
        }
    }

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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top progress indicator
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Page counter text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1}/${pages.size}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // ViewPager with enhanced onboarding content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                pageSpacing = 8.dp
            ) { page ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    shape = RoundedCornerShape(24.dp),
                    tonalElevation = 2.dp,
                    shadowElevation = 4.dp
                ) {
                    OnboardingPageContent(
                        page = pages[page],
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enhanced indicator with animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background track for indicators
                Surface(
                    modifier = Modifier
                        .width((10.dp + 8.dp) * pages.size)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {}

                // Animated indicator dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { pageIndex ->
                        val isSelected = pageIndex == pagerState.currentPage

                        // Animated size
                        val dotSize by animateDpAsState(
                            targetValue = if (isSelected) 12.dp else 8.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "Dot size animation"
                        )

                        // Animated alpha
                        val dotAlpha by animateFloatAsState(
                            targetValue = if (isSelected) 1f else 0.5f,
                            animationSpec = tween(300),
                            label = "Dot alpha animation"
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(dotSize)
                                .clip(CircleShape)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = dotAlpha),
                                    shape = CircleShape
                                )
                                .then(
                                    if (isSelected) Modifier.border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    ) else Modifier
                                )
                        )
                    }
                }
            }

            // Navigation buttons with improved styling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // Enhanced NavigationButtons - the component itself remains unchanged
                NavigationButtons(
                    isFirstPage = viewModel.isFirstPage(),
                    isLastPage = viewModel.isLastPage(),
                    onPrevious = { viewModel.previousPage() },
                    onNext = { viewModel.nextPage() },
                    onSkip = { 
                        // Mark onboarding complete and go straight to auth
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    onGetStarted = { 
                        // Mark onboarding complete and go straight to auth
                        viewModel.completeOnboarding()
                        onComplete()
                    }
                )
            }
        }
    }
}