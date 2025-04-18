package com.app.gmao_machines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gmao_machines.navigation.AppNavigation
import com.app.gmao_machines.ui.theme.*
import com.app.gmao_machines.ui.viewModel.OnboardingViewModel
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.offset

class MainActivity : ComponentActivity() {
    private val viewModel: OnboardingViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            GMAOMachinesTheme {
                var showSplash by remember { mutableStateOf(true) }
                
                AnimatedVisibility(
                    visible = showSplash,
                    exit = fadeOut(animationSpec = tween(800))
                ) {
                    EnhancedSplashScreen {
                        showSplash = false
                    }
                }
                
                AnimatedVisibility(
                    visible = !showSplash,
                    enter = fadeIn(animationSpec = tween(800))
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun EnhancedSplashScreen(onSplashFinished: () -> Unit) {
    // Create animations
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animations")
    
    // Wave animation
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )
    
    // Outer ring rotation with variable speed
    val outerRingProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_ring_progress"
    )
    
    // Calculate rotation with acceleration
    val outerRingRotation = 360f * outerRingProgress * outerRingProgress
    
    // Inner ring rotation (opposite direction) with variable speed
    val innerRingProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_ring_progress"
    )
    
    // Calculate rotation with acceleration in reverse
    val innerRingRotation = 360f - (360f * innerRingProgress * innerRingProgress)
    
    // Breathing scale effect
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_breath"
    )
    
    // Alpha pulsation for glow effect with smoother transition
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    // Loading text animation
    val loadingDotsAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_dots_animation"
    )
    
    // Separate alpha values for each dot with wave pattern
    val firstDotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = CubicBezierEasing(0.2f, 0.0f, 0.8f, 1.0f)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "first_dot_alpha"
    )
    
    val secondDotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = CubicBezierEasing(0.2f, 0.0f, 0.8f, 1.0f), delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "second_dot_alpha"
    )
    
    val thirdDotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = CubicBezierEasing(0.2f, 0.0f, 0.8f, 1.0f), delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "third_dot_alpha"
    )
    
    // Scale animations for the dots with enhanced movement
    val firstDotScale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = CubicBezierEasing(0.3f, 0.0f, 0.7f, 1.0f)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "first_dot_scale"
    )
    
    val secondDotScale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = CubicBezierEasing(0.3f, 0.0f, 0.7f, 1.0f), delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "second_dot_scale"
    )
    
    val thirdDotScale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = CubicBezierEasing(0.3f, 0.0f, 0.7f, 1.0f), delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "third_dot_scale"
    )
    
    // Orbital position animations for each dot
    val dotOrbit by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dot_orbit"
    )
    
    // Fourth dot animations with unique pattern
    val fourthDotScale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = CubicBezierEasing(0.3f, 0.0f, 0.7f, 1.0f), delayMillis = 225),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fourth_dot_scale"
    )
    
    val fourthDotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = CubicBezierEasing(0.2f, 0.0f, 0.8f, 1.0f), delayMillis = 225),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fourth_dot_alpha"
    )
    
    // Particle system with improved dynamics
    val density = LocalDensity.current
    val particles = remember {
        List(50) {
            Particle(
                initialPosition = Offset(0f, 0f),
                angle = Random.nextFloat() * 2 * Math.PI.toFloat(),
                speed = Random.nextFloat() * 2.5f + 0.5f,
                size = with(density) { (Random.nextFloat() * 4f + 1f).dp.toPx() },
                color = when(Random.nextInt(5)) {
                    0 -> Primary 
                    1 -> PrimaryLight
                    2 -> Accent 
                    3 -> Secondary
                    else -> SecondaryLight
                },
                maxDistance = with(density) { (Random.nextFloat() * 120f + 30f).dp.toPx() }
            )
        }
    }
    
    // Auto dismiss after delay with entry/exit animations
    val animatedProgress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        // Entry animation
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
        
        // Wait for the desired display time
        delay(2200)
        
        // Exit animation
        animatedProgress.animateTo(
            targetValue = 0f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
        
        // Trigger navigation
        onSplashFinished()
    }
    
    // Apply scale and alpha transformations based on animation progress
    val contentScale = 0.8f + (0.2f * animatedProgress.value)
    val contentAlpha = animatedProgress.value
    
    // Main content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Surface,
                        Background,
                        SurfaceVariant
                    ),
                    center = Offset(0.5f, 0.5f),
                    radius = 1.5f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Enhanced background with gradient, circles, diagonal lines and waves
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            
            // Draw subtle circles in the background with better layering
            val circleColor = Primary.copy(alpha = 0.03f)
            for (i in 1..6) {
                val radius = (width / 12) * i
                drawCircle(
                    color = circleColor,
                    radius = radius.toFloat(),
                    center = Offset(width / 2, height / 2)
                )
            }
            
            // Draw diagonal lines with enhanced pattern
            val lineCount = 10
            val spacingX = width / lineCount
            val strokeWidth = 1.dp.toPx()
            
            for (i in 0..lineCount) {
                val x = i * spacingX
                
                // Primary lines
                drawLine(
                    color = Primary.copy(alpha = 0.02f),
                    start = Offset(x, 0f),
                    end = Offset(0f, height - x),
                    strokeWidth = strokeWidth
                )
                
                drawLine(
                    color = Primary.copy(alpha = 0.02f),
                    start = Offset(width - x, 0f),
                    end = Offset(width, height - x),
                    strokeWidth = strokeWidth
                )
                
                // Additional accent lines at half spacing
                if (i < lineCount) {
                    val halfX = x + spacingX/2
                    drawLine(
                        color = Secondary.copy(alpha = 0.015f),
                        start = Offset(halfX, 0f),
                        end = Offset(0f, height - halfX),
                        strokeWidth = strokeWidth * 0.7f
                    )
                    
                    drawLine(
                        color = Secondary.copy(alpha = 0.015f),
                        start = Offset(width - halfX, 0f),
                        end = Offset(width, height - halfX),
                        strokeWidth = strokeWidth * 0.7f
                    )
                }
            }
            
            // Draw wave patterns with improved visualization
            val waveColor1 = PrimaryLight.copy(alpha = 0.04f)
            val waveColor2 = Secondary.copy(alpha = 0.03f)
            val waveAmplitude = height * 0.02f
            val waveLength = width * 0.33f
            
            // Main waves (2 layers with different phases and positions)
            for (layer in 0..1) {
                val path = Path()
                val yPos = height * (0.25f + layer * 0.5f)
                val phaseOffset = if (layer == 0) wavePhase else -wavePhase
                val waveColor = if (layer == 0) waveColor1 else waveColor2
                
                path.moveTo(0f, yPos)
                
                for (i in 0..width.toInt() step 4) {
                    val x = i.toFloat()
                    val offsetY = sin((x / waveLength) + phaseOffset) * waveAmplitude
                    path.lineTo(x, yPos + offsetY)
                }
                
                drawPath(path, color = waveColor, style = Stroke(width = (2.5f - layer * 0.5f).dp.toPx()))
            }
            
            // Secondary waves at different positions
            for (layer in 0..1) {
                val path = Path()
                val yPos = height * (0.4f + layer * 0.3f)
                val phaseOffset = if (layer == 0) -wavePhase * 1.5f else wavePhase * 0.8f
                val waveColor = if (layer == 0) Accent.copy(alpha = 0.03f) else PrimaryDark.copy(alpha = 0.025f)
                
                path.moveTo(0f, yPos)
                
                for (i in 0..width.toInt() step 4) {
                    val x = i.toFloat()
                    val offsetY = sin((x / (waveLength * 0.7f)) + phaseOffset) * (waveAmplitude * 0.7f)
                    path.lineTo(x, yPos + offsetY)
                }
                
                drawPath(path, color = waveColor, style = Stroke(width = (1.5f - layer * 0.5f).dp.toPx()))
            }
        }
        
        // Improved particle system with better distribution
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha)
                .scale(contentScale)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            particles.forEach { particle ->
                val distance = particle.speed * 50f
                val x = centerX + cos(particle.angle) * distance
                val y = centerY + sin(particle.angle) * distance
                
                if (distance <= particle.maxDistance) {
                    val alpha = 1f - (distance / particle.maxDistance)
                    val particleSize = particle.size * (1f - distance / particle.maxDistance * 0.6f)
                    
                    drawCircle(
                        color = particle.color.copy(alpha = alpha * 0.7f),
                        radius = particleSize,
                        center = Offset(x, y)
                    )
                    
                    // Add small glow effect to particles
                    drawCircle(
                        color = particle.color.copy(alpha = alpha * 0.2f),
                        radius = particleSize * 1.8f,
                        center = Offset(x, y)
                    )
                }
            }
        }
        
        // Radial gradient background behind logo for better depth
        Box(
            modifier = Modifier
                .size(200.dp)
                .alpha(glowAlpha * contentAlpha)
                .scale(contentScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryLight.copy(alpha = 0.15f),
                            Primary.copy(alpha = 0.08f),
                            Primary.copy(alpha = 0.0f)
                        ),
                        radius = 300f
                    ),
                    shape = CircleShape
                )
        )
        
        // Glow effect behind icon
        Box(
            modifier = Modifier
                .size(160.dp)
                .alpha(glowAlpha * contentAlpha)
                .scale(contentScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.7f),
                            Primary.copy(alpha = 0.0f)
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        // Main logo with scale animation
        Image(
            painter = painterResource(id = R.drawable.splash_icon),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp)
                .scale(scale * contentScale)
                .alpha(contentAlpha)
        )
        
        // Outer rotating ring with drop shadow
        Box(
            modifier = Modifier
                .size(190.dp)
                .alpha(contentAlpha)
                .scale(contentScale)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(outerRingRotation)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = Primary.copy(alpha = 0.3f),
                        spotColor = PrimaryDark.copy(alpha = 0.2f)
                    ),
                color = Primary,
                strokeWidth = 4.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round
            )
        }
        
        // Inner rotating ring with drop shadow
        Box(
            modifier = Modifier
                .size(150.dp)
                .alpha(contentAlpha)
                .scale(contentScale)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(innerRingRotation)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        ambientColor = Secondary.copy(alpha = 0.3f),
                        spotColor = SecondaryDark.copy(alpha = 0.2f)
                    ),
                color = Secondary,
                strokeWidth = 3.dp,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round
            )
        }
        
        // Orbital dots system around the center
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha)
                .scale(contentScale),
            contentAlignment = Alignment.Center
        ) {
            // Calculate positions on orbital path
            val orbitRadius = 150.dp
            
            // First dot (blue)
            val dot1X = cos(dotOrbit) * orbitRadius.value
            val dot1Y = sin(dotOrbit) * orbitRadius.value
            
            // Second dot (green)
            val dot2X = cos(dotOrbit + Math.PI.toFloat() * 0.5f) * orbitRadius.value
            val dot2Y = sin(dotOrbit + Math.PI.toFloat() * 0.5f) * orbitRadius.value
            
            // Third dot (yellow)
            val dot3X = cos(dotOrbit + Math.PI.toFloat()) * orbitRadius.value
            val dot3Y = sin(dotOrbit + Math.PI.toFloat()) * orbitRadius.value
            
            // Fourth dot (red)
            val dot4X = cos(dotOrbit + Math.PI.toFloat() * 1.5f) * orbitRadius.value
            val dot4Y = sin(dotOrbit + Math.PI.toFloat() * 1.5f) * orbitRadius.value
            
            // First animated dot
            Box(
                modifier = Modifier
                    .offset(dot1X.dp, dot1Y.dp)
                    .size(18.dp)
                    .scale(firstDotScale)
                    .alpha(firstDotAlpha)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        spotColor = Primary
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary,
                                PrimaryLight.copy(alpha = 0.7f)
                            )
                        ),
                        shape = CircleShape
                    )
            )
            
            // Second animated dot
            Box(
                modifier = Modifier
                    .offset(dot2X.dp, dot2Y.dp)
                    .size(22.dp)
                    .scale(secondDotScale)
                    .alpha(secondDotAlpha)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        spotColor = PrimaryLight
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                PrimaryLight,
                                Primary.copy(alpha = 0.7f)
                            )
                        ),
                        shape = CircleShape
                    )
            )
            
            // Third animated dot
            Box(
                modifier = Modifier
                    .offset(dot3X.dp, dot3Y.dp)
                    .size(20.dp)
                    .scale(thirdDotScale)
                    .alpha(thirdDotAlpha)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        spotColor = Secondary
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Secondary,
                                SecondaryLight.copy(alpha = 0.7f)
                            )
                        ),
                        shape = CircleShape
                    )
            )
            
            // Fourth animated dot
            Box(
                modifier = Modifier
                    .offset(dot4X.dp, dot4Y.dp)
                    .size(16.dp)
                    .scale(fourthDotScale)
                    .alpha(fourthDotAlpha)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        spotColor = Accent
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Accent,
                                PrimaryDark.copy(alpha = 0.7f)
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        // Loading text with animated dots at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(contentAlpha),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "GMAO-Machines",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}

// Particle data class for the particle system
data class Particle(
    val initialPosition: Offset,
    val angle: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val maxDistance: Float
)


