package com.example.myspatial.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object StarBrand {
    val primary = Color(0xFF6366F1)
    val primaryLight = Color(0xFF818CF8)
    val primaryDark = Color(0xFF4F46E5)
    
    val secondary = Color(0xFFEC4899)
    val secondaryLight = Color(0xFFF472B6)
    val secondaryDark = Color(0xFFDB2777)
    
    val tertiary = Color(0xFF06B6D4)
    val tertiaryLight = Color(0xFF22D3EE)
    
    val accentOrange = Color(0xFFF97316)
    val accentYellow = Color(0xFFFBBF24)
    val accentGreen = Color(0xFF10B981)
    val accentPurple = Color(0xFF8B5CF6)
    
    val success = Color(0xFF10B981)
    val warning = Color(0xFFF59E0B)
    val error = Color(0xFFEF4444)
    val info = Color(0xFF3B82F6)
    
    val background = Color(0xFFF8FAFC)
    val backgroundWarm = Color(0xFFFFFDF5)
    val surface = Color(0xFFFFFFFF)
    val surfaceVariant = Color(0xFFF1F5F9)
    val surfaceElevated = Color(0xFFFFFFFF)
    val card = Color(0xFFFFFFFF)
    
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF475569)
    val textTertiary = Color(0xFF94A3B8)
    val textPlaceholder = Color(0xFFCBD5E1)
    val textInverse = Color(0xFFFFFFFF)
    
    val border = Color(0xFFE2E8F0)
    val borderLight = Color(0xFFF1F5F9)
    val borderFocused = Color(0xFF6366F1)
    
    val gradientStart = Color(0xFF6366F1)
    val gradientEnd = Color(0xFFEC4899)
    
    val companionColors = mapOf(
        "star" to Color(0xFFFBBF24),
        "moon" to Color(0xFFA5B4FC),
        "sun" to Color(0xFFFB923C),
        "cloud" to Color(0xFFCBD5E1),
        "flame" to Color(0xFFFB7185)
    )
    
    val moodColors = mapOf(
        "happy" to Color(0xFFFBBF24),
        "calm" to Color(0xFF60A5FA),
        "sad" to Color(0xFF93C5FD),
        "angry" to Color(0xFFFCA5A5),
        "scared" to Color(0xFFDDD6FE),
        "missing" to Color(0xFFFBCFE8),
        "lonely" to Color(0xFFE2E8F0),
        "confused" to Color(0xFFFEF3C7)
    )
    
    val moodEmojis = mapOf(
        "happy" to "😊",
        "calm" to "😌",
        "sad" to "😢",
        "angry" to "😠",
        "scared" to "😨",
        "missing" to "🥺",
        "lonely" to "🥀",
        "confused" to "😕"
    )
    
    val shadowColor = Color(0x1A000000)
    val shadowColorMedium = Color(0x26000000)
    val shadowColorStrong = Color(0x33000000)
}

val StarColors = lightColors(
    primary = StarBrand.primary,
    primaryVariant = StarBrand.primaryDark,
    secondary = StarBrand.secondary,
    secondaryVariant = StarBrand.secondaryDark,
    background = StarBrand.background,
    surface = StarBrand.surface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = StarBrand.textPrimary,
    onSurface = StarBrand.textSecondary,
    error = StarBrand.error
)

val StarTypography = Typography(
    h1 = TextStyle(
        fontSize = 48.sp,
        fontWeight = FontWeight.ExtraBold,
        color = StarBrand.textPrimary,
        letterSpacing = (-1.5).sp,
        lineHeight = 56.sp
    ),
    h2 = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        color = StarBrand.textPrimary,
        letterSpacing = (-1).sp,
        lineHeight = 44.sp
    ),
    h3 = TextStyle(
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        color = StarBrand.textPrimary,
        letterSpacing = (-0.5).sp,
        lineHeight = 36.sp
    ),
    h4 = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        color = StarBrand.textPrimary,
        lineHeight = 32.sp
    ),
    h5 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = StarBrand.textPrimary,
        lineHeight = 28.sp
    ),
    subtitle1 = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = StarBrand.textSecondary,
        lineHeight = 26.sp
    ),
    subtitle2 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = StarBrand.textSecondary,
        lineHeight = 24.sp
    ),
    body1 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = StarBrand.textSecondary,
        lineHeight = 24.sp
    ),
    body2 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = StarBrand.textTertiary,
        lineHeight = 20.sp
    ),
    button = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        letterSpacing = 0.5.sp
    ),
    caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        color = StarBrand.textPlaceholder,
        lineHeight = 16.sp
    ),
    overline = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = StarBrand.textPlaceholder,
        letterSpacing = 1.5.sp
    )
)

val StarExtraTypography = object {
    val buttonSmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        letterSpacing = 0.5.sp
    )
    val badge = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        letterSpacing = 0.5.sp
    )
    val tag = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = StarBrand.textSecondary
    )
}

val StarShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

val StarExtraShapes = object {
    val extraLarge = RoundedCornerShape(24.dp)
    val pill = RoundedCornerShape(9999.dp)
}

val StarElevations = object {
    val none = 0.dp
    val low = 2.dp
    val medium = 4.dp
    val high = 8.dp
    val extraHigh = 16.dp
    val floating = 24.dp
}

val StarSpacing = object {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

val StarGradients = object {
    val primary = Brush.linearGradient(
        colors = listOf(StarBrand.gradientStart, StarBrand.gradientEnd)
    )
    val warm = Brush.linearGradient(
        colors = listOf(Color(0xFFFFFBF0), Color(0xFFFDF4FF))
    )
    val cool = Brush.linearGradient(
        colors = listOf(Color(0xFFEEF2FF), Color(0xFFECFEFF))
    )
    val sunset = Brush.linearGradient(
        colors = listOf(Color(0xFFFF6B6B), Color(0xFFFECA57), Color(0xFFFF9F43))
    )
    val ocean = Brush.linearGradient(
        colors = listOf(Color(0xFF06B6D4), Color(0xFF3B82F6), Color(0xFF6366F1))
    )
    val orangeWarm = Brush.linearGradient(
        colors = listOf(StarBrand.warning, StarBrand.accentOrange)
    )
    val greenSuccess = Brush.linearGradient(
        colors = listOf(StarBrand.success, StarBrand.secondaryDark)
    )
}

@Composable
fun StarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = StarColors,
        typography = StarTypography,
        shapes = StarShapes,
        content = content
    )
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    StarTheme(content = content)
}