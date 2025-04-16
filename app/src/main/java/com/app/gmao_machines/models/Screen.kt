package com.app.gmao_machines.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector, val title: String) {
    object Home : Screen("home", Icons.Rounded.Home, "Home")
    object History : Screen("history", Icons.Rounded.History, "History")
    object Profile : Screen("profile", Icons.Rounded.Person, "Profile")
}