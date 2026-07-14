package com.example.myspatial.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myspatial.ai.AIProvider
import com.example.myspatial.data.*
import com.example.myspatial.ui.components.*
import kotlinx.coroutines.launch

enum class ContentMode {
    STORIES,
    LEARNING,
    DAILY_TIPS
}

@Composable
fun LearningContentScreen(
    context: Context,
    onBack: () -> Unit
) {
    var currentMode by remember { mutableStateOf(ContentMode.STORIES) }
    var selectedStory by remember { mutableStateOf<StoryContent?>(null) }
    var selectedLearning by remember { mutableStateOf<LearningContent?>(null) }
    var isReading by remember { mutableStateOf(false) }
    var completedStories by remember { mutableStateOf(0) }
    var totalStories by remember { mutableStateOf(0) }
    val voiceService = remember { AIProvider.getVoiceService(context) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        ContentLibrary.initialize(context)
        totalStories = ContentLibrary.getStories().size
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习陪伴", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = StarBrand.textPrimary) },
                navigationIcon = { BackButton(onClick = onBack) },
                backgroundColor = StarBrand.backgroundWarm,
                elevation = 0.dp
            )
        },
        backgroundColor = StarBrand.backgroundWarm
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ModeButton(
                    text = "故事",
                    icon = Icons.Filled.Book,
                    isSelected = currentMode == ContentMode.STORIES,
                    onClick = { currentMode = ContentMode.STORIES },
                    color = Color(0xFFF59E0B)
                )
                ModeButton(
                    text = "学习",
                    icon = Icons.Filled.School,
                    isSelected = currentMode == ContentMode.LEARNING,
                    onClick = { currentMode = ContentMode.LEARNING },
                    color = Color(0xFF10B981)
                )
                ModeButton(
                    text = "每日贴士",
                    icon = Icons.Filled.Lightbulb,
                    isSelected = currentMode == ContentMode.DAILY_TIPS,
                    onClick = { currentMode = ContentMode.DAILY_TIPS },
                    color = Color(0xFF8B5CF6)
                )
            }

            when (currentMode) {
                ContentMode.STORIES -> StoriesList(
                    stories = ContentLibrary.getStories(),
                    onSelectStory = { selectedStory = it }
                )
                ContentMode.LEARNING -> LearningList(
                    contents = ContentLibrary.getLearningContents(),
                    onSelectContent = { selectedLearning = it }
                )
                ContentMode.DAILY_TIPS -> DailyTipsList()
            }

            selectedStory?.let { story ->
                StoryDetailDialog(
                    story = story,
                    isReading = isReading,
                    onToggleReading = {
                        isReading = !isReading
                        if (isReading) {
                            coroutineScope.launch {
                                voiceService.textToSpeech(story.content, VoiceTone.GENTLE_WARM)
                                isReading = false
                            }
                        } else {
                            voiceService.stopSpeaking()
                        }
                    },
                    onClose = {
                        voiceService.stopSpeaking()
                        selectedStory = null
                        isReading = false
                    }
                )
            }

            selectedLearning?.let { learning ->
                LearningDetailDialog(content = learning, onClose = { selectedLearning = null })
            }
        }
    }
}

@Composable
fun ModeButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) color else Color(0xFFF1F5F9))
                .shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else Color(0xFF94A3B8), modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, color = if (isSelected) color else Color(0xFF94A3B8))
    }
}

@Composable
fun StoriesList(stories: List<StoryContent>, onSelectStory: (StoryContent) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(stories) { story ->
            StoryCard(story = story, onClick = { onSelectStory(story) })
        }
    }
}

@Composable
fun StoryCard(story: StoryContent, onClick: () -> Unit) {
    val categoryColor = getStoryCategoryColor(story.category)
    val categoryIcon = getStoryCategoryIcon(story.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(categoryColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(categoryIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(story.title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(4.dp))
                Text(story.content.take(60) + "...", fontSize = 14.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Tag(text = story.duration, color = Color(0xFFF97316))
                    Spacer(modifier = Modifier.width(8.dp))
                    Tag(text = story.ageGroup + "岁", color = Color(0xFF06B6D4))
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color(0xFFCBD5E1))
        }
    }
}

@Composable
fun StoryDetailDialog(
    story: StoryContent,
    isReading: Boolean,
    onToggleReading: () -> Unit,
    onClose: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(dismissOnBackPress = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFFFFFF))
                .shadow(24.dp, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(32.dp)) {
                Text(story.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Tag(text = story.duration, color = Color(0xFFF97316))
                    Spacer(modifier = Modifier.width(8.dp))
                    Tag(text = story.ageGroup + "岁", color = Color(0xFF06B6D4))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFF1F5F9)
                ) {
                    Text(story.content, fontSize = 15.sp, color = Color(0xFF475569), lineHeight = 24.sp, modifier = Modifier.padding(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("学到的道理：", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.height(120.dp)) {
                    items(story.lessons) { lesson ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lesson, fontSize = 14.sp, color = Color(0xFF475569))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("互动练习：", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(8.dp))
                StoryQuizSection(story = story)

                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SecondaryButton(
                        text = if (isReading) "停止朗读" else "开始朗读",
                        onClick = onToggleReading,
                        icon = if (isReading) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        text = "关闭",
                        onClick = onClose,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun StoryQuizSection(story: StoryContent) {
    val questions = listOf(
        "这个故事里的主角遇到了什么困难？",
        "如果你是主角，你会怎么做？",
        "从这个故事中学到了什么？"
    )
    val selectedAnswers = remember { mutableStateListOf<String?>(null, null, null) }
    val showResults = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        questions.forEachIndexed { index, question ->
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color(0xFFF1F5F9)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(question, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = selectedAnswers[index] ?: "",
                        onValueChange = { selectedAnswers[index] = it },
                        placeholder = { Text("写下你的答案...", color = Color(0xFFCBD5E1)) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 13.sp, color = Color(0xFF475569)),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFFF97316),
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(
            text = "提交答案",
            onClick = { showResults.value = true },
            modifier = Modifier.fillMaxWidth()
        )
        if (showResults.value) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color(0xFF10B981).copy(alpha = 0.1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("太棒了！你已经完成了这个故事的学习练习！", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF10B981))
                }
            }
        }
    }
}

@Composable
fun LearningList(contents: List<LearningContent>, onSelectContent: (LearningContent) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(contents) { content ->
            LearningCard(content = content, onClick = { onSelectContent(content) })
        }
    }
}

@Composable
fun LearningCard(content: LearningContent, onClick: () -> Unit) {
    val categoryColor = getLearningCategoryColor(content.category)
    val typeIcon = getContentTypeIcon(content.type)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(categoryColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(typeIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(content.title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(4.dp))
                Text(content.content.take(50) + "...", fontSize = 14.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Tag(text = getContentTypeLabel(content.type), color = Color(0xFF6366F1))
                    Spacer(modifier = Modifier.width(8.dp))
                    Tag(text = content.ageGroup + "岁", color = Color(0xFF06B6D4))
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color(0xFFCBD5E1))
        }
    }
}

@Composable
fun LearningDetailDialog(content: LearningContent, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        shape = RoundedCornerShape(20.dp),
        backgroundColor = Color.White,
        buttons = {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = onClose,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("关闭", color = Color.White)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(content.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(12.dp))
                Text(getContentTypeLabel(content.type), fontSize = 14.sp, color = Color(0xFF6366F1))
                Spacer(modifier = Modifier.height(16.dp))
                Text(content.content, fontSize = 15.sp, color = Color(0xFF334155), lineHeight = 24.sp)

                if (content.exercises.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("练习活动：", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.height(150.dp)) {
                        items(content.exercises) { exercise ->
                            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(exercise.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(exercise.description, fontSize = 14.sp, color = Color(0xFF475569), modifier = Modifier.padding(start = 26.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("时长：" + exercise.duration, fontSize = 12.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(start = 26.dp))
                            }
                        }
                    }
                }

                if (content.tips.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("小贴士：", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.height(100.dp)) {
                        items(content.tips) { tip ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(tip, fontSize = 14.sp, color = Color(0xFF475569))
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DailyTipsList() {
    val tips = ContentLibrary.getDailyTips()
    val todayTip = ContentLibrary.getTipForDay((System.currentTimeMillis() / (1000 * 60 * 60 * 24)).toInt())

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(Color(0xFFF59E0B), Color(0xFFF97316))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("今日贴士", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    todayTip?.let { tip ->
                        Text(tip.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(tip.content, fontSize = 15.sp, color = Color.White.copy(alpha = 0.9f), lineHeight = 24.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("行动：" + tip.action, fontSize = 14.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text("本周贴士", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B), modifier = Modifier.padding(top = 8.dp))
        }

        items(tips) { tip ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable {}
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEEF2FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("第${tip.day}天", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6366F1))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(tip.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                    Text(tip.content.take(30) + "...", fontSize = 13.sp, color = Color(0xFF64748B))
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color(0xFFCBD5E1))
            }
        }
    }
}

fun getStoryCategoryColor(category: StoryCategory): Color {
    return when (category) {
        StoryCategory.EMOTION_MANAGEMENT -> Color(0xFF8B5CF6)
        StoryCategory.SELF_ESTEEM -> Color(0xFF06B6D4)
        StoryCategory.FRIENDSHIP -> Color(0xFF10B981)
        StoryCategory.FAMILY_LOVE -> Color(0xFFF59E0B)
        StoryCategory.COPING_SKILLS -> Color(0xFF3B82F6)
        StoryCategory.GROWTH_MINDSET -> Color(0xFFEC4899)
        StoryCategory.BEDTIME_STORY -> Color(0xFF6366F1)
    }
}

fun getStoryCategoryIcon(category: StoryCategory): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        StoryCategory.EMOTION_MANAGEMENT -> Icons.Filled.Favorite
        StoryCategory.SELF_ESTEEM -> Icons.Filled.Star
        StoryCategory.FRIENDSHIP -> Icons.Filled.Group
        StoryCategory.FAMILY_LOVE -> Icons.Filled.Home
        StoryCategory.COPING_SKILLS -> Icons.Filled.Shield
        StoryCategory.GROWTH_MINDSET -> Icons.Filled.TrendingUp
        StoryCategory.BEDTIME_STORY -> Icons.Filled.Brightness3
    }
}

fun getLearningCategoryColor(category: LearningCategory): Color {
    return when (category) {
        LearningCategory.EMOTION_RECOGNITION -> Color(0xFF8B5CF6)
        LearningCategory.COMMUNICATION_SKILLS -> Color(0xFF06B6D4)
        LearningCategory.PROBLEM_SOLVING -> Color(0xFF10B981)
        LearningCategory.MINDFULNESS -> Color(0xFFF59E0B)
        LearningCategory.STRESS_MANAGEMENT -> Color(0xFF3B82F6)
        LearningCategory.CREATIVE_EXPRESSION -> Color(0xFFEC4899)
    }
}

fun getContentTypeIcon(type: ContentType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        ContentType.ARTICLE -> Icons.Filled.Book
        ContentType.ACTIVITY -> Icons.Filled.PlayCircle
        ContentType.QUIZ -> Icons.Filled.CheckCircle
        ContentType.MEDITATION -> Icons.Filled.Healing
    }
}

fun getContentTypeLabel(type: ContentType): String {
    return when (type) {
        ContentType.ARTICLE -> "文章"
        ContentType.ACTIVITY -> "活动"
        ContentType.QUIZ -> "测验"
        ContentType.MEDITATION -> "冥想"
    }
}