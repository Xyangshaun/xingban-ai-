package com.example.myspatial.domain

import android.content.Context
import com.example.myspatial.data.CompanionRepository
import com.example.myspatial.data.SharedPrefsDataSource

object UseCaseProvider {

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    private val dataSource by lazy { SharedPrefsDataSource(context) }

    private val repository by lazy {
        CompanionRepository(
            companionDataSource = dataSource,
            conversationDataSource = dataSource,
            moodDataSource = dataSource
        )
    }

    val createCompanionUseCase by lazy { CreateCompanionUseCase(repository) }
    val getCompanionUseCase by lazy { GetCompanionUseCase(repository) }
    val deleteCompanionUseCase by lazy { DeleteCompanionUseCase(repository) }
    val sendMessageUseCase by lazy { SendMessageUseCase(repository) }
    val receiveMessageUseCase by lazy { ReceiveMessageUseCase(repository) }
    val getMessagesUseCase by lazy { GetMessagesUseCase(repository) }
    val recordMoodUseCase by lazy { RecordMoodUseCase(repository) }
    val getMoodTrendUseCase by lazy { GetMoodTrendUseCase(repository) }
    val getMoodRecordsUseCase by lazy { GetMoodRecordsUseCase(repository) }
}
