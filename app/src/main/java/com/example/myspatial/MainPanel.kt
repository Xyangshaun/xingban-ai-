package com.example.myspatial

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.myspatial.data.Companion
import com.example.myspatial.domain.AppAction
import com.example.myspatial.domain.AppState
import com.example.myspatial.domain.Screen
import com.example.myspatial.domain.UseCaseProvider
import com.example.myspatial.domain.appReducer
import com.example.myspatial.ui.components.AppTheme
import com.example.myspatial.ui.screens.ARSceneScreen
import com.example.myspatial.ui.screens.CompanionCreationScreen
import com.example.myspatial.ui.screens.CompanionMainScreen
import com.example.myspatial.ui.screens.LearningContentScreen
import com.example.myspatial.ui.screens.MoodDiaryScreen
import com.example.myspatial.ui.screens.SettingsScreen
import com.example.myspatial.ui.screens.VoiceRecordingScreen
import com.example.myspatial.ui.screens.XRSceneScreen

@Composable
fun MainPanel(modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf(AppState()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val result = UseCaseProvider.getCompanionUseCase.execute()
        when (result) {
            is com.example.myspatial.core.Result.Success -> {
                if (result.data != null) {
                    state = appReducer(state, AppAction.LoadCompanion(result.data))
                }
            }
            is com.example.myspatial.core.Result.Error -> {
                errorMessage = result.message
            }
            else -> {}
        }
    }

    AppTheme {
        when (state.currentScreen) {
            Screen.CREATION -> {
                CompanionCreationScreen(
                    onComplete = { companion ->
                        state = appReducer(state, AppAction.CreateCompanion(companion))
                    },
                    onError = { error ->
                        errorMessage = error
                    }
                )
            }
            Screen.MAIN -> {
                state.companion?.let { companion ->
                    val context = androidx.compose.ui.platform.LocalContext.current
                    CompanionMainScreen(
                        companion = companion,
                        context = context,
                        onOpenMoodDiary = {
                            state = appReducer(state, AppAction.NavigateTo(Screen.MOOD_DIARY))
                        },
                        onOpenSettings = {
                            state = appReducer(state, AppAction.NavigateTo(Screen.SETTINGS))
                        },
                        onOpenARScene = {
                            state = appReducer(state, AppAction.NavigateTo(Screen.AR_SCENE))
                        },
                        onOpenXRScene = {
                            state = appReducer(state, AppAction.NavigateTo(Screen.XR_SCENE))
                        },
                        onOpenVoiceRecording = {
                            state = appReducer(state, AppAction.NavigateTo(Screen.VOICE_RECORDING))
                        },
                        onOpenLearningContent = {
                            state = appReducer(state, AppAction.NavigateTo(Screen.LEARNING_CONTENT))
                        }
                    )
                } ?: run {
                    state = appReducer(state, AppAction.NavigateTo(Screen.CREATION))
                }
            }
            Screen.AR_SCENE -> {
                state.companion?.let { companion ->
                    val context = androidx.compose.ui.platform.LocalContext.current
                    ARSceneScreen(
                        companion = companion,
                        context = context,
                        onBack = {
                            state = appReducer(state, AppAction.NavigateTo(Screen.MAIN))
                        }
                    )
                } ?: run {
                    state = appReducer(state, AppAction.NavigateTo(Screen.CREATION))
                }
            }
            Screen.MOOD_DIARY -> {
                MoodDiaryScreen(
                    onBack = {
                        state = appReducer(state, AppAction.NavigateTo(Screen.MAIN))
                    }
                )
            }
            Screen.XR_SCENE -> {
                state.companion?.let { companion ->
                    val context = androidx.compose.ui.platform.LocalContext.current
                    XRSceneScreen(
                        companion = companion,
                        context = context,
                        onBack = {
                            state = appReducer(state, AppAction.NavigateTo(Screen.MAIN))
                        }
                    )
                } ?: run {
                    state = appReducer(state, AppAction.NavigateTo(Screen.CREATION))
                }
            }
            Screen.SETTINGS -> {
                state.companion?.let { companion ->
                    SettingsScreen(
                        companion = companion,
                        onBack = {
                            state = appReducer(state, AppAction.NavigateTo(Screen.MAIN))
                        },
                        onReset = {
                            UseCaseProvider.deleteCompanionUseCase.execute()
                            state = appReducer(state, AppAction.ResetCompanion)
                        }
                    )
                } ?: run {
                    state = appReducer(state, AppAction.NavigateTo(Screen.CREATION))
                }
            }
            Screen.VOICE_RECORDING -> {
                VoiceRecordingScreen(
                    onComplete = { voiceProfile ->
                        state = appReducer(state, AppAction.UpdateVoiceProfile(voiceProfile))
                        state = appReducer(state, AppAction.NavigateTo(Screen.MAIN))
                    },
                    onBack = {
                        state = appReducer(state, AppAction.NavigateTo(Screen.MAIN))
                    }
                )
            }
            Screen.LEARNING_CONTENT -> {
                val context = androidx.compose.ui.platform.LocalContext.current
                LearningContentScreen(
                    context = context,
                    onBack = {
                        state = appReducer(state, AppAction.NavigateTo(Screen.MAIN))
                    }
                )
            }
        }
    }

    if (errorMessage != null) {
        androidx.compose.material.AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { androidx.compose.material.Text("提示") },
            text = { androidx.compose.material.Text(errorMessage ?: "") },
            confirmButton = {
                androidx.compose.material.Button(onClick = { errorMessage = null }) {
                    androidx.compose.material.Text("确定")
                }
            }
        )
    }
}
