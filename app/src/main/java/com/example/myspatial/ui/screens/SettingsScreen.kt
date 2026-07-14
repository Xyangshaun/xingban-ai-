package com.example.myspatial.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myspatial.ai.AIProvider
import com.example.myspatial.data.*
import com.example.myspatial.domain.UseCaseProvider
import com.example.myspatial.ui.components.*

@Composable
fun SettingsScreen(
    companion: Companion,
    onBack: () -> Unit,
    onReset: () -> Unit
) {
    val showResetConfirm = remember { mutableStateOf(false) }
    val showPrivacySettings = remember { mutableStateOf(false) }
    val showCharacterInfo = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val apiKey = remember { mutableStateOf(AIProvider.getApiKey(context)) }
    val showApiKey = remember { mutableStateOf(false) }
    val savedMessage = remember { mutableStateOf("") }
    val cameraPermission = remember { mutableStateOf(true) }
    val microphonePermission = remember { mutableStateOf(true) }
    val dataCollection = remember { mutableStateOf(true) }
    val notificationsEnabled = remember { mutableStateOf(true) }
    val darkModeEnabled = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFFFDF5))) {
        AppTopBar(
            title = "设置",
            navigationIcon = { BackButton(onBack) }
        )

        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
            Text("我的星伴", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCharacterInfo.value = true }
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0xFFFFFFFF)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(companion.character.getCharacterColor())
                            .shadow(4.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        when (companion.character) {
                            CompanionCharacter.STAR_BRIGHT -> Text("⭐", fontSize = 36.sp)
                            CompanionCharacter.LUNA_GENTLE -> Text("🌙", fontSize = 36.sp)
                            CompanionCharacter.SUNNY_ACTIVE -> Text("☀️", fontSize = 36.sp)
                            CompanionCharacter.CLOUDY_SHY -> Text("☁️", fontSize = 36.sp)
                            CompanionCharacter.FLAME_WARM -> Text("🔥", fontSize = 36.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(companion.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(companion.personality.getPersonalityName(), fontSize = 14.sp, color = Color(0xFF94A3B8))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("点击查看角色详情", fontSize = 13.sp, color = Color(0xFFF97316))
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = Color(0xFFCBD5E1))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("隐私管理", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0xFFFFFFFF)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PrivacySettingRow(
                        icon = Icons.Filled.Camera,
                        title = "相机权限",
                        subtitle = "用于AR场景和拍照",
                        isEnabled = cameraPermission.value,
                        onToggle = { cameraPermission.value = !cameraPermission.value }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrivacySettingRow(
                        icon = Icons.Filled.Mic,
                        title = "麦克风权限",
                        subtitle = "用于语音聊天和录音",
                        isEnabled = microphonePermission.value,
                        onToggle = { microphonePermission.value = !microphonePermission.value }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrivacySettingRow(
                        icon = Icons.Filled.CloudUpload,
                        title = "数据收集",
                        subtitle = "用于优化AI对话体验",
                        isEnabled = dataCollection.value,
                        onToggle = { dataCollection.value = !dataCollection.value }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("AI 配置", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0xFFFFFFFF)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981).copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Smartphone, contentDescription = "AI", tint = Color(0xFF10B981))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Qwen API Key", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("配置通义千问API以启用真实AI对话（免费额度100万Token）", fontSize = 14.sp, color = Color(0xFF94A3B8))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = apiKey.value,
                        onValueChange = { apiKey.value = it },
                        label = { Text("API Key") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showApiKey.value) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showApiKey.value = !showApiKey.value }) {
                                Icon(
                                    if (showApiKey.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle visibility"
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryButton(
                        text = "保存",
                        onClick = {
                            AIProvider.setApiKey(context, apiKey.value)
                            AIProvider.resetLLMClient()
                            savedMessage.value = "API Key已保存"
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                savedMessage.value = ""
                            }, 2000)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (savedMessage.value.isNotEmpty()) {
                        Text(savedMessage.value, fontSize = 14.sp, color = Color(0xFF10B981), modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("通用设置", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0xFFFFFFFF)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PrivacySettingRow(
                        icon = Icons.Filled.Notifications,
                        title = "通知提醒",
                        subtitle = "接收心情提醒和活动通知",
                        isEnabled = notificationsEnabled.value,
                        onToggle = { notificationsEnabled.value = !notificationsEnabled.value }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrivacySettingRow(
                        icon = Icons.Filled.DarkMode,
                        title = "深色模式",
                        subtitle = "切换到深色主题",
                        isEnabled = darkModeEnabled.value,
                        onToggle = { darkModeEnabled.value = !darkModeEnabled.value }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("关于", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(
                icon = Icons.Filled.Info,
                title = "关于星伴 AI",
                subtitle = "陪伴成长，温暖心灵",
                color = Color(0xFFF97316)
            )

            InfoCard(
                icon = Icons.Filled.Shield,
                title = "隐私政策",
                subtitle = "保护你的每一份心意",
                color = Color(0xFF10B981)
            )

            InfoCard(
                icon = Icons.Filled.Help,
                title = "帮助与反馈",
                subtitle = "我们一直在倾听",
                color = Color(0xFF8B5CF6)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("管理", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { showResetConfirm.value = true })
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color(0xFFEF4444).copy(alpha = 0.05f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Reset", tint = Color(0xFFEF4444))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("重新创建星伴", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("这将删除当前星伴和所有聊天记录", fontSize = 14.sp, color = Color(0xFFEF4444).copy(alpha = 0.8f))
                    }
                }
            }
        }
    }

    if (showCharacterInfo.value) {
        CharacterInfoDialog(companion = companion, onClose = { showCharacterInfo.value = false })
    }

    if (showResetConfirm.value) {
        AlertDialog(
            onDismissRequest = { showResetConfirm.value = false },
            title = { Text("确认删除？", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)) },
            text = { Text("删除后将无法恢复当前星伴和所有聊天记录，确定要继续吗？", color = Color(0xFF64748B)) },
            confirmButton = {
                DangerButton(text = "确定", onClick = {
                    UseCaseProvider.deleteCompanionUseCase.execute()
                    onReset()
                })
            },
            dismissButton = {
                SecondaryButton(text = "取消", onClick = { showResetConfirm.value = false })
            }
        )
    }
}

@Composable
fun PrivacySettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isEnabled) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = if (isEnabled) Color(0xFF10B981) else Color(0xFFCBD5E1), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 13.sp, color = Color(0xFF94A3B8))
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFF97316),
                checkedTrackColor = Color(0xFFFBBF24).copy(alpha = 0.5f),
                uncheckedThumbColor = Color(0xFFCBD5E1),
                uncheckedTrackColor = Color(0xFFE2E8F0)
            )
        )
    }
}

@Composable
fun CharacterInfoDialog(companion: Companion, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color(0xFFFFFEF9),
        buttons = {
            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("关闭", color = Color.White)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(companion.character.getCharacterColor().copy(alpha = 0.1f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(companion.character.getCharacterColor()),
                        contentAlignment = Alignment.Center
                    ) {
                        when (companion.character) {
                            CompanionCharacter.STAR_BRIGHT -> Text("⭐", fontSize = 50.sp)
                            CompanionCharacter.LUNA_GENTLE -> Text("🌙", fontSize = 50.sp)
                            CompanionCharacter.SUNNY_ACTIVE -> Text("☀️", fontSize = 50.sp)
                            CompanionCharacter.CLOUDY_SHY -> Text("☁️", fontSize = 50.sp)
                            CompanionCharacter.FLAME_WARM -> Text("🔥", fontSize = 50.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(companion.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text(companion.personality.getPersonalityName(), fontSize = 16.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(20.dp))
                CharacterTraitCard(
                    icon = Icons.Filled.Favorite,
                    title = "性格特点",
                    content = companion.personality.getPersonalityDescription()
                )
                Spacer(modifier = Modifier.height(12.dp))
                CharacterTraitCard(
                    icon = Icons.Filled.Chat,
                    title = "聊天风格",
                    content = companion.personality.getChatStyle()
                )
                Spacer(modifier = Modifier.height(12.dp))
                CharacterTraitCard(
                    icon = Icons.Filled.Lightbulb,
                    title = "擅长话题",
                    content = companion.personality.getSpecialTopics()
                )
            }
        }
    )
}

@Composable
fun CharacterTraitCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(12.dp))
            .padding(14.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(content, fontSize = 14.sp, color = Color(0xFF475569), lineHeight = 20.sp)
        }
    }
}

@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
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
        Column {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 14.sp, color = Color(0xFF64748B))
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = Color(0xFFCBD5E1))
    }
    Spacer(modifier = Modifier.height(12.dp))
}