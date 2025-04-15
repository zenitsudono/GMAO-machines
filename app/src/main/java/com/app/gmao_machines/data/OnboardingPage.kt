package com.app.gmao_machines.data

import com.app.gmao_machines.R

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnboardingPage(
        imageRes = R.drawable.ic_launcher_foreground,
        title = "Title 1",
        description = "Description 1"
    ),
    OnboardingPage(
        imageRes = R.drawable.ic_launcher_foreground,
        title = "Title 2",
        description = "Description 2"
    ),
    OnboardingPage(
        imageRes = R.drawable.ic_launcher_foreground,
        title = "Title 3",
        description = "Description 3"
    ),
)
