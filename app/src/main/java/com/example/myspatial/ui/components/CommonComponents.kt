package com.example.myspatial.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myspatial.data.CompanionCharacter
import com.example.myspatial.data.CompanionPersonality
import com.example.myspatial.ui.components.StarBrand
import com.example.myspatial.ui.components.StarShapes
import com.example.myspatial.ui.components.StarElevations
import com.example.myspatial.ui.components.StarSpacing
import com.example.myspatial.ui.components.StarExtraShapes
import com.example.myspatial.ui.components.StarGradients
import com.example.myspatial.ui.components.StarExtraTypography

enum class ButtonSize { SMALL, NORMAL, LARGE }

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    loading: Boolean = false,
    size: ButtonSize = ButtonSize.NORMAL
) {
    val buttonHeight = when (size) {
        ButtonSize.SMALL -> 44.dp
        ButtonSize.NORMAL -> 56.dp
        ButtonSize.LARGE -> 64.dp
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .clip(RoundedCornerShape(12.dp))
            .let {
                if (enabled && !loading) {
                    it.background(Brush.linearGradient(colors = listOf(Color(0xFF6366F1), Color(0xFFEC4899))))
                } else {
                    it.background(Color(0xFFF1F5F9))
                }
            }
            .clickable(enabled = enabled && !loading, onClick = onClick)
            .shadow(if (enabled && !loading) 4.dp else 0.dp, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.let {
                    Icon(it, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text,
                    fontSize = when (size) {
                        ButtonSize.SMALL -> 14.sp
                        ButtonSize.NORMAL -> 16.sp
                        ButtonSize.LARGE -> 18.sp
                    },
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled && !loading) Color.White else Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    size: ButtonSize = ButtonSize.NORMAL
) {
    val buttonHeight = when (size) {
        ButtonSize.SMALL -> 44.dp
        ButtonSize.NORMAL -> 56.dp
        ButtonSize.LARGE -> 64.dp
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFFFF))
            .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF475569))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text,
                fontSize = when (size) {
                    ButtonSize.SMALL -> 14.sp
                    ButtonSize.NORMAL -> 16.sp
                    ButtonSize.LARGE -> 18.sp
                },
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEF4444))
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
fun AppTopBar(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    showGradient: Boolean = false
) {
    TopAppBar(
        title = {
            Text(
                title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (showGradient) Color.White else Color(0xFF0F172A)
            )
        },
        backgroundColor = if (showGradient) Color.Transparent else Color(0xFFFFFFFF),
        elevation = if (showGradient) 0.dp else 2.dp,
        navigationIcon = navigationIcon,
        actions = actions,
        modifier = if (showGradient) {
            Modifier.background(Brush.linearGradient(colors = listOf(Color(0xFF6366F1), Color(0xFFEC4899))))
        } else Modifier
    )
}

@Composable
fun BackButton(onClick: () -> Unit, tint: Color = Color(0xFF475569)) {
    IconButton(onClick = onClick) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "返回", tint = tint)
    }
}

@Composable
fun SettingsButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Filled.Settings, contentDescription = "设置", tint = Color(0xFF94A3B8))
    }
}

@Composable
fun InfoCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit = {},
    hasArrow: Boolean = true,
    color: Color = Color(0xFF6366F1)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
            subtitle?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(it, fontSize = 14.sp, color = Color(0xFF94A3B8))
            }
        }
        if (hasArrow) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = Color(0xFFCBD5E1))
        }
    }
}

@Composable
fun InfoCardIcon(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
    color: Color = Color(0xFF6366F1)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = color)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 14.sp, color = Color(0xFF94A3B8))
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = Color(0xFFCBD5E1))
    }
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(Color(0xFF6366F1), Color(0xFFEC4899)))),
                contentAlignment = Alignment.Center
            ) {
                Text("🌟", fontSize = 40.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                color = Color(0xFF6366F1),
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("星伴 AI", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6366F1))
        }
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF4444).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Error,
                contentDescription = "Error",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(50.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(message, fontSize = 18.sp, textAlign = TextAlign.Center, color = Color(0xFF475569))
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(text = "重试", onClick = onRetry)
    }
}

@Composable
fun CharacterAvatar(
    character: CompanionCharacter,
    size: Int = 64,
    onClick: () -> Unit = {},
    showShadow: Boolean = true
) {
    val color = getCharacterColor(character)

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .let {
                if (showShadow) it.shadow(4.dp, CircleShape) else it
            },
        contentAlignment = Alignment.Center
    ) {
        when (character) {
            CompanionCharacter.STAR_BRIGHT -> Text("⭐", fontSize = (size / 2).sp)
            CompanionCharacter.LUNA_GENTLE -> Text("🌙", fontSize = (size / 2).sp)
            CompanionCharacter.SUNNY_ACTIVE -> Text("☀️", fontSize = (size / 2).sp)
            CompanionCharacter.CLOUDY_SHY -> Text("☁️", fontSize = (size / 2).sp)
            CompanionCharacter.FLAME_WARM -> Text("🔥", fontSize = (size / 2).sp)
        }
    }
}

@Composable
fun MoodIcon(mood: String, size: Int = 40) {
    val moodColorMap = mapOf(
        "happy" to Color(0xFFFBBF24),
        "calm" to Color(0xFF06B6D4),
        "sad" to Color(0xFF6366F1),
        "angry" to Color(0xFFEF4444),
        "scared" to Color(0xFF8B5CF6),
        "missing" to Color(0xFFEC4899),
        "lonely" to Color(0xFF64748B),
        "confused" to Color(0xFFF97316)
    )
    val moodEmojiMap = mapOf(
        "happy" to "😊", "calm" to "😌", "sad" to "😢", "angry" to "😠",
        "scared" to "😨", "missing" to "🥺", "lonely" to "🥀", "confused" to "😕"
    )
    val color = moodColorMap[mood] ?: Color(0xFFE2E8F0)
    val emoji = moodEmojiMap[mood] ?: "😌"

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = (size / 2).sp)
    }
}

fun getCharacterColor(character: CompanionCharacter): Color {
    return when (character) {
        CompanionCharacter.STAR_BRIGHT -> Color(0xFFFBBF24)
        CompanionCharacter.LUNA_GENTLE -> Color(0xFFA5B4FC)
        CompanionCharacter.SUNNY_ACTIVE -> Color(0xFFFB923C)
        CompanionCharacter.CLOUDY_SHY -> Color(0xFFCBD5E1)
        CompanionCharacter.FLAME_WARM -> Color(0xFFFB7185)
    }
}

fun getCharacterName(character: CompanionCharacter): String {
    return when (character) {
        CompanionCharacter.STAR_BRIGHT -> "星星"
        CompanionCharacter.LUNA_GENTLE -> "月亮"
        CompanionCharacter.SUNNY_ACTIVE -> "太阳"
        CompanionCharacter.CLOUDY_SHY -> "云朵"
        CompanionCharacter.FLAME_WARM -> "火焰"
    }
}

fun getPersonalityName(personality: CompanionPersonality): String {
    return when (personality) {
        CompanionPersonality.GENTLE -> "温柔型"
        CompanionPersonality.ACTIVE -> "活泼型"
        CompanionPersonality.QUIET -> "安静型"
        CompanionPersonality.CURIOUS -> "好奇型"
        CompanionPersonality.CAREFUL -> "细心型"
    }
}

@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Float = 16f,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        color = Color(0xFF6366F1)
    )
}

@Composable
fun EmotionCard(
    emotion: String,
    title: String,
    content: String,
    onClick: () -> Unit = {}
) {
    val moodColorMap = mapOf(
        "happy" to Color(0xFFFBBF24),
        "calm" to Color(0xFF06B6D4),
        "sad" to Color(0xFF6366F1),
        "angry" to Color(0xFFEF4444),
        "scared" to Color(0xFF8B5CF6),
        "missing" to Color(0xFFEC4899),
        "lonely" to Color(0xFF64748B),
        "confused" to Color(0xFFF97316)
    )
    val color = moodColorMap[emotion] ?: Color(0xFFE2E8F0)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MoodIcon(mood = emotion, size = 48)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                content,
                fontSize = 15.sp,
                color = Color(0xFF475569),
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "查看更多 →",
                    fontSize = 14.sp,
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun BubbleDialog(
    text: String,
    isUser: Boolean,
    emotion: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        val shape = if (isUser) {
            RoundedCornerShape(24.dp, 24.dp, 8.dp, 24.dp)
        } else {
            RoundedCornerShape(24.dp, 24.dp, 24.dp, 8.dp)
        }

        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .let {
                    if (isUser) {
                        it.background(Brush.linearGradient(colors = listOf(Color(0xFF6366F1), Color(0xFFEC4899))))
                    } else {
                        it.background(Color(0xFFFFFFFF))
                    }
                }
                .padding(16.dp)
                .shadow(2.dp, shape)
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                color = if (isUser) Color.White else Color(0xFF0F172A),
                lineHeight = 24.sp
            )
            emotion?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MoodIcon(mood = it, size = 24)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        when (it) {
                            "happy" -> "开心"
                            "calm" -> "平静"
                            "sad" -> "难过"
                            "angry" -> "生气"
                            "scared" -> "害怕"
                            "missing" -> "想念"
                            "lonely" -> "孤独"
                            "confused" -> "困惑"
                            else -> ""
                        },
                        fontSize = 12.sp,
                        color = if (isUser) Color.White.copy(0.8f) else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingGradientButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    androidx.compose.material.FloatingActionButton(
        onClick = onClick,
        backgroundColor = Color.Transparent,
        modifier = modifier
            .shadow(8.dp, CircleShape)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(colors = listOf(Color(0xFF6366F1), Color(0xFFEC4899)))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color = Color(0xFF6366F1),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 14.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun Badge(
    text: String,
    color: Color = Color(0xFF6366F1),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun Tag(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    color: Color = Color(0xFF6366F1)
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(if (selected) color else color.copy(alpha = 0.1f))
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else color
        )
    }
}

@Composable
fun TooltipIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color,
    modifier: Modifier = Modifier,
    showTooltipAbove: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(modifier = modifier) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFFFFF).copy(alpha = 0.9f))
                .shadow(2.dp, CircleShape)
                .hoverable(interactionSource = interactionSource)
        ) {
            Icon(icon, contentDescription = contentDescription, tint = tint)
        }
        AnimatedVisibility(visible = isHovered) {
            Box(
                modifier = Modifier
                    .padding(if (showTooltipAbove) 0.dp else 0.dp, 8.dp)
                    .align(Alignment.Center)
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .padding(8.dp, 6.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(contentDescription, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun TooltipBottomButton(
    tooltip: String,
    onClick: () -> Unit,
    isLarge: Boolean = false,
    gradientColors: List<Color> = emptyList(),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box {
        Box(
            modifier = Modifier
                .size(if (isLarge) 92.dp else 60.dp)
                .clip(CircleShape)
                .then(
                    if (gradientColors.isNotEmpty()) {
                        Modifier.background(
                            brush = Brush.radialGradient(
                                colors = gradientColors,
                                radius = if (isLarge) 60f else 40f
                            )
                        )
                    } else {
                        Modifier.background(Color(0xFFFFFFFF).copy(alpha = 0.9f))
                    }
                )
                .clickable(onClick = onClick)
                .shadow(if (isLarge) 24.dp else 4.dp, CircleShape)
                .hoverable(interactionSource = interactionSource),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
        AnimatedVisibility(visible = isHovered) {
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.Center)
                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                    .padding(8.dp, 6.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(tooltip, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
    }
}