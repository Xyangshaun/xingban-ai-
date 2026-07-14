package com.example.myspatial.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

data class StoryContent(
    val id: String,
    val title: String,
    val category: StoryCategory,
    val ageGroup: String,
    val content: String,
    val lessons: List<String>,
    val duration: String,
    val emotionTags: List<String>
)

enum class StoryCategory {
    EMOTION_MANAGEMENT,
    SELF_ESTEEM,
    FRIENDSHIP,
    FAMILY_LOVE,
    COPING_SKILLS,
    GROWTH_MINDSET,
    BEDTIME_STORY
}

data class LearningContent(
    val id: String,
    val title: String,
    val category: LearningCategory,
    val ageGroup: String,
    val type: ContentType,
    val content: String,
    val exercises: List<Exercise>,
    val tips: List<String>
)

enum class LearningCategory {
    EMOTION_RECOGNITION,
    COMMUNICATION_SKILLS,
    PROBLEM_SOLVING,
    MINDFULNESS,
    STRESS_MANAGEMENT,
    CREATIVE_EXPRESSION
}

enum class ContentType {
    ARTICLE,
    ACTIVITY,
    QUIZ,
    MEDITATION
}

data class Exercise(
    val id: String,
    val title: String,
    val description: String,
    val duration: String
)

data class DailyTip(
    val id: String,
    val day: Int,
    val title: String,
    val content: String,
    val action: String
)

object ContentLibrary {

    private var stories: List<StoryContent> = emptyList()
    private var learningContents: List<LearningContent> = emptyList()
    private var dailyTips: List<DailyTip> = emptyList()

    fun initialize(context: Context) {
        stories = loadStories(context)
        learningContents = loadLearningContents(context)
        dailyTips = loadDailyTips(context)
    }

    private fun loadStories(context: Context): List<StoryContent> {
        return try {
            val inputStream = context.assets.open("stories.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val json = reader.readText()
            reader.close()
            
            val type = object : TypeToken<List<StoryContent>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            getDefaultStories()
        }
    }

    private fun loadLearningContents(context: Context): List<LearningContent> {
        return try {
            val inputStream = context.assets.open("learning_content.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val json = reader.readText()
            reader.close()
            
            val type = object : TypeToken<List<LearningContent>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            getDefaultLearningContents()
        }
    }

    private fun loadDailyTips(context: Context): List<DailyTip> {
        return try {
            val inputStream = context.assets.open("daily_tips.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val json = reader.readText()
            reader.close()
            
            val type = object : TypeToken<List<DailyTip>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            getDefaultDailyTips()
        }
    }

    private fun getDefaultStories(): List<StoryContent> {
        return listOf(
            StoryContent(
                id = "story_001",
                title = "勇敢的小兔子",
                category = StoryCategory.COPING_SKILLS,
                ageGroup = "8-12",
                content = "小兔子朵朵第一次离开妈妈去森林里采蘑菇。她很害怕，怕遇到大灰狼，怕迷路。妈妈告诉她：'害怕的时候，就深呼吸，告诉自己：我能行！'朵朵照着妈妈的话做。当她遇到一条小河时，她深呼吸，找到了过河的石头。当她听到奇怪的声音时，她深呼吸，发现那只是风吹树叶的声音。最后，朵朵采了满满一篮子蘑菇回家了。她明白了：害怕并不可怕，只要勇敢面对，就能克服困难。",
                lessons = listOf("害怕是正常的情绪", "深呼吸可以帮助我们冷静", "勇敢不是不害怕，而是害怕了还能继续前进"),
                duration = "3分钟",
                emotionTags = listOf("害怕", "勇气", "自信")
            ),
            StoryContent(
                id = "story_002",
                title = "星星的愿望",
                category = StoryCategory.FAMILY_LOVE,
                ageGroup = "8-14",
                content = "小星星住在遥远的天空，她每天晚上都会望着地球，想念在远方工作的爸爸妈妈。有一天，她遇到了一颗老星星。老星星告诉她：'你知道吗？爸爸妈妈就像太阳和月亮，虽然有时候不在身边，但他们的爱永远围绕着你。'小星星问：'我怎么能感受到他们的爱呢？'老星星说：'当你想念他们的时候，就对着天空说说话。他们虽然听不到，但你的心意会变成闪亮的星光，他们一定能感受到。'从那以后，小星星每天晚上都会对着地球说话，告诉爸爸妈妈她今天学到了什么，遇到了什么有趣的事情。她不再感到孤单，因为她知道，爱就像星光一样，永远不会消失。",
                lessons = listOf("爱是一种看不见但能感受到的力量", "想念也是一种爱的表达", "即使相隔遥远，爱也能连接我们"),
                duration = "4分钟",
                emotionTags = listOf("想念", "爱", "温暖")
            ),
            StoryContent(
                id = "story_003",
                title = "小刺猬的烦恼",
                category = StoryCategory.FRIENDSHIP,
                ageGroup = "8-12",
                content = "小刺猬波波有一身尖尖的刺，大家都不敢靠近他。波波很孤独，他想交朋友，但是每次他靠近别人，别人都会躲开。波波很难过，他去找猫头鹰博士帮忙。猫头鹰博士说：'你的刺是用来保护自己的，但也可以成为你的优点。'波波不明白。猫头鹰博士继续说：'你可以用你的刺帮大家搬果子呀！'波波试着用背上的刺帮小兔子运苹果，帮小松鼠运松果。大家发现，波波虽然有刺，但他的心很善良。慢慢地，大家都愿意和波波做朋友了。波波明白了：每个人都有自己的特点，只要用心，就能找到和别人相处的方式。",
                lessons = listOf("每个人都有自己的独特之处", "善良的心比外表更重要", "学会用自己的优点帮助别人"),
                duration = "3分钟",
                emotionTags = listOf("孤独", "友谊", "接纳")
            ),
            StoryContent(
                id = "story_004",
                title = "情绪小精灵",
                category = StoryCategory.EMOTION_MANAGEMENT,
                ageGroup = "8-14",
                content = "小宇的心里住着一群情绪小精灵：开心精灵、难过精灵、生气精灵、害怕精灵。有一天，生气精灵突然变得很大很大，因为小宇在学校被同学误会了。生气精灵大声嚷嚷：'他们太过分了！'小宇的脸涨得通红，他想发脾气。这时候，开心精灵轻轻拉住他的手说：'先别急，我们来听听难过精灵怎么说。'难过精灵小声说：'我只是觉得被误会很委屈...'小宇深呼吸，让自己冷静下来。他决定第二天去找同学解释清楚。第二天，误会解开了，生气精灵变小了，开心精灵又变得闪闪发光。小宇明白了：情绪就像天气，会变，但我们可以学会管理它们。",
                lessons = listOf("每个人都有多种情绪", "生气的时候先停下来", "倾听自己内心的声音"),
                duration = "4分钟",
                emotionTags = listOf("生气", "情绪", "理解")
            ),
            StoryContent(
                id = "story_005",
                title = "慢慢长大的小树",
                category = StoryCategory.GROWTH_MINDSET,
                ageGroup = "8-14",
                content = "小树苗阿绿希望自己能快点长大，像森林里的大树一样高大挺拔。但是不管他怎么努力，他还是长得很慢。阿绿很着急。有一天，一场暴风雨来了。阿绿害怕自己会被吹倒。但是大树爷爷告诉他：'别急，小树。每一次风雨都是成长的机会。你的根会在泥土里扎得更深，你的树干会变得更结实。'暴风雨过后，阿绿发现自己真的长高了一点点。他明白了：成长需要时间，每一步都很重要。就像大树不是一天长成的，我们的成长也需要耐心和坚持。",
                lessons = listOf("成长需要时间", "困难是成长的机会", "耐心和坚持很重要"),
                duration = "3分钟",
                emotionTags = listOf("着急", "成长", "耐心")
            ),
            StoryContent(
                id = "story_006",
                title = "月亮婆婆的故事",
                category = StoryCategory.BEDTIME_STORY,
                ageGroup = "8-12",
                content = "夜深了，月亮婆婆轻轻地把银色的月光洒在大地上。小猫咪睡不着，她问月亮婆婆：'月亮婆婆，你每天晚上都不睡觉吗？'月亮婆婆笑了：'我在守护每一个需要陪伴的孩子呀。'小猫咪问：'那你的孩子呢？'月亮婆婆说：'所有睡不着的孩子都是我的孩子。当你想念爸爸妈妈的时候，就看看月亮，我会帮你把思念带给他们。'小猫咪闭上眼睛，想象着爸爸妈妈收到了她的思念。慢慢地，她睡着了。梦里，她和爸爸妈妈一起在月光下散步，温暖又幸福。",
                lessons = listOf("每个人都在被守护", "思念可以传递", "美好的梦境会带来安慰"),
                duration = "4分钟",
                emotionTags = listOf("思念", "安心", "温暖")
            )
        )
    }

    private fun getDefaultLearningContents(): List<LearningContent> {
        return listOf(
            LearningContent(
                id = "learn_001",
                title = "认识你的情绪",
                category = LearningCategory.EMOTION_RECOGNITION,
                ageGroup = "8-12",
                type = ContentType.ARTICLE,
                content = "情绪就像天气一样，有晴天、阴天、雨天。我们每个人都会经历不同的情绪：开心、难过、生气、害怕、平静、困惑...这些情绪没有好坏之分，它们都是我们内心的信号，告诉我们发生了什么。当你感到开心时，说明你遇到了喜欢的事情；当你感到难过时，说明你需要安慰；当你感到生气时，说明你可能受到了伤害。学会认识自己的情绪，是管理情绪的第一步。",
                exercises = listOf(
                    Exercise(
                        id = "ex_001",
                        title = "情绪日记",
                        description = "每天花5分钟记录今天的情绪：今天最强烈的情绪是什么？是什么事情引起的？你是怎么应对的？",
                        duration = "5分钟"
                    ),
                    Exercise(
                        id = "ex_002",
                        title = "情绪脸谱",
                        description = "画一张情绪脸谱，用不同的颜色和表情来表示不同的情绪。",
                        duration = "10分钟"
                    )
                ),
                tips = listOf(
                    "每天记录情绪，你会发现自己的情绪规律",
                    "给情绪起个名字，说出来会好很多",
                    "接纳自己的情绪，不要觉得生气或难过是不好的"
                )
            ),
            LearningContent(
                id = "learn_002",
                title = "深呼吸放松法",
                category = LearningCategory.MINDFULNESS,
                ageGroup = "8-14",
                type = ContentType.MEDITATION,
                content = "深呼吸是最简单也最有效的放松方法。当你感到紧张、害怕或生气的时候，试试深呼吸：用鼻子慢慢吸气，感受肚子像气球一样鼓起来；然后用嘴巴慢慢呼气，感受肚子瘪下去。重复几次，你会感觉平静下来。",
                exercises = listOf(
                    Exercise(
                        id = "ex_003",
                        title = "4-7-8呼吸法",
                        description = "用鼻子吸气4秒，屏住呼吸7秒，用嘴巴呼气8秒。重复3-5次。",
                        duration = "2分钟"
                    ),
                    Exercise(
                        id = "ex_004",
                        title = "气球呼吸",
                        description = "想象自己是一个气球：吸气时慢慢变大，呼气时慢慢变小。",
                        duration = "1分钟"
                    )
                ),
                tips = listOf(
                    "每天练习深呼吸，会让你更容易控制情绪",
                    "深呼吸可以随时随地做，不需要任何工具",
                    "如果感到紧张，停下来做几次深呼吸"
                )
            ),
            LearningContent(
                id = "learn_003",
                title = "如何表达自己的感受",
                category = LearningCategory.COMMUNICATION_SKILLS,
                ageGroup = "8-14",
                type = ContentType.ACTIVITY,
                content = "很多时候，我们不是没有情绪，而是不知道怎么表达。学习用'我'语句来表达感受：'我感到...因为...我希望...'。例如：'我感到难过，因为你没有听我说话。我希望你能认真听我说。'这样的表达方式不会让对方感到被指责，更容易被接受。",
                exercises = listOf(
                    Exercise(
                        id = "ex_005",
                        title = "练习'我'语句",
                        description = "想一想最近让你不舒服的事情，用'我感到...因为...我希望...'的格式表达出来。",
                        duration = "5分钟"
                    ),
                    Exercise(
                        id = "ex_006",
                        title = "情景演练",
                        description = "和你的虚拟伙伴一起练习表达感受的对话。",
                        duration = "10分钟"
                    )
                ),
                tips = listOf(
                    "说话时看着对方的眼睛",
                    "用平和的语气，不要大声喊叫",
                    "表达感受不是抱怨，而是分享"
                )
            ),
            LearningContent(
                id = "learn_004",
                title = "解决问题的步骤",
                category = LearningCategory.PROBLEM_SOLVING,
                ageGroup = "8-14",
                type = ContentType.QUIZ,
                content = "遇到问题时，不要慌张，按照这四个步骤来：1. 冷静下来：先做深呼吸，让自己平静；2. 分析问题：发生了什么？为什么会这样？3. 寻找办法：有哪些解决办法？每个办法的后果是什么？4. 行动：选择最好的办法，开始行动。",
                exercises = listOf(
                    Exercise(
                        id = "ex_007",
                        title = "问题分析",
                        description = "想一想最近遇到的问题，按照四个步骤来分析。",
                        duration = "10分钟"
                    ),
                    Exercise(
                        id = "ex_008",
                        title = "角色扮演",
                        description = "和虚拟伙伴一起模拟解决问题的过程。",
                        duration = "10分钟"
                    )
                ),
                tips = listOf(
                    "遇到问题时，先停下来，不要冲动",
                    "有时候需要寻求别人的帮助",
                    "解决问题比责怪别人更重要"
                )
            ),
            LearningContent(
                id = "learn_005",
                title = "创意表达情绪",
                category = LearningCategory.CREATIVE_EXPRESSION,
                ageGroup = "8-14",
                type = ContentType.ACTIVITY,
                content = "除了说话，我们还可以用很多方式表达情绪：画画、写日记、唱歌、跳舞、做手工...选择你喜欢的方式，把情绪表达出来。例如：难过的时候可以画一幅蓝色的画，开心的时候可以唱一首快乐的歌。",
                exercises = listOf(
                    Exercise(
                        id = "ex_009",
                        title = "情绪涂鸦",
                        description = "用颜色和线条来表达你现在的情绪，不需要画得好看，只要表达出来就行。",
                        duration = "10分钟"
                    ),
                    Exercise(
                        id = "ex_010",
                        title = "情绪歌曲",
                        description = "选一首能表达你情绪的歌，跟着唱。或者自己编一首简单的歌。",
                        duration = "5分钟"
                    )
                ),
                tips = listOf(
                    "创意表达没有对错，只要你喜欢",
                    "有时候画画比说话更容易表达感受",
                    "收藏你的作品，以后可以回顾"
                )
            )
        )
    }

    private fun getDefaultDailyTips(): List<DailyTip> {
        return listOf(
            DailyTip(
                id = "tip_001",
                day = 1,
                title = "认识自己",
                content = "今天试着留意自己的情绪。当你感到开心、难过或生气时，停下来想一想：'我现在是什么感觉？为什么会有这种感觉？'",
                action = "记录今天最强烈的三种情绪"
            ),
            DailyTip(
                id = "tip_002",
                day = 2,
                title = "呼吸练习",
                content = "每天练习3分钟深呼吸。用鼻子吸气4秒，屏住呼吸7秒，用嘴巴呼气8秒。这会帮助你保持平静。",
                action = "至少做3次深呼吸练习"
            ),
            DailyTip(
                id = "tip_003",
                day = 3,
                title = "表达感谢",
                content = "今天想一想，有什么人或事让你感到开心或感激？试着说出来或写下来。",
                action = "说出或写下3件让你感激的事情"
            ),
            DailyTip(
                id = "tip_004",
                day = 4,
                title = "积极的自我对话",
                content = "当你遇到困难时，不要说'我做不到'，试着说'我可以试试'或'我需要帮助'。",
                action = "今天遇到困难时，用积极的方式鼓励自己"
            ),
            DailyTip(
                id = "tip_005",
                day = 5,
                title = "与他人连接",
                content = "今天试着和一个人好好说话，认真听对方说什么，也分享你自己的想法。",
                action = "与一个人进行一次真诚的对话"
            ),
            DailyTip(
                id = "tip_006",
                day = 6,
                title = "自我关爱",
                content = "今天做一件让自己开心的事情：看一部喜欢的电影、吃喜欢的食物、或者只是安静地待一会儿。",
                action = "做一件让自己开心的事情"
            ),
            DailyTip(
                id = "tip_007",
                day = 7,
                title = "回顾一周",
                content = "回顾这一周，你学到了什么？有什么进步？不需要完美，只要比上周更好就行。",
                action = "写下本周的三个进步"
            )
        )
    }

    fun getStories(): List<StoryContent> = stories
    fun getStoriesByCategory(category: StoryCategory): List<StoryContent> = 
        stories.filter { it.category == category }
    fun getStoriesByEmotion(emotionTag: String): List<StoryContent> =
        stories.filter { it.emotionTags.contains(emotionTag) }
    fun getRandomStory(): StoryContent = stories.random()

    fun getLearningContents(): List<LearningContent> = learningContents
    fun getLearningContentsByCategory(category: LearningCategory): List<LearningContent> =
        learningContents.filter { it.category == category }
    fun getRandomLearningContent(): LearningContent = learningContents.random()

    fun getDailyTips(): List<DailyTip> = dailyTips
    fun getTipForDay(day: Int): DailyTip? = dailyTips.find { it.day == day % dailyTips.size }
    fun getRandomTip(): DailyTip = dailyTips.random()
}
