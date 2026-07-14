package com.example.myspatial.ar

import com.example.myspatial.data.Companion
import com.example.myspatial.data.CompanionCharacter
import com.example.myspatial.data.CompanionPersonality
import androidx.compose.ui.graphics.Color

class ARCompanion(
    private val companion: Companion
) {
    private var animationState: CompanionAnimation = CompanionAnimation.IDLE
    private var animationProgress: Float = 0f

    fun getCharacterIcon(): String {
        return companion.character.getCharacterIcon()
    }

    fun getCharacterName(): String {
        return companion.character.name
    }

    fun getPersonalityName(): String {
        return companion.personality.getPersonalityName()
    }

    fun getCharacterColor(): Color {
        return companion.character.getCharacterColor()
    }

    fun getGreeting(): String {
        return companion.getGreeting()
    }

    fun updateAnimation(deltaTime: Float) {
        animationProgress += deltaTime
        when (animationState) {
            CompanionAnimation.IDLE -> {
                if (animationProgress > 3f) {
                    animationState = CompanionAnimation.BREATHING
                    animationProgress = 0f
                }
            }
            CompanionAnimation.BREATHING -> {
                if (animationProgress > 2f) {
                    animationState = CompanionAnimation.IDLE
                    animationProgress = 0f
                }
            }
            CompanionAnimation.BLINKING -> {
                if (animationProgress > 0.5f) {
                    animationState = CompanionAnimation.IDLE
                    animationProgress = 0f
                }
            }
            CompanionAnimation.WAVING -> {
                if (animationProgress > 1.5f) {
                    animationState = CompanionAnimation.IDLE
                    animationProgress = 0f
                }
            }
            CompanionAnimation.LISTENING -> {
                if (animationProgress > 5f) {
                    animationState = CompanionAnimation.IDLE
                    animationProgress = 0f
                }
            }
            CompanionAnimation.TALKING -> {
                if (animationProgress > 0.2f) {
                    animationProgress = 0f
                }
            }
            else -> {}
        }
    }

    fun setAnimation(animation: CompanionAnimation) {
        if (animation != CompanionAnimation.IDLE || animationState == CompanionAnimation.IDLE) {
            animationState = animation
            animationProgress = 0f
        }
    }

    fun getCurrentAnimation(): CompanionAnimation = animationState

    fun getAnimationScale(): Float {
        return when (animationState) {
            CompanionAnimation.BREATHING -> 1f + Math.sin(animationProgress * Math.PI).toFloat() * 0.05f
            CompanionAnimation.TALKING -> 1f + Math.sin(animationProgress * Math.PI * 5).toFloat() * 0.03f
            else -> 1f
        }
    }

    fun getAnimationRotation(): Float {
        return when (animationState) {
            CompanionAnimation.WAVING -> Math.sin(animationProgress * Math.PI * 4).toFloat() * 30f
            CompanionAnimation.LISTENING -> Math.sin(animationProgress * Math.PI * 2).toFloat() * 5f
            else -> 0f
        }
    }
}