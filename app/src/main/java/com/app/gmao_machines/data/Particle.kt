package com.app.gmao_machines.data

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class Particle(
    val initialPosition: Offset,
    val angle: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val maxDistance: Float
)