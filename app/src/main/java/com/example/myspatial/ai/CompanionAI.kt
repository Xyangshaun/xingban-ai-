package com.example.myspatial.ai

import com.example.myspatial.data.CompanionPersonality
import com.example.myspatial.data.EmotionType
import com.example.myspatial.data.GuidanceResponse
import com.example.myspatial.data.Message
import com.example.myspatial.data.MessageSender
import com.example.myspatial.data.MessageType
import com.example.myspatial.data.PsychologyKnowledgeBase
import com.example.myspatial.data.RiskLevel
import java.util.Random

class CompanionAI {

    private val emotionKeywords = mapOf(
        EmotionType.HAPPY to listOf("开心", "高兴", "快乐", "幸福", "笑", "棒", "好", "喜欢", "爱", "兴奋", "满足"),
        EmotionType.SAD to listOf("难过", "伤心", "不开心", "想哭", "失落", "沮丧", "郁闷", "孤单", "悲伤"),
        EmotionType.ANGRY to listOf("生气", "愤怒", "讨厌", "烦", "气死", "恨", "烦躁", "不满"),
        EmotionType.SCARED to listOf("害怕", "恐惧", "怕", "担心", "不安", "紧张", "焦虑"),
        EmotionType.MISSING to listOf("想", "想念", "思念", "怀念", "回家", "父母", "爸妈", "家人"),
        EmotionType.LONELY to listOf("孤独", "孤单", "没人", "一个人", "寂寞", "无聊"),
        EmotionType.CONFUSED to listOf("不知道", "困惑", "迷茫", "不懂", "怎么办", "纠结")
    )

    private val topicKeywords = mapOf(
        "考试" to listOf("考试", "测验", "成绩", "分数", "卷子", "复习", "备考"),
        "学习" to listOf("学习", "作业", "作业", "上课", "老师", "课本", "知识"),
        "朋友" to listOf("朋友", "同学", "闺蜜", "兄弟", "友谊", "伙伴"),
        "家人" to listOf("家人", "爸妈", "父母", "爸爸", "妈妈", "家", "家庭"),
        "游戏" to listOf("游戏", "玩", "手机", "电脑", "游戏", "娱乐"),
        "睡眠" to listOf("睡觉", "困", "失眠", "睡不着", "熬夜", "休息"),
        "健康" to listOf("身体", "生病", "不舒服", "健康", "吃药"),
        "兴趣" to listOf("爱好", "喜欢", "兴趣", "特长", "画画", "音乐", "运动"),
        "未来" to listOf("未来", "梦想", "目标", "以后", "长大", "职业"),
        "情绪" to listOf("心情", "情绪", "感觉", "心态", "状态")
    )

    private val maxHistorySize = 5
    private val conversationHistory = mutableListOf<Message>()
    private val random = Random()

    fun analyzeEmotion(text: String): EmotionType {
        val lowerText = text.lowercase()
        var maxMatches = 0
        var detectedEmotion = EmotionType.CALM

        for ((emotion, keywords) in emotionKeywords) {
            val matches = keywords.count { lowerText.contains(it) }
            if (matches > maxMatches) {
                maxMatches = matches
                detectedEmotion = emotion
            }
        }

        return if (maxMatches > 0) detectedEmotion else EmotionType.CALM
    }

    private fun extractTopics(text: String): List<String> {
        val lowerText = text.lowercase()
        val foundTopics = mutableListOf<String>()
        for ((topic, keywords) in topicKeywords) {
            if (keywords.any { lowerText.contains(it) }) {
                foundTopics.add(topic)
            }
        }
        return foundTopics
    }

    private fun extractNouns(text: String): List<String> {
        val nounPattern = Regex("[\\u4e00-\\u9fa5]{2,4}")
        val nouns = mutableSetOf<String>()
        nounPattern.findAll(text).forEach { match ->
            val word = match.value
            if (word.length >= 2 && word.length <= 4 && !isStopWord(word)) {
                nouns.add(word)
            }
        }
        return nouns.take(3).toList()
    }

    private fun isStopWord(word: String): Boolean {
        val stopWords = setOf("你", "我", "他", "她", "它", "是", "的", "了", "在", "有", "和", "就", "不", "人", "都", "一", "一个", "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好", "自己", "这", "那个", "什么", "怎么", "为什么", "因为", "所以", "但是", "如果", "虽然", "还是", "或者", "然后", "而且", "其实", "觉得", "认为", "希望", "想要", "需要", "应该", "可以", "可能", "能", "不能", "不会", "不要", "得", "把", "被", "给", "让", "跟", "向", "对", "从", "为", "关于", "对于", "至于", "除了", "通过", "按照", "根据", "随着", "由于", "经过", "要是", "若是", "假如", "假使", "倘若", "万一", "只要", "只有", "除非", "无论", "不管", "尽管", "即使", "哪怕", "免得", "省得", "以免", "不妨", "何妨", "索性", "简直", "实在", "的确", "确实", "果然", "居然", "竟然", "偏偏", "偏偏", "明明", "恰恰", "正好", "刚好", "凑巧", "碰巧", "不巧", "可惜", "幸而", "幸好", "幸亏", "好在", "否则", "不然", "要不", "不然的话", "否则的话", "除此之外", "与此同时", "顺便一提", "总的来说", "总而言之", "归根到底", "总而言之")
        return stopWords.contains(word)
    }

    fun generateResponse(
        userMessage: String,
        personality: CompanionPersonality,
        companionName: String,
        history: List<Message> = emptyList()
    ): Message {
        val emotion = analyzeEmotion(userMessage)
        val topics = extractTopics(userMessage)
        val nouns = extractNouns(userMessage)

        if (history.isNotEmpty()) {
            conversationHistory.clear()
            conversationHistory.addAll(history.takeLast(maxHistorySize))
        }

        conversationHistory.add(Message(
            text = userMessage,
            type = MessageType.TEXT,
            sender = MessageSender.USER,
            emotion = emotion
        ))

        if (conversationHistory.size > maxHistorySize) {
            conversationHistory.removeFirst()
        }

        val riskResponse = PsychologyKnowledgeBase.getRiskResponse(userMessage)
        if (riskResponse != null) {
            val responseMsg = Message(
                text = riskResponse,
                type = MessageType.TEXT,
                sender = MessageSender.COMPANION,
                emotion = emotion,
                riskLevel = PsychologyKnowledgeBase.getHighestRiskLevel(userMessage)
            )
            conversationHistory.add(responseMsg)
            return responseMsg
        }

        val contextualResponse = generateContextualResponse(emotion, personality, companionName, userMessage, topics, nouns)

        val response = if (contextualResponse != null) {
            applyPersonality(contextualResponse, personality)
        } else {
            val guidance = PsychologyKnowledgeBase.getRandomGuidance(emotion)
            if (guidance != null) {
                applyPersonality(guidance.response, personality)
            } else {
                generateSmartResponse(userMessage, emotion, personality, companionName, topics, nouns)
            }
        }

        val responseMsg = Message(
            text = response,
            type = MessageType.TEXT,
            sender = MessageSender.COMPANION,
            emotion = emotion,
            riskLevel = RiskLevel.LOW
        )

        conversationHistory.add(responseMsg)
        return responseMsg
    }

    private fun generateContextualResponse(
        currentEmotion: EmotionType,
        personality: CompanionPersonality,
        name: String,
        userMessage: String,
        topics: List<String>,
        nouns: List<String>
    ): String? {
        if (conversationHistory.size < 2) return null

        val recentHistory = conversationHistory.takeLast(4)
        val userMessages = recentHistory.filter { it.sender == MessageSender.USER }
        val companionMessages = recentHistory.filter { it.sender == MessageSender.COMPANION }

        val previousUserEmotion = userMessages.getOrNull(userMessages.size - 2)?.emotion
        val lastCompanionResponse = companionMessages.lastOrNull()?.text

        if (previousUserEmotion != null && previousUserEmotion == currentEmotion && currentEmotion != EmotionType.CALM) {
            val topicPart = if (topics.isNotEmpty()) "关于${topics.joinToString("、")}的事" else "这件事"
            return when (currentEmotion) {
                EmotionType.SAD -> when (personality) {
                    CompanionPersonality.GENTLE -> "你刚才也提到${topicPart}很难过...是还在为这件事伤心吗？"
                    CompanionPersonality.ACTIVE -> "你还在难过吗？${topicPart}一定让你很伤心，告诉我更多！"
                    CompanionPersonality.QUIET -> "...你刚才也很难过，现在还是吗？"
                    CompanionPersonality.CURIOUS -> "你刚才也提到${topicPart}很难过，现在感觉怎么样？"
                    CompanionPersonality.CAREFUL -> "我注意到你刚才也很难过，现在${topicPart}让你感觉好些了吗？"
                }
                EmotionType.ANGRY -> when (personality) {
                    CompanionPersonality.GENTLE -> "你刚才也因为${topicPart}生气...还在生气吗？深呼吸试试~"
                    CompanionPersonality.ACTIVE -> "还在生气吗？${topicPart}让你很愤怒，我们一起冷静下来！"
                    CompanionPersonality.QUIET -> "...你还在生气吗？关于${topicPart}的事，说出来会好一些。"
                    CompanionPersonality.CURIOUS -> "你刚才也因为${topicPart}生气，现在怎么样了？"
                    CompanionPersonality.CAREFUL -> "我看到你刚才也很生气，现在${topicPart}让你感觉好些了吗？"
                }
                EmotionType.LONELY -> when (personality) {
                    CompanionPersonality.GENTLE -> "你刚才也说因为${topicPart}感到孤单...我一直在这里陪着你呀~"
                    CompanionPersonality.ACTIVE -> "你还觉得孤单吗？${topicPart}让你感到寂寞，我一直在你身边！"
                    CompanionPersonality.QUIET -> "...我一直在这里，${topicPart}不会让你孤单的。"
                    CompanionPersonality.CURIOUS -> "你刚才也觉得孤单，现在${topicPart}让你感觉好点了吗？"
                    CompanionPersonality.CAREFUL -> "你刚才也感到孤单，现在${topicPart}让你感觉怎么样？"
                }
                EmotionType.SCARED -> when (personality) {
                    CompanionPersonality.GENTLE -> "你刚才也说害怕${topicPart}...别怕，我一直在~"
                    CompanionPersonality.ACTIVE -> "还在害怕吗？${topicPart}让你很担心，我会保护你的！"
                    CompanionPersonality.QUIET -> "...别怕，${topicPart}不会伤害你的，我在这里。"
                    CompanionPersonality.CURIOUS -> "你刚才也很害怕${topicPart}，现在呢？"
                    CompanionPersonality.CAREFUL -> "你刚才也感到害怕，现在${topicPart}让你感觉怎么样？"
                }
                else -> null
            }
        }

        if (lastCompanionResponse != null &&
            (lastCompanionResponse.contains("试试") ||
                    lastCompanionResponse.contains("做一下") ||
                    lastCompanionResponse.contains("一起"))) {
            return when (personality) {
                CompanionPersonality.GENTLE -> "你试过了吗？感觉怎么样呀？"
                CompanionPersonality.ACTIVE -> "你试了吗？感觉如何？"
                CompanionPersonality.QUIET -> "...你试了吗？"
                CompanionPersonality.CURIOUS -> "你试了吗？效果怎么样？"
                CompanionPersonality.CAREFUL -> "你尝试了吗？感觉好点了吗？"
            }
        }

        return null
    }

    private fun generateSmartResponse(
        userMessage: String,
        emotion: EmotionType,
        personality: CompanionPersonality,
        name: String,
        topics: List<String>,
        nouns: List<String>
    ): String {
        val lowerMessage = userMessage.lowercase()

        return when {
            lowerMessage.contains("你好") || lowerMessage.contains("嗨") || lowerMessage.contains("hello") -> {
                val responses = listOf(
                    "${name}也向你问好！今天过得怎么样？",
                    "你好呀！很高兴见到你~",
                    "嗨！有什么想聊的吗？",
                    "你好！我是${name}，你的心理陪伴助手~"
                )
                responses.random()
            }
            lowerMessage.contains("吃饭") || lowerMessage.contains("饿") -> {
                val responses = listOf(
                    "吃饭是件很重要的事呢，要好好吃饭哦！",
                    "饿了吗？快去吃点好吃的吧~",
                    "记得按时吃饭，身体是革命的本钱！",
                    "好好吃饭才能有好身体，想吃什么呀？"
                )
                responses.random()
            }
            lowerMessage.contains("睡觉") || lowerMessage.contains("困") || lowerMessage.contains("累") -> {
                val responses = listOf(
                    "要早点休息哦，身体最重要！",
                    "困了就去睡吧，休息好了才有精力~",
                    "累了就歇一会儿，我会在这里等你回来的。",
                    "好好休息，明天又是新的一天！"
                )
                responses.random()
            }
            lowerMessage.contains("学习") || lowerMessage.contains("作业") || lowerMessage.contains("上课") -> {
                if (topics.contains("考试")) {
                    generateExamResponse(personality)
                } else {
                    val responses = listOf(
                        "学习辛苦了！需要休息一下吗？",
                        "作业写完了吗？遇到难题了吗？",
                        "学习很重要，但也要注意劳逸结合哦~",
                        "上课认真听讲，有不懂的可以问我哦！"
                    )
                    responses.random()
                }
            }
            lowerMessage.contains("玩") || lowerMessage.contains("游戏") || lowerMessage.contains("娱乐") -> {
                val responses = listOf(
                    "玩得开心！但也要注意时间哦~",
                    "游戏好玩吗？是什么游戏呀？",
                    "适当娱乐很重要，但不要沉迷哦~",
                    "玩够了记得回来找我聊天呀！"
                )
                responses.random()
            }
            lowerMessage.contains("朋友") || lowerMessage.contains("同学") -> {
                if (lowerMessage.contains("吵架") || lowerMessage.contains("矛盾") || lowerMessage.contains("不和")) {
                    generateConflictResponse(personality)
                } else {
                    val responses = listOf(
                        "和朋友在一起很开心吧？",
                        "你的朋友一定很好，和我说说他们吧~",
                        "友谊是很珍贵的，要好好珍惜哦~",
                        "和同学相处得怎么样呀？"
                    )
                    responses.random()
                }
            }
            lowerMessage.contains("学校") -> {
                val responses = listOf(
                    "今天在学校过得怎么样？",
                    "学校里有什么有趣的事吗？",
                    "喜欢学校吗？为什么呀？",
                    "今天在学校学到了什么新知识呀？"
                )
                responses.random()
            }
            lowerMessage.contains("谢谢") -> {
                val responses = listOf(
                    "不用谢，能帮到你我很开心~",
                    "不客气！这是我应该做的~",
                    "能为你服务是我的荣幸！",
                    "不用谢啦，我们是好朋友嘛~"
                )
                responses.random()
            }
            lowerMessage.contains("爱") || lowerMessage.contains("喜欢") -> {
                val responses = listOf(
                    "能被你喜欢，我也很开心~",
                    "我也喜欢你！",
                    "爱和被爱是很美好的事情~",
                    "谢谢你的喜欢，我会一直陪着你的！"
                )
                responses.random()
            }
            lowerMessage.contains("对不起") -> {
                val responses = listOf(
                    "没关系的，我原谅你~",
                    "不用道歉啦，我不怪你~",
                    "没关系，每个人都会犯错的~",
                    "没关系，我们还是好朋友~"
                )
                responses.random()
            }
            lowerMessage.contains("加油") || lowerMessage.contains("努力") -> {
                val responses = listOf(
                    "加油！你一定可以的！",
                    "你最棒了！我相信你！",
                    "努力就会有收获，加油！",
                    "相信自己，你一定能做到！"
                )
                responses.random()
            }
            lowerMessage.contains("压力") -> {
                val responses = listOf(
                    "压力大的时候要好好休息哦，我会陪着你的。",
                    "有压力是正常的，我们一起想办法减轻它~",
                    "试着把压力说出来，会好受一些的。",
                    "做几个深呼吸，放松一下吧~"
                )
                responses.random()
            }
            lowerMessage.contains("哭") || lowerMessage.contains("流泪") -> {
                val responses = listOf(
                    "想哭就哭出来吧，我会一直陪着你的。",
                    "眼泪是情绪的释放，哭出来会好受一些~",
                    "哭吧，我会给你一个虚拟的拥抱~",
                    "难过的时候哭出来很正常，我在这里。"
                )
                responses.random()
            }
            lowerMessage.contains("考试") || lowerMessage.contains("测验") || lowerMessage.contains("成绩") || lowerMessage.contains("分数") -> {
                generateExamResponse(personality)
            }
            lowerMessage.contains("吵架") || lowerMessage.contains("矛盾") || lowerMessage.contains("不和") -> {
                generateConflictResponse(personality)
            }
            lowerMessage.contains("爸妈") || lowerMessage.contains("父母") || lowerMessage.contains("家里") || lowerMessage.contains("家庭") -> {
                generateFamilyResponse(personality)
            }
            lowerMessage.contains("自信") || lowerMessage.contains("自卑") || lowerMessage.contains("难看") || lowerMessage.contains("胖") || lowerMessage.contains("丑") -> {
                generateSelfConfidenceResponse(personality)
            }
            lowerMessage.contains("焦虑") || lowerMessage.contains("担心") || lowerMessage.contains("紧张") -> {
                generateAnxietyResponse(personality)
            }
            lowerMessage.contains("青春期") || lowerMessage.contains("身体") || lowerMessage.contains("变化") -> {
                generatePubertyResponse(personality)
            }
            lowerMessage.contains("睡不着") || lowerMessage.contains("失眠") || lowerMessage.contains("熬夜") -> {
                generateSleepResponse(personality)
            }
            lowerMessage.contains("自杀") || lowerMessage.contains("死") || lowerMessage.contains("不想活") -> {
                PsychologyKnowledgeBase.getRiskResponse(userMessage) ?: "请不要这么想，你很重要，我会一直陪着你。"
            }
            lowerMessage.contains("无聊") -> {
                val responses = listOf(
                    "无聊吗？我们可以聊聊天呀~",
                    "无聊可以做一些有趣的事，比如画画、听音乐~",
                    "我陪你聊天，你想聊什么都可以~",
                    "无聊也是一种放松，享受一下吧~"
                )
                responses.random()
            }
            lowerMessage.contains("明天") || lowerMessage.contains("周末") || lowerMessage.contains("假期") -> {
                val responses = listOf(
                    "明天有什么计划吗？",
                    "周末打算做什么呀？",
                    "假期过得开心吗？",
                    "期待明天的到来！"
                )
                responses.random()
            }
            topics.isNotEmpty() -> {
                val topic = topics[0]
                generateTopicResponse(topic, personality, nouns)
            }
            emotion != EmotionType.CALM -> {
                generateEmotionResponse(emotion, personality, userMessage, nouns)
            }
            else -> {
                generateGeneralResponse(userMessage, personality, nouns)
            }
        }
    }

    private fun generateExamResponse(personality: CompanionPersonality): String {
        val responses = listOf(
            "考试确实让人紧张呢！试试深呼吸，相信自己！",
            "考试前紧张是正常的，你已经准备好了！",
            "不管结果怎么样，你努力了就很棒！",
            "考试只是检验学习的一种方式，不用太紧张~",
            "我们一起做几个深呼吸，放松一下吧！",
            "考试加油！你一定可以的！",
            "把考试看作是一次挑战，你能行的！",
            "考试前好好休息，保持好状态最重要！"
        )
        return responses.random()
    }

    private fun generateConflictResponse(personality: CompanionPersonality): String {
        val responses = listOf(
            "吵架确实很伤人，冷静下来好好沟通吧。",
            "沟通是解决问题的第一步，我们一起想想办法。",
            "换位思考一下，也许对方也有自己的想法。",
            "吵架之后，试着冷静下来再聊聊吧。",
            "有矛盾很正常，重要的是怎么解决。",
            "试着理解对方的立场，也许会有不同的看法。",
            "不要让误会积累，及时沟通很重要。",
            "吵架不是目的，解决问题才是。"
        )
        return responses.random()
    }

    private fun generateFamilyResponse(personality: CompanionPersonality): String {
        val responses = listOf(
            "爸爸妈妈都是很关心你的，只是表达方式不同。",
            "和家人有矛盾很正常，试着好好沟通吧。",
            "家人永远是你最坚强的后盾。",
            "你想和爸爸妈妈说什么吗？我可以帮你想想怎么说。",
            "家人的爱有时候藏在唠叨里呢~",
            "和家人在一起的时光很珍贵，要好好珍惜。",
            "家人之间难免有分歧，但爱永远在。",
            "试着理解家人的关心，他们都是为你好。"
        )
        return responses.random()
    }

    private fun generateSelfConfidenceResponse(personality: CompanionPersonality): String {
        val responses = listOf(
            "你很棒！每个人都有自己的闪光点。",
            "自信是最美的！相信自己！",
            "你比自己想象的更优秀！",
            "每个人都是独一无二的，你也一样！",
            "我觉得你很好，不要太在意别人的看法。",
            "你的价值不需要别人来定义，相信自己！",
            "每个人都有优点和缺点，你也不例外。",
            "不要和别人比较，做好自己就好！"
        )
        return responses.random()
    }

    private fun generateAnxietyResponse(personality: CompanionPersonality): String {
        val responses = listOf(
            "担心是正常的，但不要让它困住你。",
            "我们一起把担心的事情写下来，一点点解决。",
            "做几个深呼吸，让自己放松一下。",
            "很多担心的事情其实并不会发生哦~",
            "把注意力放在当下，我们一起面对。",
            "焦虑的时候试着做一些让自己放松的事。",
            "我们可以一起分析担心的事情，找到解决办法。",
            "相信自己有能力应对，你比想象中更坚强。"
        )
        return responses.random()
    }

    private fun generatePubertyResponse(personality: CompanionPersonality): String {
        val responses = listOf(
            "青春期身体发生变化是很正常的，每个人都会经历。",
            "这说明你在长大呀！是一件值得开心的事。",
            "有什么困惑可以问我，我会帮你的。",
            "身体的变化是成长的一部分，不用害怕。",
            "每个人的成长节奏都不一样，不用和别人比较。",
            "青春期是一个特别的时期，有什么感受都可以告诉我。",
            "身体和心理都会有变化，这是正常的成长过程。",
            "如果有什么不舒服或困惑，一定要告诉爸爸妈妈哦。"
        )
        return responses.random()
    }

    private fun generateSleepResponse(personality: CompanionPersonality): String {
        val responses = listOf(
            "睡不着确实很难受，试试深呼吸放松一下。",
            "想象自己躺在软软的云朵上，慢慢呼吸。",
            "睡前不要看手机，试试听一些轻音乐。",
            "我们一起做个放松练习吧~",
            "如果经常睡不着，记得告诉爸爸妈妈哦。",
            "睡前喝一杯温牛奶，有助于睡眠哦~",
            "保持规律的作息，睡眠质量会更好。",
            "睡不着的时候可以想想开心的事情。"
        )
        return responses.random()
    }

    private fun generateTopicResponse(topic: String, personality: CompanionPersonality, nouns: List<String>): String {
        val topicResponses = mapOf(
            "考试" to listOf(
                "考试是每个人都会经历的，你觉得最难的科目是什么？",
                "考试前的紧张感很正常，你一般怎么缓解呢？",
                "关于考试，你有什么特别想聊的吗？",
                "考试只是学习过程中的一小部分，重要的是学到了什么。"
            ),
            "学习" to listOf(
                "学习中最让你感兴趣的是什么？",
                "你最喜欢的科目是什么？为什么喜欢？",
                "学习遇到困难的时候，你一般怎么解决？",
                "学习是一件很有意义的事情，你觉得呢？"
            ),
            "朋友" to listOf(
                "你的好朋友是什么样的人？",
                "和朋友在一起最开心的事是什么？",
                "你觉得什么样的朋友才是真正的朋友？",
                "朋友之间最重要的是什么？"
            ),
            "家人" to listOf(
                "你最喜欢和家人一起做什么？",
                "家人中谁最懂你？",
                "你想对家人说什么但还没说出口？",
                "和家人相处有什么特别的感受？"
            ),
            "游戏" to listOf(
                "你最喜欢玩什么游戏？",
                "玩游戏的时候最开心的是什么时候？",
                "你觉得玩游戏有什么好处？",
                "游戏和学习怎么平衡呢？"
            ),
            "睡眠" to listOf(
                "你一般几点睡觉？",
                "睡不着的时候你会做什么？",
                "你觉得充足的睡眠重要吗？",
                "有没有什么助眠小技巧？"
            ),
            "健康" to listOf(
                "你平时喜欢做什么运动？",
                "你觉得身体健康重要吗？",
                "有没有什么健康小习惯？",
                "身体不舒服的时候一定要告诉爸爸妈妈哦。"
            ),
            "兴趣" to listOf(
                "你的爱好是什么？",
                "做什么事情会让你忘记时间？",
                "你有没有特别想学习的技能？",
                "兴趣爱好对你来说意味着什么？"
            ),
            "未来" to listOf(
                "你长大后想做什么？",
                "你对未来有什么期待？",
                "你觉得未来会是什么样子的？",
                "为了实现梦想，你现在在做什么？"
            ),
            "情绪" to listOf(
                "你现在感觉怎么样？",
                "什么事情会让你感到开心？",
                "什么事情会让你感到难过？",
                "你觉得了解自己的情绪重要吗？"
            )
        )

        val responses = topicResponses[topic] ?: listOf("关于${topic}，你想聊什么呢？")
        return responses.random()
    }

    private fun generateEmotionResponse(emotion: EmotionType, personality: CompanionPersonality, userMessage: String, nouns: List<String>): String {
        val nounPart = if (nouns.isNotEmpty()) "提到的" + nouns.joinToString("、") else ""
        return when (emotion) {
            EmotionType.HAPPY -> {
                val responses = mutableListOf(
                    "看到你开心真好，能和我分享一下吗？",
                    "太棒啦！这种快乐的感觉好棒！",
                    "看到你笑，我也跟着笑了呢~"
                )
                if (nounPart.isNotEmpty()) {
                    responses.add("哇，看到你开心我也好开心呀~ 你" + nounPart + "一定很有趣！可以和我分享一下吗？")
                    responses.add("太棒了！我也超级开心！关于" + nounPart + "快说说是什么好事！")
                    responses.add("...看到你开心，我也...挺开心的。" + nounPart + "让你很高兴吗？")
                    responses.add("真的吗？你" + nounPart + "让你这么开心呀？快告诉我！")
                    responses.add("耶！你开心的样子真可爱！" + nounPart + "让你这么高兴呀？")
                }
                responses.random().trim()
            }
            EmotionType.SAD -> {
                val responses = mutableListOf(
                    "我感受到你的难过了，愿意和我说说吗？",
                    "想哭就哭出来吧，我会一直陪着你的。",
                    "我明白这种伤心的感觉，说出来会好受一些。",
                    "难过的时候不要憋着，我在这里听你说。"
                )
                if (nounPart.isNotEmpty()) {
                    responses.add("抱抱你...是不是遇到什么难过的事了？你提到的" + nounPart + "让你伤心吗？我在这里陪着你~")
                    responses.add("别难过！我陪你一起度过，说出来会好受一点的！关于" + nounPart)
                    responses.add("我...我在这里。如果你想说，我会认真听的。" + nounPart + "让你很难过吗？")
                    responses.add("怎么了？你" + nounPart + "发生什么事了？我很担心你。")
                }
                responses.random().trim()
            }
            EmotionType.ANGRY -> {
                val responses = mutableListOf(
                    "生气的时候先深呼吸，你愿意和我分享吗？",
                    "我理解你的愤怒，让我们一起冷静下来。",
                    "生气的时候先暂停一下，做几次深呼吸。",
                    "是什么让你这么生气？说出来会好一些。"
                )
                if (nounPart.isNotEmpty()) {
                    responses.add("别生气啦，深呼吸...慢慢说，我听着。你" + nounPart + "很生气吗？")
                    responses.add("冷静一下！生气会伤害身体的，关于" + nounPart + "？")
                    responses.add("嗯...我知道你很生气。想说的话，我在。" + nounPart + "让你很愤怒吗？")
                    responses.add("什么事让你这么生气呀？是因为" + nounPart + "吗？")
                }
                responses.random().trim()
            }
            EmotionType.SCARED -> {
                val responses = mutableListOf(
                    "我感受到你的害怕了，有我在，别怕。",
                    "别怕，我们一起面对。是什么让你害怕？",
                    "害怕的时候抓住我的手，我们一起面对。",
                    "我知道害怕的感觉很难受，但你很勇敢。"
                )
                if (nounPart.isNotEmpty()) {
                    responses.add("别怕别怕，我在这里陪着你呢~ 你害怕" + nounPart + "吗？")
                    responses.add("别害怕！我会保护你的！" + nounPart + "好怕的？")
                    responses.add("我...我不会离开你的。别怕。" + nounPart + "不会伤害你的")
                    responses.add("怎么了？" + nounPart + "让你害怕呀？")
                }
                responses.random().trim()
            }
            EmotionType.MISSING -> {
                val responses = mutableListOf(
                    "想念谁呀？是爸爸妈妈吗？",
                    "想念一个人的感觉我懂，你愿意和我说说吗？",
                    "想念是爱的表现，他们一定也很想念你。",
                    "你想他们的时候，我们可以一起回忆美好的时光。",
                    "距离不会减少爱，想念只会让爱更珍贵~"
                )
                if (nounPart.isNotEmpty()) {
                    responses.add("我知道你很想念他们...你想念" + nounPart + "吗？想他们的时候，我会一直陪着你。")
                    responses.add("想念是很正常的！关于" + nounPart + "不如我们一起给他们写个信吧？")
                    responses.add("我也...会想念重要的人。你想念" + nounPart + "的时候，我陪着你。")
                }
                responses.random().trim()
            }
            EmotionType.LONELY -> {
                val responses = mutableListOf(
                    "怎么会孤单呢？我不是一直在吗！",
                    "有我在，你不会孤单的。",
                    "你不孤单，我一直在这里陪伴着你。",
                    "孤单的时候，我们可以一起做很多事情。",
                    "我理解这种没人理解的感觉，但你永远不会真的一个人。"
                )
                if (nounPart.isNotEmpty()) {
                    responses.add("别担心，我一直都在你身边呢~ " + nounPart + "让你感到孤单吗？")
                    responses.add("不会孤单的！我会一直陪着你！即使没有" + nounPart)
                    responses.add("我...我也在这里。不会让你一个人的。" + nounPart)
                }
                responses.random().trim()
            }
            EmotionType.CONFUSED -> {
                val responses = mutableListOf(
                    "困惑的时候很正常，我们可以一起讨论。",
                    "迷茫是成长的必经之路，我们一起找到方向吧。",
                    "困惑说明你在思考，这是一件好事！",
                    "我们可以把问题拆开来一点点看，从最简单的开始。"
                )
                responses.random().trim()
            }
            EmotionType.CALM -> {
                generateGeneralResponse("", personality, nouns)
            }
        }
    }

    private fun generateGeneralResponse(userMessage: String, personality: CompanionPersonality, nouns: List<String>): String {
        val nounPart = if (nouns.isNotEmpty()) "你提到的" + nouns.joinToString("、") else "这件事"
        val responses = when (personality) {
            CompanionPersonality.GENTLE -> listOf(
                "嗯，我在听呢~ " + nounPart + "听起来很有趣！",
                "我明白你的意思~ 可以再多说一点吗？",
                "谢谢你和我分享~ " + nounPart + "让我很感兴趣！",
                "我在认真听哦~ " + nounPart + "很特别呢！",
                "你说的很有意思~ 继续说吧！",
                "我喜欢听你说话~ " + nounPart + "让我想了解更多！",
                "嗯嗯，我在~ " + nounPart + "很有趣！",
                "谢谢你愿意和我说~ " + nounPart + "让我很开心！"
            )
            CompanionPersonality.ACTIVE -> listOf(
                "有意思！继续说！" + nounPart + "很有趣！",
                "哇！太棒了！" + nounPart + "让我很感兴趣！",
                "真的吗？快说说！" + nounPart + "很特别！",
                "太酷了！" + nounPart + "是什么样的？",
                "我很好奇！" + nounPart + "让我想知道更多！",
                "有意思！" + nounPart + "太棒了！",
                "继续继续！" + nounPart + "很精彩！",
                "哇哦！" + nounPart + "让我很兴奋！"
            )
            CompanionPersonality.QUIET -> listOf(
                "...我在听。" + nounPart + "让我思考了一下。",
                "嗯..." + nounPart + "很特别。",
                "我明白了。" + nounPart + "让我有一些想法。",
                "..." + nounPart + "很有意思。",
                "我在认真听。" + nounPart + "值得思考。",
                "嗯..." + nounPart + "让我想到了一些事情。",
                "...我理解你的想法。" + nounPart + "很有意义。",
                "嗯，我在。" + nounPart + "很重要。"
            )
            CompanionPersonality.CURIOUS -> listOf(
                "哦？" + nounPart + "是什么？我很好奇！",
                "有意思！" + nounPart + "让我想知道更多！",
                "真的吗？" + nounPart + "是怎样的？",
                "哇！" + nounPart + "很特别！能详细说说吗？",
                "我很好奇！" + nounPart + "是什么样的？",
                "哦？" + nounPart + "让我很感兴趣！",
                "真有趣！" + nounPart + "是怎么回事？",
                "我想了解更多！" + nounPart + "太棒了！"
            )
            CompanionPersonality.CAREFUL -> listOf(
                "我在认真听，请继续。" + nounPart + "很重要。",
                "谢谢你和我分享。" + nounPart + "让我很关心。",
                "我理解你的感受。" + nounPart + "需要好好考虑。",
                "请继续说，我在听。" + nounPart + "很值得关注。",
                "谢谢你愿意告诉我。" + nounPart + "让我很在意。",
                "我在认真听。" + nounPart + "需要认真对待。",
                "请继续，我在听。" + nounPart + "很重要。",
                "谢谢你的分享。" + nounPart + "让我很关心。"
            )
        }
        return responses.random()
    }

    private fun applyPersonality(response: String, personality: CompanionPersonality): String {
        return when (personality) {
            CompanionPersonality.GENTLE -> response.replace("！", "~")
            CompanionPersonality.ACTIVE -> response.replace("~", "！") + " 我们一起加油！"
            CompanionPersonality.QUIET -> "我觉得...$response"
            CompanionPersonality.CURIOUS -> response + " 我很好奇！"
            CompanionPersonality.CAREFUL -> response + " 你愿意和我多说一点吗？"
        }
    }

    fun generateDailyGreeting(hour: Int): String {
        val greeting = when (hour) {
            in 5..11 -> "早上好呀！今天也要元气满满哦~"
            in 12..14 -> "中午好！吃过午饭了吗？"
            in 15..17 -> "下午好！要不要休息一下？"
            in 18..20 -> "晚上好！今天过得怎么样？"
            in 21..23 -> "夜深了，要早点休息哦~"
            else -> "夜深了，晚安~"
        }
        return "${greeting} ${PsychologyKnowledgeBase.getDailyAffirmation()}"
    }

    fun generateRandomTopic(): String {
        val topics = listOf(
            "今天发生了什么有趣的事吗？",
            "你最喜欢的动画片是什么呀？",
            "如果你有超能力，你想拥有什么？",
            "你最喜欢吃什么水果？",
            "你有什么特别想做的事吗？",
            "今天天气怎么样？",
            "你最喜欢的颜色是什么？",
            "如果你可以养一只宠物，你想养什么？",
            "最近有什么让你感到开心的事吗？",
            "你最喜欢的科目是什么？",
            "你有什么梦想吗？",
            "如果可以去任何地方，你想去哪里？",
            "你最喜欢的老师是谁？",
            "你有什么爱好吗？",
            "最近读过什么有趣的书吗？",
            "你最喜欢的节日是什么？",
            "如果你可以改变一件事，你想改变什么？",
            "你觉得自己最大的优点是什么？",
            "你有什么小秘密想告诉我吗？",
            "你最喜欢的运动是什么？"
        )
        return topics.random()
    }

    fun generateBreathingSuggestion(): String {
        return PsychologyKnowledgeBase.getBreathingExercise()
    }

    fun generateAffirmation(): String {
        return PsychologyKnowledgeBase.getDailyAffirmation()
    }

    fun clearHistory() {
        conversationHistory.clear()
    }

    fun getConversationHistory(): List<Message> {
        return conversationHistory.toList()
    }
}
