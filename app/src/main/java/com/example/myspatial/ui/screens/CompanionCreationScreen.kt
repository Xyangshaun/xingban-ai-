package com.example.myspatial.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myspatial.core.Result
import com.example.myspatial.data.Companion
import com.example.myspatial.data.CompanionCharacter
import com.example.myspatial.data.CompanionPersonality
import com.example.myspatial.domain.UseCaseProvider
import com.example.myspatial.ui.components.PrimaryButton
import com.example.myspatial.ui.components.SecondaryButton

@Composable
fun CompanionCreationScreen(
    onComplete: (Companion) -> Unit,
    onError: (String) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedCharacter by remember { mutableStateOf<CompanionCharacter?>(null) }
    var selectedPersonality by remember { mutableStateOf<CompanionPersonality?>(null) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (step) {
            1 -> Step1SelectCharacter(selectedCharacter, { selectedCharacter = it }) { step = 2 }
            2 -> Step2SelectPersonality(selectedPersonality, { selectedPersonality = it }, { step = 1 }, { step = 3 })
            3 -> Step3EnterInfo(name, { name = it }, age, { age = it }, {
                if (name.isNotBlank() && selectedCharacter != null && selectedPersonality != null) {
                    isLoading = true
                    val result = UseCaseProvider.createCompanionUseCase.execute(
                        name = name,
                        character = selectedCharacter!!,
                        personality = selectedPersonality!!,
                        age = age.toIntOrNull() ?: 10
                    )
                    isLoading = false
                    when (result) {
                        is Result.Success -> onComplete(result.data)
                        is Result.Error -> onError(result.message ?: "创建失败")
                        else -> {}
                    }
                }
            }, { step = 2 }, isLoading)
        }
    }
}

@Composable
fun Step1SelectCharacter(
    selectedCharacter: CompanionCharacter?,
    onSelect: (CompanionCharacter) -> Unit,
    onNext: () -> Unit
) {
    Text("创建你的星伴", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    Spacer(modifier = Modifier.height(8.dp))
    Text("选择一个喜欢的形象", fontSize = 16.sp, color = Color(0xFF64748B))
    Spacer(modifier = Modifier.height(40.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(CompanionCharacter.values()) { character ->
            CharacterCard(
                character = character,
                isSelected = selectedCharacter == character,
                onClick = { onSelect(character) }
            )
        }
    }

    Spacer(modifier = Modifier.height(40.dp))
    PrimaryButton(
        text = "下一步",
        onClick = onNext,
        enabled = selectedCharacter != null
    )
}

@Composable
fun Step2SelectPersonality(
    selectedPersonality: CompanionPersonality?,
    onSelect: (CompanionPersonality) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Text("设定性格", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    Spacer(modifier = Modifier.height(8.dp))
    Text("选择星伴的性格", fontSize = 16.sp, color = Color(0xFF64748B))
    Spacer(modifier = Modifier.height(40.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(CompanionPersonality.values()) { personality ->
            PersonalityCard(
                personality = personality,
                isSelected = selectedPersonality == personality,
                onClick = { onSelect(personality) }
            )
        }
    }

    Spacer(modifier = Modifier.height(40.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SecondaryButton(text = "返回", onClick = onBack, modifier = Modifier.weight(1f))
        PrimaryButton(text = "下一步", onClick = onNext, enabled = selectedPersonality != null, modifier = Modifier.weight(1f))
    }
}

@Composable
fun Step3EnterInfo(
    name: String,
    onNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    Text("介绍一下", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    Spacer(modifier = Modifier.height(8.dp))
    Text("给你的星伴起个名字吧", fontSize = 16.sp, color = Color(0xFF64748B))
    Spacer(modifier = Modifier.height(40.dp))

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        placeholder = { Text("星伴的名字", color = Color(0xFF94A3B8)) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = 18.sp, color = Color(0xFF1E293B)),
        enabled = !isLoading,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF6366F1),
            unfocusedBorderColor = Color(0xFFE2E8F0)
        )
    )

    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = age,
        onValueChange = onAgeChange,
        placeholder = { Text("你的年龄", color = Color(0xFF94A3B8)) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = 18.sp, color = Color(0xFF1E293B)),
        enabled = !isLoading,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF6366F1),
            unfocusedBorderColor = Color(0xFFE2E8F0)
        )
    )

    Spacer(modifier = Modifier.height(40.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SecondaryButton(text = "返回", onClick = onBack, modifier = Modifier.weight(1f))
        PrimaryButton(text = "完成", onClick = onComplete, enabled = name.isNotBlank() && !isLoading, modifier = Modifier.weight(1f))
    }
}

@Composable
fun CharacterCard(
    character: CompanionCharacter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = getCharacterColor(character)
    val name = getCharacterName(character)

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF6366F1) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .background(color, shape = RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            when (character) {
                CompanionCharacter.STAR_BRIGHT -> Text("⭐", fontSize = 32.sp)
                CompanionCharacter.LUNA_GENTLE -> Text("🌙", fontSize = 32.sp)
                CompanionCharacter.SUNNY_ACTIVE -> Text("☀️", fontSize = 32.sp)
                CompanionCharacter.CLOUDY_SHY -> Text("☁️", fontSize = 32.sp)
                CompanionCharacter.FLAME_WARM -> Text("🔥", fontSize = 32.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
    }
}

@Composable
fun PersonalityCard(
    personality: CompanionPersonality,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val name = getPersonalityName(personality)

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(
                width = 3.dp,
                color = if (isSelected) Color(0xFF6366F1) else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(8.dp))
        when (personality) {
            CompanionPersonality.GENTLE -> Text("温柔体贴", fontSize = 14.sp, color = Color(0xFF64748B))
            CompanionPersonality.ACTIVE -> Text("活泼开朗", fontSize = 14.sp, color = Color(0xFF64748B))
            CompanionPersonality.QUIET -> Text("安静内向", fontSize = 14.sp, color = Color(0xFF64748B))
            CompanionPersonality.CURIOUS -> Text("好奇探索", fontSize = 14.sp, color = Color(0xFF64748B))
            CompanionPersonality.CAREFUL -> Text("细心认真", fontSize = 14.sp, color = Color(0xFF64748B))
        }
    }
}

fun getCharacterColor(character: CompanionCharacter): Color {
    return when (character) {
        CompanionCharacter.STAR_BRIGHT -> Color(0xFFFBBF24)
        CompanionCharacter.LUNA_GENTLE -> Color(0xFFA5B4FC)
        CompanionCharacter.SUNNY_ACTIVE -> Color(0xFFFF9F43)
        CompanionCharacter.CLOUDY_SHY -> Color(0xFFCBD5E1)
        CompanionCharacter.FLAME_WARM -> Color(0xFFFF6B6B)
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