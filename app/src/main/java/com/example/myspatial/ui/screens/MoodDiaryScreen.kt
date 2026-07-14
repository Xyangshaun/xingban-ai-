package com.example.myspatial.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myspatial.data.EmotionType
import com.example.myspatial.data.MoodRecord
import com.example.myspatial.domain.UseCaseProvider
import com.example.myspatial.ui.components.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MoodDiaryScreen(
    onBack: () -> Unit
) {
    val selectedEmotion = remember { mutableStateOf<EmotionType?>(null) }
    val moodNote = remember { mutableStateOf("") }
    val showMoodSelector = remember { mutableStateOf(false) }
    val moodTrendState = remember { mutableStateOf(UseCaseProvider.getMoodTrendUseCase.execute(7)) }
    val moodRecordsState = remember { mutableStateOf(UseCaseProvider.getMoodRecordsUseCase.execute()) }
    val isRecording = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFFFDF5))) {
        AppTopBar(
            title = "心情日记",
            navigationIcon = { BackButton(onBack) }
        )

        LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
            item {
                Text("记录今天的心情", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(8.dp))
                Text("用心感受每一天", fontSize = 16.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PrimaryButton(
                        text = "记录心情",
                        onClick = { showMoodSelector.value = true },
                        icon = Icons.Filled.CalendarMonth,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = if (isRecording.value) "停止录音" else "语音日记",
                        onClick = {
                            isRecording.value = !isRecording.value
                        },
                        icon = if (isRecording.value) Icons.Filled.Stop else Icons.Filled.Mic,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text("心情日历", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                MoodCalendarView()
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text("心情统计", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                MoodStatsView()
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text("近7天心情趋势", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                val moodTrend = moodTrendState.value
                when (moodTrend) {
                    is com.example.myspatial.core.Result.Success -> {
                        MoodTrendView(trend = moodTrend.data)
                    }
                    else -> {
                        Text("加载趋势失败", color = Color(0xFFEF4444))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text("心情记录", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(16.dp))
            }

            val moodRecords = moodRecordsState.value
            when (moodRecords) {
                is com.example.myspatial.core.Result.Success -> {
                    items(moodRecords.data.reversed()) { record ->
                        MoodRecordItem(record = record)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                else -> {
                    item { Text("加载记录失败", color = Color(0xFFEF4444)) }
                }
            }
        }
    }

    if (showMoodSelector.value) {
        MoodSelectorPopup(
            onSelect = { emotion ->
                selectedEmotion.value = emotion
            },
            onConfirm = {
                if (selectedEmotion.value != null) {
                    val result = UseCaseProvider.recordMoodUseCase.execute(
                        date = LocalDate.now(),
                        emotion = selectedEmotion.value!!,
                        intensity = 50,
                        note = moodNote.value
                    )
                    when (result) {
                        is com.example.myspatial.core.Result.Success -> {
                            moodTrendState.value = UseCaseProvider.getMoodTrendUseCase.execute(7)
                            moodRecordsState.value = UseCaseProvider.getMoodRecordsUseCase.execute()
                        }
                        else -> {}
                    }
                    showMoodSelector.value = false
                    selectedEmotion.value = null
                    moodNote.value = ""
                }
            },
            onCancel = {
                showMoodSelector.value = false
                selectedEmotion.value = null
                moodNote.value = ""
            },
            selectedEmotion = selectedEmotion.value,
            note = moodNote.value,
            onNoteChange = { moodNote.value = it }
        )
    }
}

@Composable
fun MoodTrendView(trend: com.example.myspatial.data.MoodTrend) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                trend.dates.forEachIndexed { index, date ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            date.format(DateTimeFormatter.ofPattern("M/d")),
                            fontSize = 13.sp,
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(trend.emotions[index].getColor())
                                .shadow(2.dp, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(trend.emotions[index].getEmoji(), fontSize = 26.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoodStatsView() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            icon = Icons.Filled.SentimentSatisfied,
            title = "本周心情",
            value = "😊",
            color = Color(0xFFFBBF24),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Filled.TrendingUp,
            title = "记录天数",
            value = "7天",
            color = Color(0xFF6366F1),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Filled.CalendarMonth,
            title = "连续记录",
            value = "5天",
            color = StarBrand.success,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MoodRecordItem(record: MoodRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(record.emotion.getColor())
                    .shadow(2.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(record.emotion.getEmoji(), fontSize = 30.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    record.date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )
                if (record.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(record.note, fontSize = 15.sp, color = Color(0xFF475569), lineHeight = 22.sp)
                }
            }
            Badge(text = record.emotion.getEmotionName(), color = Color(0xFF8B5CF6))
        }
    }
}

@Composable
fun MoodSelectorPopup(
    onSelect: (EmotionType) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    selectedEmotion: EmotionType?,
    note: String,
    onNoteChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onCancel),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(24.dp))
                .padding(32.dp)
                .clickable(enabled = false) { }
                .shadow(24.dp, RoundedCornerShape(24.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("今天心情怎么样？", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(28.dp))

            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                EmotionType.values().forEach { emotion ->
                    item {
                        MoodOptionItem(
                            emotion = emotion,
                            isSelected = selectedEmotion == emotion,
                            onClick = { onSelect(emotion) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                placeholder = { Text("写点什么...", color = Color(0xFFCBD5E1)) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF0F172A)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SecondaryButton(text = "取消", onClick = onCancel)
                PrimaryButton(text = "确定", onClick = onConfirm, enabled = selectedEmotion != null)
            }
        }
    }
}

@Composable
fun MoodOptionItem(
    emotion: EmotionType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                color = if (isSelected) emotion.getColor().copy(alpha = 0.2f) else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
            .shadow(if (isSelected) 2.dp else 0.dp, RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(emotion.getColor()),
            contentAlignment = Alignment.Center
        ) {
            Text(emotion.getEmoji(), fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(emotion.getEmotionName(), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
    }
}

@Composable
fun MoodCalendarView() {
    val today = LocalDate.now()
    val currentMonth = today.month
    val currentYear = today.year
    val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
    val daysInMonth = currentMonth.length(currentYear % 4 == 0 && currentYear % 100 != 0 || currentYear % 400 == 0)
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val weekDays = listOf("日", "一", "二", "三", "四", "五", "六")

    val moodRecords = UseCaseProvider.getMoodRecordsUseCase.execute()
    val moodMap = mutableMapOf<LocalDate, EmotionType>()
    if (moodRecords is com.example.myspatial.core.Result.Success) {
        moodRecords.data.forEach { record ->
            moodMap[record.date] = record.emotion
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${currentYear}年${currentMonth.value}月",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Row {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "上月", tint = Color(0xFF94A3B8))
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "下月", tint = Color(0xFF94A3B8))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekDays.forEach { day ->
                    Text(day, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = StarBrand.textTertiary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(7),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 0 until startDayOfWeek) {
                    item { Box(modifier = Modifier.size(40.dp)) }
                }

                for (day in 1..daysInMonth) {
                    item {
                        val date = LocalDate.of(currentYear, currentMonth, day)
                        val isToday = date == today
                        val emotion = moodMap[date]

                        Column(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(StarShapes.small)
                                .background(
                                    if (isToday) StarBrand.accentYellow.copy(alpha = 0.2f)
                                    else if (emotion != null) emotion.getColor().copy(alpha = 0.15f)
                                    else Color.Transparent
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                day.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) StarBrand.accentYellow else StarBrand.textSecondary
                            )
                            if (emotion != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(emotion.getColor()),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emotion.getEmoji(), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

