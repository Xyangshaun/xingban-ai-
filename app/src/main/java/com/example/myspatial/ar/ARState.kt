package com.example.myspatial.ar

import com.example.myspatial.data.Companion

enum class PositionMode {
    FOLLOW,
    FIXED
}

enum class UIMode {
    SIMPLE,
    FULL
}

data class ARState(
    val companion: Companion? = null,
    val isCameraPermissionGranted: Boolean = false,
    val isAudioPermissionGranted: Boolean = false,
    val isCameraPreviewStarted: Boolean = false,
    val isRecording: Boolean = false,
    val companionPosition: CompanionPosition = CompanionPosition(),
    val companionAnimation: CompanionAnimation = CompanionAnimation.IDLE,
    val showConversation: Boolean = false,
    val errorMessage: String? = null,
    val positionMode: PositionMode = PositionMode.FOLLOW,
    val uiMode: UIMode = UIMode.FULL,
    val hasInitiatedGreeting: Boolean = false,
    val isCompanionSpeaking: Boolean = false
)

data class CompanionPosition(
    val x: Float = 0.5f,
    val y: Float = 0.7f,
    val scale: Float = 1.0f
)

enum class CompanionAnimation {
    IDLE,
    BREATHING,
    BLINKING,
    WAVING,
    LISTENING,
    TALKING,
    HAPPY,
    SAD
}

sealed class ARAction {
    data class SetCameraPermission(val granted: Boolean) : ARAction()
    data class SetAudioPermission(val granted: Boolean) : ARAction()
    data class StartCameraPreview(val started: Boolean) : ARAction()
    data class ToggleRecording(val recording: Boolean) : ARAction()
    data class UpdateCompanionPosition(val position: CompanionPosition) : ARAction()
    data class SetCompanionAnimation(val animation: CompanionAnimation) : ARAction()
    data class ToggleConversation(val show: Boolean) : ARAction()
    data class SetErrorMessage(val message: String?) : ARAction()
    data class SetPositionMode(val mode: PositionMode) : ARAction()
    data class SetUIMode(val mode: UIMode) : ARAction()
    object MarkGreetingInitiated : ARAction()
    data class SetIsSpeaking(val speaking: Boolean) : ARAction()
}

fun arReducer(state: ARState, action: ARAction): ARState {
    return when (action) {
        is ARAction.SetCameraPermission -> state.copy(isCameraPermissionGranted = action.granted)
        is ARAction.SetAudioPermission -> state.copy(isAudioPermissionGranted = action.granted)
        is ARAction.StartCameraPreview -> state.copy(isCameraPreviewStarted = action.started)
        is ARAction.ToggleRecording -> state.copy(isRecording = action.recording)
        is ARAction.UpdateCompanionPosition -> state.copy(companionPosition = action.position)
        is ARAction.SetCompanionAnimation -> state.copy(companionAnimation = action.animation)
        is ARAction.ToggleConversation -> state.copy(showConversation = action.show)
        is ARAction.SetErrorMessage -> state.copy(errorMessage = action.message)
        is ARAction.SetPositionMode -> state.copy(positionMode = action.mode)
        is ARAction.SetUIMode -> state.copy(uiMode = action.mode)
        is ARAction.MarkGreetingInitiated -> state.copy(hasInitiatedGreeting = true)
        is ARAction.SetIsSpeaking -> state.copy(isCompanionSpeaking = action.speaking)
    }
}