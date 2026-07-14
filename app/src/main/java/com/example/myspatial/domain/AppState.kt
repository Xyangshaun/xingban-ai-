package com.example.myspatial.domain

import com.example.myspatial.data.Companion
import com.example.myspatial.data.Message
import com.example.myspatial.data.MoodTrend

data class AppState(
    val currentScreen: Screen = Screen.CREATION,
    val companion: Companion? = null,
    val messages: List<Message> = emptyList(),
    val moodTrend: MoodTrend? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class Screen {
    CREATION,
    MAIN,
    AR_SCENE,
    XR_SCENE,
    MOOD_DIARY,
    SETTINGS,
    VOICE_RECORDING,
    LEARNING_CONTENT
}

sealed class AppAction {
    data class CreateCompanion(val companion: Companion) : AppAction()
    data class LoadCompanion(val companion: Companion?) : AppAction()
    data class UpdateVoiceProfile(val voiceProfile: com.example.myspatial.data.VoiceProfile) : AppAction()
    data class DeleteCompanion(val confirmed: Boolean) : AppAction()
    data class SendMessage(val message: Message) : AppAction()
    data class ReceiveMessage(val message: Message) : AppAction()
    data class LoadMessages(val messages: List<Message>) : AppAction()
    data class LoadMoodTrend(val trend: MoodTrend) : AppAction()
    data class NavigateTo(val screen: Screen) : AppAction()
    data class ShowLoading(val loading: Boolean) : AppAction()
    data class ShowError(val message: String?) : AppAction()
    object ResetCompanion : AppAction()
}

fun appReducer(state: AppState, action: AppAction): AppState {
    return when (action) {
        is AppAction.CreateCompanion -> state.copy(
            companion = action.companion,
            currentScreen = Screen.MAIN,
            errorMessage = null
        )
        is AppAction.LoadCompanion -> state.copy(
            companion = action.companion,
            currentScreen = if (action.companion != null) Screen.MAIN else Screen.CREATION,
            errorMessage = null
        )
        is AppAction.UpdateVoiceProfile -> state.copy(
            companion = state.companion?.copy(voiceProfile = action.voiceProfile),
            errorMessage = null
        )
        is AppAction.DeleteCompanion -> if (action.confirmed) {
            state.copy(
                companion = null,
                currentScreen = Screen.CREATION,
                messages = emptyList(),
                errorMessage = null
            )
        } else {
            state
        }
        is AppAction.SendMessage -> state.copy(
            messages = state.messages + action.message,
            errorMessage = null
        )
        is AppAction.ReceiveMessage -> state.copy(
            messages = state.messages + action.message,
            errorMessage = null
        )
        is AppAction.LoadMessages -> state.copy(
            messages = action.messages,
            errorMessage = null
        )
        is AppAction.LoadMoodTrend -> state.copy(
            moodTrend = action.trend,
            errorMessage = null
        )
        is AppAction.NavigateTo -> state.copy(
            currentScreen = action.screen,
            errorMessage = null
        )
        is AppAction.ShowLoading -> state.copy(
            isLoading = action.loading
        )
        is AppAction.ShowError -> state.copy(
            isLoading = false,
            errorMessage = action.message
        )
        AppAction.ResetCompanion -> state.copy(
            companion = null,
            currentScreen = Screen.CREATION,
            messages = emptyList(),
            moodTrend = null,
            errorMessage = null
        )
    }
}
