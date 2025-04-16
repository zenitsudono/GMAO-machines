package com.app.gmao_machines.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationButtons(
    isFirstPage: Boolean,
    isLastPage: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onGetStarted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Previous button (left)
        if (!isFirstPage) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("Previous")
            }
        }

        // Skip button (center) - only show if not on last page
        if (!isLastPage) {
            TextButton(
                onClick = onSkip,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("Skip")
            }
        }

        // Next/Finish button (right)
        Button(
            onClick = if (isLastPage) onGetStarted else onNext,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(if (isLastPage) "Get Started" else "Next")
        }
    }
}