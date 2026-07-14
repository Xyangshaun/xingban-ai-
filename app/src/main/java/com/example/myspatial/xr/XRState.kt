package com.example.myspatial.xr

import com.example.myspatial.data.Companion

data class XRState(
    val companion: Companion? = null,
    val isImmersiveMode: Boolean = false,
    val companionPosition: CompanionPosition = CompanionPosition(),
    val companionAnimation: CompanionAnimation = CompanionAnimation.IDLE,
    val showConversation: Boolean = false,
    val showMoodPanel: Boolean = false,
    val gazeTarget: String? = null,
    val interactionMode: InteractionMode = InteractionMode.GAZE,
    val errorMessage: String? = null
)

data class CompanionPosition(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 2f
)

enum class CompanionAnimation {
    IDLE, BREATHING, BLINKING, WAVING, LISTENING, TALKING, HAPPY, SAD
}

enum class InteractionMode {
    GAZE, HAND, VOICE, TOUCH
}

sealed class XRAction {
    data class SetImmersiveMode(val immersive: Boolean) : XRAction()
    data class SetCompanionPosition(val position: CompanionPosition) : XRAction()
    data class SetCompanionAnimation(val animation: CompanionAnimation) : XRAction()
    data class ToggleConversation(val show: Boolean) : XRAction()
    data class ToggleMoodPanel(val show: Boolean) : XRAction()
    data class SetGazeTarget(val target: String?) : XRAction()
    data class SetInteractionMode(val mode: InteractionMode) : XRAction()
    data class SetErrorMessage(val message: String?) : XRAction()
}

fun xrReducer(state: XRState, action: XRAction): XRState {
    return when (action) {
        is XRAction.SetImmersiveMode -> state.copy(isImmersiveMode = action.immersive)
        is XRAction.SetCompanionPosition -> state.copy(companionPosition = action.position)
        is XRAction.SetCompanionAnimation -> state.copy(companionAnimation = action.animation)
        is XRAction.ToggleConversation -> state.copy(showConversation = action.show)
        is XRAction.ToggleMoodPanel -> state.copy(showMoodPanel = action.show)
        is XRAction.SetGazeTarget -> state.copy(gazeTarget = action.target)
        is XRAction.SetInteractionMode -> state.copy(interactionMode = action.mode)
        is XRAction.SetErrorMessage -> state.copy(errorMessage = action.message)
    }
}