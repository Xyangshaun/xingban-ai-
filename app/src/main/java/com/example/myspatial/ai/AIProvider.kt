package com.example.myspatial.ai

import android.content.Context
import android.content.SharedPreferences

object AIProvider {
    private const val PREFS_NAME = "MySpatialAppPrefs"
    private const val KEY_QWEN_API_KEY = "qwen_api_key"
    
    private const val DEFAULT_DEMO_API_KEY = ""
    
    private var llmClientInstance: LLMClient? = null
    
    val emotionAnalyzer: EmotionAnalyzer by lazy { KeywordEmotionAnalyzer() }
    
    private var voiceServiceInstance: VoiceService? = null
    
    fun getLLMClient(context: Context): LLMClient {
        if (llmClientInstance == null) {
            val apiKey = getEffectiveApiKey(context)
            android.util.Log.i("AIProvider", "Using API Key: ${if (apiKey.isEmpty()) "NONE" else "configured"}")
            
            llmClientInstance = if (apiKey.isNotEmpty()) {
                QwenApiClient(apiKey = apiKey)
            } else {
                MockLLMClient()
            }
        }
        return llmClientInstance!!
    }
    
    private fun getEffectiveApiKey(context: Context): String {
        val userKey = getUserApiKey(context)
        if (userKey.isNotEmpty()) {
            return userKey
        }
        
        if (DEFAULT_DEMO_API_KEY.isNotEmpty()) {
            return DEFAULT_DEMO_API_KEY
        }
        
        return ""
    }
    
    fun setApiKey(context: Context, apiKey: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_QWEN_API_KEY, apiKey).apply()
        llmClientInstance = null
    }
    
    fun getApiKey(context: Context): String {
        return getUserApiKey(context)
    }
    
    private fun getUserApiKey(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_QWEN_API_KEY, "") ?: ""
    }
    
    fun getVoiceService(context: Context): VoiceService {
        if (voiceServiceInstance == null) {
            voiceServiceInstance = AndroidVoiceService(context)
        }
        return voiceServiceInstance!!
    }
    
    fun resetVoiceService() {
        (voiceServiceInstance as? AndroidVoiceService)?.destroy()
        voiceServiceInstance = null
    }
    
    fun resetLLMClient() {
        llmClientInstance = null
    }
}
