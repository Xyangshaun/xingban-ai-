package com.example.myspatial.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.draw.rotate

object AnimationDurations {
    val instant = 100
    val fast = 200
    val normal = 300
    val slow = 500
    val extraSlow = 800
    val verySlow = 1200
}

@Composable
fun Modifier.fadeIn(
    delay: Int = 0,
    duration: Int = AnimationDurations.normal
): Modifier {
    val alpha by animateFloatAsState(
        1f,
        animationSpec = tween(duration, delay, LinearEasing),
        label = "fadeIn"
    )
    return this.alpha(alpha)
}

@Composable
fun Modifier.fadeOut(
    delay: Int = 0,
    duration: Int = AnimationDurations.normal
): Modifier {
    val alpha by animateFloatAsState(
        0f,
        animationSpec = tween(duration, delay, LinearEasing),
        label = "fadeOut"
    )
    return this.alpha(alpha)
}

@Composable
fun Modifier.scaleIn(
    delay: Int = 0,
    duration: Int = AnimationDurations.normal
): Modifier {
    val scale by animateFloatAsState(
        1f,
        animationSpec = tween(duration, delay, FastOutSlowInEasing),
        label = "scaleIn"
    )
    return this.scale(scale)
}

@Composable
fun Modifier.popIn(
    delay: Int = 0,
    duration: Int = AnimationDurations.fast
): Modifier {
    return this.scaleIn(delay, duration)
}

@Composable
fun Modifier.bounceIn(
    delay: Int = 0,
    duration: Int = AnimationDurations.slow
): Modifier {
    val scale by animateFloatAsState(
        1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounceIn"
    )
    return this.scale(scale)
}

@Composable
fun Modifier.pulse(
    duration: Int = 1500
): Modifier {
    val scale by animateFloatAsState(
        1.05f,
        animationSpec = infiniteRepeatable(
            tween(duration),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )
    return this.scale(scale)
}

@Composable
fun Modifier.hoverScale(
    isHovered: Boolean,
    duration: Int = AnimationDurations.fast
): Modifier {
    val targetScale = if (isHovered) 1.05f else 1f
    val scale by animateFloatAsState(
        targetScale,
        animationSpec = tween(duration, easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)),
        label = "hoverScale"
    )
    return this.scale(scale)
}

@Composable
fun Modifier.slideInFromBottom(
    delay: Int = 0,
    duration: Int = AnimationDurations.normal
): Modifier {
    val offsetY by animateIntAsState(
        0,
        animationSpec = tween(duration, delay, FastOutSlowInEasing),
        label = "slideInFromBottom"
    )
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(IntOffset(0, offsetY))
        }
    }
}

@Composable
fun Modifier.slideInFromTop(
    delay: Int = 0,
    duration: Int = AnimationDurations.normal
): Modifier {
    val offsetY by animateIntAsState(
        0,
        animationSpec = tween(duration, delay, FastOutSlowInEasing),
        label = "slideInFromTop"
    )
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(IntOffset(0, offsetY))
        }
    }
}

@Composable
fun Modifier.slideInFromStart(
    delay: Int = 0,
    duration: Int = AnimationDurations.normal
): Modifier {
    val offsetX by animateIntAsState(
        0,
        animationSpec = tween(duration, delay, FastOutSlowInEasing),
        label = "slideInFromStart"
    )
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(IntOffset(offsetX, 0))
        }
    }
}

@Composable
fun Modifier.slideInFromEnd(
    delay: Int = 0,
    duration: Int = AnimationDurations.normal
): Modifier {
    val offsetX by animateIntAsState(
        0,
        animationSpec = tween(duration, delay, FastOutSlowInEasing),
        label = "slideInFromEnd"
    )
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(IntOffset(offsetX, 0))
        }
    }
}

@Composable
fun Modifier.shake(
    duration: Int = AnimationDurations.fast
): Modifier {
    val offsetX by animateFloatAsState(
        0f,
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(IntOffset((offsetX * 10).toInt(), 0))
        }
    }
}

@Composable
fun Modifier.shimmer(
    duration: Int = AnimationDurations.slow
): Modifier {
    val shimmerOffset by animateFloatAsState(
        1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    return this.background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0x40FFFFFF),
                Color(0x80FFFFFF),
                Color(0x40FFFFFF)
            ),
            start = Offset(-Float.MAX_VALUE, 0f),
            end = Offset(Float.MAX_VALUE, 0f)
        )
    )
}

@Composable
fun Modifier.floatUpDown(
    duration: Int = AnimationDurations.verySlow
): Modifier {
    val offsetY by animateFloatAsState(
        (-10).toFloat(),
        animationSpec = infiniteRepeatable(
            tween(duration),
            RepeatMode.Reverse
        ),
        label = "floatUpDown"
    )
    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(IntOffset(0, offsetY.toInt()))
        }
    }
}

@Composable
fun Modifier.rotateIn(
    delay: Int = 0,
    duration: Int = AnimationDurations.normal
): Modifier {
    val rotation by animateFloatAsState(
        0f,
        animationSpec = tween(duration, delay, FastOutSlowInEasing),
        label = "rotateIn"
    )
    return this.scale(1f).rotate(rotation)
}

@Composable
fun Modifier.glow(
    isGlowing: Boolean,
    color: Color = Color(0xFF6366F1),
    duration: Int = AnimationDurations.fast
): Modifier {
    val alpha by animateFloatAsState(
        if (isGlowing) 0.5f else 0f,
        animationSpec = tween(duration),
        label = "glow"
    )
    return this
}

@Composable
fun Modifier.switchScale(
    isOn: Boolean,
    duration: Int = AnimationDurations.fast
): Modifier {
    val scale by animateFloatAsState(
        if (isOn) 1.1f else 1f,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "switchScale"
    )
    return this.scale(scale)
}