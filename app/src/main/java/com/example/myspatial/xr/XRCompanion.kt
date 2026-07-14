package com.example.myspatial.xr

import androidx.compose.ui.graphics.Color
import com.example.myspatial.data.Companion
import com.example.myspatial.data.CompanionCharacter
import com.example.myspatial.data.CompanionPersonality

class XRCompanion(val companion: Companion) {
    private var animationState: CompanionAnimation = CompanionAnimation.IDLE
    private var animationProgress: Float = 0f
    private var targetPosition: CompanionPosition = CompanionPosition()
    private var currentPosition: CompanionPosition = CompanionPosition()

    fun update(deltaTime: Float) {
        updateAnimation(deltaTime)
        updatePosition(deltaTime)
    }

    private fun updateAnimation(deltaTime: Float) {
        animationProgress += deltaTime
        when (animationState) {
            CompanionAnimation.IDLE -> {
                if (animationProgress > 4f) {
                    animationState = CompanionAnimation.BREATHING
                    animationProgress = 0f
                }
            }
            CompanionAnimation.BREATHING -> {
                if (animationProgress > 3f) {
                    animationState = CompanionAnimation.IDLE
                    animationProgress = 0f
                }
            }
            CompanionAnimation.TALKING -> {
            }
            CompanionAnimation.LISTENING -> {
            }
            CompanionAnimation.WAVING -> {
                if (animationProgress > 2f) {
                    animationState = CompanionAnimation.IDLE
                    animationProgress = 0f
                }
            }
            else -> {
                if (animationProgress > 2f) {
                    animationState = CompanionAnimation.IDLE
                    animationProgress = 0f
                }
            }
        }
    }

    private fun updatePosition(deltaTime: Float) {
        val lerpFactor = 5f * deltaTime
        currentPosition = CompanionPosition(
            x = currentPosition.x + (targetPosition.x - currentPosition.x) * lerpFactor,
            y = currentPosition.y + (targetPosition.y - currentPosition.y) * lerpFactor,
            z = currentPosition.z + (targetPosition.z - currentPosition.z) * lerpFactor
        )
    }

    fun setAnimation(animation: CompanionAnimation) {
        animationState = animation
        animationProgress = 0f
    }

    fun setTargetPosition(position: CompanionPosition) {
        targetPosition = position
    }

    fun getCurrentAnimation(): CompanionAnimation = animationState

    fun getCurrentPosition(): CompanionPosition = currentPosition

    fun getAnimationScale(): Float {
        return when (animationState) {
            CompanionAnimation.BREATHING -> 1f + kotlin.math.sin(animationProgress * kotlin.math.PI).toFloat() * 0.03f
            CompanionAnimation.TALKING -> 1f + kotlin.math.sin(animationProgress * kotlin.math.PI * 6).toFloat() * 0.02f
            CompanionAnimation.WAVING -> 1f + kotlin.math.sin(animationProgress * kotlin.math.PI * 4).toFloat() * 0.08f
            else -> 1f
        }
    }

    fun getCharacterColor(): Color {
        return companion.character.getCharacterColor()
    }

    fun getCharacterIcon(): String {
        return companion.character.getCharacterIcon()
    }

    fun getGreeting(): String {
        return when (companion.personality) {
            CompanionPersonality.GENTLE -> "你好呀~ 我是${companion.name}，很高兴在这里见到你！"
            CompanionPersonality.ACTIVE -> "嗨嗨！${companion.name}来啦！准备好一起探索这个世界了吗？"
            CompanionPersonality.QUIET -> "你好...我是${companion.name}，如果你想聊天，我会在这里听你说的。"
            CompanionPersonality.CURIOUS -> "哇！你终于来了！我是${companion.name}，对你的世界充满好奇！"
            CompanionPersonality.CAREFUL -> "你好，我是${companion.name}。让我来好好陪伴你吧~"
        }
    }
}