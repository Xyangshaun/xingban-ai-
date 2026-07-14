package com.example.myspatial

import android.app.Application
import android.content.Context
import com.example.myspatial.domain.UseCaseProvider
import com.pico.spatial.ui.foundation.dsl.launch

class MySpatialApp : Application() {

    companion object {
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        UseCaseProvider.init(this)
        initVoiceService()
        launch(::mainApp)
    }

    private fun initVoiceService() {
        try {
            android.util.Log.i("MySpatialApp", "Pre-initializing VoiceService...")
            com.example.myspatial.ai.AIProvider.getVoiceService(this)
            android.util.Log.i("MySpatialApp", "VoiceService pre-initialization complete")
        } catch (e: Throwable) {
            android.util.Log.e("MySpatialApp", "VoiceService pre-init failed: ${e.message}", e)
        }
    }
}
