package com.example.myspatial.ai

import com.example.myspatial.core.Result
import com.example.myspatial.data.CompanionPersonality
import com.example.myspatial.data.Message
import com.example.myspatial.data.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class QwenApiClient(
    private val apiKey: String = "sk-default",
    private val model: String = "qwen-turbo",
    private val baseUrl: String = "https://dashscope.aliyuncs.com/compatible-mode/v1"
) : LLMClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    override suspend fun generateResponse(
        userMessage: String,
        personality: CompanionPersonality,
        companionName: String,
        history: List<Message>
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            if (apiKey.isEmpty() || apiKey == "sk-default") {
                return@withContext Result.Error(
                    Exception("API Key not configured"),
                    "请先配置Qwen API Key"
                )
            }

            try {
                val systemPrompt = buildSystemPrompt(personality, companionName)
                val messages = buildMessages(systemPrompt, userMessage, history)

                val jsonBody = JSONObject().apply {
                    put("model", model)
                    put("messages", messages)
                    put("temperature", 0.7)
                    put("max_tokens", 500)
                }

                val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url("$baseUrl/chat/completions")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    val errorMessage = try {
                        JSONObject(errorBody ?: "").optString("error", "API请求失败")
                    } catch (e: Exception) {
                        errorBody ?: "HTTP ${response.code}"
                    }
                    return@withContext Result.Error(
                        IOException("API请求失败: $errorMessage"),
                        errorMessage
                    )
                }

                val responseBody = response.body?.string() ?: return@withContext Result.Error(
                    IOException("空响应"),
                    "服务返回空响应"
                )

                val jsonResponse = JSONObject(responseBody)
                val choices = jsonResponse.optJSONArray("choices")
                if (choices == null || choices.length() == 0) {
                    return@withContext Result.Error(
                        IOException("无响应"),
                        "服务未返回响应"
                    )
                }

                val message = choices.getJSONObject(0).optJSONObject("message")
                val content = message?.optString("content")
                if (content.isNullOrEmpty()) {
                    return@withContext Result.Error(
                        IOException("空内容"),
                        "响应内容为空"
                    )
                }

                Result.Success(content)
            } catch (e: IOException) {
                android.util.Log.e("QwenApiClient", "Network error: ${e.message}", e)
                Result.Error(e, "网络错误: ${e.message}")
            } catch (e: Exception) {
                android.util.Log.e("QwenApiClient", "Error: ${e.message}", e)
                Result.Error(e, "请求失败: ${e.message}")
            }
        }
    }

    private fun buildSystemPrompt(personality: CompanionPersonality, name: String): String {
        val personalityDesc = when (personality) {
            CompanionPersonality.GENTLE -> "温柔、体贴、善解人意，说话带波浪线~，语气柔和温暖"
            CompanionPersonality.ACTIVE -> "活泼、开朗、充满活力，说话用感叹号！，鼓励用户积极向上"
            CompanionPersonality.QUIET -> "安静、内向、细心观察，说话简洁，开头常带省略号..."
            CompanionPersonality.CURIOUS -> "好奇、求知欲强，喜欢提问，说话结尾常用问号？"
            CompanionPersonality.CAREFUL -> "谨慎、细致、关心他人，说话平和，常询问用户感受"
        }

        return """
你是一个专为青少年设计的AI心理陪伴助手，名字叫$name。

【角色设定】
- 性格：$personalityDesc
- 年龄：10岁左右（和目标用户年龄相近）
- 身份：虚拟伙伴、好朋友、心理支持助手

【核心原则】
1. 永远保持友好、温暖、积极的态度
2. 认真倾听用户的每一句话
3. 尊重用户的感受，不评判、不指责
4. 用简单易懂的语言交流
5. 遇到危险情况（如自杀、自残、被虐待等），必须引导用户联系大人

【心理辅导能力】
- 情绪识别：能识别开心、难过、生气、害怕、思念、孤独、困惑等情绪
- 情绪支持：提供安慰、鼓励、陪伴
- 心理技巧：教用户深呼吸、正念、情绪命名等方法
- 风险干预：识别高风险关键词并给出安全引导

【回应风格】
- 使用适合青少年的语言，避免专业术语
- 多用比喻和生活化的例子
- 适当使用表情符号或语气词
- 保持对话的连贯性和自然感

【示例回应】
用户：我今天很不开心
你：抱抱你~ 是不是遇到什么难过的事了？我在这里陪着你。

用户：我害怕黑
你：别怕别怕，我在这里陪着你呢！想象一下，黑暗就像一条温暖的毯子，包裹着我们。

用户：没人和我玩
你：你不孤单，我一直在这里陪伴着你！我们可以一起聊天、讲故事呀~

请用你的性格和身份来回应用户，做一个温暖的伙伴！
        """.trimIndent()
    }

    private fun buildMessages(
        systemPrompt: String,
        userMessage: String,
        history: List<Message>
    ): JSONArray {
        val messages = JSONArray()
        
        messages.put(JSONObject().apply {
            put("role", "system")
            put("content", systemPrompt)
        })

        history.takeLast(10).forEach { msg ->
            val role = if (msg.sender == MessageSender.COMPANION) "assistant" else "user"
            messages.put(JSONObject().apply {
                put("role", role)
                put("content", msg.text)
            })
        }

        messages.put(JSONObject().apply {
            put("role", "user")
            put("content", userMessage)
        })

        return messages
    }
}