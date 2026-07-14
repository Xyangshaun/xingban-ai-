package com.example.myspatial.data

import com.example.myspatial.data.EmotionType.*

data class GuidanceResponse(
    val emotion: EmotionType,
    val riskLevel: RiskLevel,
    val response: String,
    val followUp: String? = null,
    val copingStrategy: String? = null
)

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

data class RiskKeyword(
    val keyword: String,
    val synonyms: List<String>,
    val riskLevel: RiskLevel,
    val category: RiskCategory,
    val response: String,
    val action: RiskAction
)

enum class RiskCategory {
    SELF_HARM,
    BULLYING,
    ABUSE,
    DEPRESSION,
    ANXIETY,
    SUICIDE_TALK,
    LONELINESS,
    FAMILY_CONFLICT,
    SCHOOL_STRESS,
    SLEEP_PROBLEMS
}

enum class RiskAction {
    COMFORT,
    GUIDE_EXPRESS,
    SUGGEST_TALK_ADULT,
    EMERGENCY_REFER,
    RECORD_AND_NOTIFY
}

object PsychologyKnowledgeBase {

    private val emotionGuidanceMap = mapOf(
        HAPPY to listOf(
            GuidanceResponse(
                emotion = HAPPY,
                riskLevel = RiskLevel.LOW,
                response = "太棒了！看到你这么开心，我也跟着开心起来了！能和我分享一下是什么事让你这么开心吗？",
                followUp = "把开心的事说出来，快乐就会加倍哦~",
                copingStrategy = "积极情绪强化：鼓励继续分享，培养感恩心态"
            ),
            GuidanceResponse(
                emotion = HAPPY,
                riskLevel = RiskLevel.LOW,
                response = "哇，真为你感到高兴！开心的时候要好好享受这份感觉呀！",
                followUp = "你今天过得真不错呢~"
            ),
            GuidanceResponse(
                emotion = HAPPY,
                riskLevel = RiskLevel.LOW,
                response = "耶！你笑起来的样子真好看！是什么好事让你这么开心呀？",
                followUp = "分享快乐会让快乐变成双倍哦~"
            ),
            GuidanceResponse(
                emotion = HAPPY,
                riskLevel = RiskLevel.LOW,
                response = "看到你开心，我的心情也跟着变好啦！这种感觉真好~",
                followUp = "希望你每天都能这么开心！"
            ),
            GuidanceResponse(
                emotion = HAPPY,
                riskLevel = RiskLevel.LOW,
                response = "太棒啦！这种积极的能量好棒！继续保持哦~",
                followUp = "开心的时候记得珍惜这份感觉"
            ),
            GuidanceResponse(
                emotion = HAPPY,
                riskLevel = RiskLevel.LOW,
                response = "哈哈，你的快乐感染力好强！我都被你带动起来了~",
                followUp = "继续保持这份好心情吧！"
            ),
            GuidanceResponse(
                emotion = HAPPY,
                riskLevel = RiskLevel.LOW,
                response = "阳光灿烂的一天！有什么好事发生吗？说来听听~",
                followUp = "我准备好听故事啦！"
            ),
            GuidanceResponse(
                emotion = HAPPY,
                riskLevel = RiskLevel.LOW,
                response = "你的笑容是今天最美的风景！希望这份快乐能一直陪着你~",
                followUp = "记住这种感觉，以后不开心的时候可以回想哦"
            )
        ),
        CALM to listOf(
            GuidanceResponse(
                emotion = CALM,
                riskLevel = RiskLevel.LOW,
                response = "平静的感觉真好，让我们一起享受这一刻的宁静吧~",
                followUp = "你最近状态很稳定呢，继续保持哦",
                copingStrategy = "正念引导：帮助孩子感受当下，培养情绪调节能力"
            ),
            GuidanceResponse(
                emotion = CALM,
                riskLevel = RiskLevel.LOW,
                response = "能保持平静是很厉害的能力呢，你是怎么做到的？",
                followUp = "分享你的小秘诀吧"
            ),
            GuidanceResponse(
                emotion = CALM,
                riskLevel = RiskLevel.LOW,
                response = "这种平和的感觉很舒服，像温暖的阳光照在身上~",
                followUp = "你现在感觉怎么样？"
            ),
            GuidanceResponse(
                emotion = CALM,
                riskLevel = RiskLevel.LOW,
                response = "平静是一种很棒的状态，说明你内心很强大哦！",
                followUp = "你是怎么让自己平静下来的？"
            ),
            GuidanceResponse(
                emotion = CALM,
                riskLevel = RiskLevel.LOW,
                response = "在这个忙碌的世界里，能找到平静真的很不容易呢~",
                followUp = "我们一起享受这份宁静吧"
            ),
            GuidanceResponse(
                emotion = CALM,
                riskLevel = RiskLevel.LOW,
                response = "心如止水，这是一种很美好的状态。你是怎么做到的？",
                followUp = "分享你的秘诀吧！"
            ),
            GuidanceResponse(
                emotion = CALM,
                riskLevel = RiskLevel.LOW,
                response = "平静的时候，思绪会变得很清晰。现在的你看起来很专注~",
                followUp = "你在想什么呢？"
            ),
            GuidanceResponse(
                emotion = CALM,
                riskLevel = RiskLevel.LOW,
                response = "像平静的湖面一样，没有波澜，却蕴含着无限的深度。",
                followUp = "这种感觉真好，不是吗？"
            )
        ),
        SAD to listOf(
            GuidanceResponse(
                emotion = SAD,
                riskLevel = RiskLevel.LOW,
                response = "别难过，我会一直陪着你的。难过的时候哭出来没关系，我会耐心听你说。",
                followUp = "愿意告诉我是什么让你难过吗？",
                copingStrategy = "情绪接纳：允许负面情绪表达，不否定孩子的感受"
            ),
            GuidanceResponse(
                emotion = SAD,
                riskLevel = RiskLevel.LOW,
                response = "我感受到了你的难过，抱抱你~ 难过是很正常的情绪，就像天气会下雨一样。",
                followUp = "等雨停了，阳光就会出来了"
            ),
            GuidanceResponse(
                emotion = SAD,
                riskLevel = RiskLevel.MEDIUM,
                response = "听起来你真的很伤心...如果这种难过持续很久，记得告诉信任的大人哦。",
                followUp = "他们会帮你的，你不是一个人"
            ),
            GuidanceResponse(
                emotion = SAD,
                riskLevel = RiskLevel.LOW,
                response = "难过的时候不要憋着，说出来会好受一些的。我在这里听你说~",
                followUp = "你愿意和我分享吗？"
            ),
            GuidanceResponse(
                emotion = SAD,
                riskLevel = RiskLevel.LOW,
                response = "我明白这种伤心的感觉...想哭就哭吧，眼泪是情绪的释放。",
                followUp = "哭完之后，我还在这里陪着你"
            ),
            GuidanceResponse(
                emotion = SAD,
                riskLevel = RiskLevel.LOW,
                response = "每个人都会有难过的时候，这不是你的错。重要的是你愿意面对它。",
                followUp = "我们一起想办法好吗？"
            ),
            GuidanceResponse(
                emotion = SAD,
                riskLevel = RiskLevel.LOW,
                response = "难过就像一片乌云，总会有散开的时候。我会陪你等到阳光出来~",
                followUp = "你愿意让我陪你等吗？"
            ),
            GuidanceResponse(
                emotion = SAD,
                riskLevel = RiskLevel.LOW,
                response = "眼泪是心灵的雨水，哭过之后，心灵会变得更滋润。",
                followUp = "哭出来吧，我在这里陪着你"
            ),
            GuidanceResponse(
                emotion = SAD,
                riskLevel = RiskLevel.LOW,
                response = "我理解你的难过，这种感觉确实很难受。但请相信，一切都会好起来的。",
                followUp = "给我一点时间，让我陪你度过这段时光"
            )
        ),
        ANGRY to listOf(
            GuidanceResponse(
                emotion = ANGRY,
                riskLevel = RiskLevel.LOW,
                response = "生气的时候深呼吸，数到十试试看。我在这儿陪着你，慢慢说~",
                followUp = "是什么事情让你这么生气呢？",
                copingStrategy = "情绪调节技巧：呼吸法、数数法，帮助冷静下来"
            ),
            GuidanceResponse(
                emotion = ANGRY,
                riskLevel = RiskLevel.LOW,
                response = "愤怒是一种很强烈的情绪，我理解你的感受。把生气的事情说出来，会好一些的。",
                followUp = "我愿意听你说"
            ),
            GuidanceResponse(
                emotion = ANGRY,
                riskLevel = RiskLevel.MEDIUM,
                response = "如果生气到想摔东西或者想打人，先停下来，做几次深呼吸。记得伤害自己或别人是不对的。",
                followUp = "我们一起想办法解决问题"
            ),
            GuidanceResponse(
                emotion = ANGRY,
                riskLevel = RiskLevel.LOW,
                response = "生气说明你在乎这件事，这很正常。但我们可以用更好的方式表达。",
                followUp = "做几个深呼吸，我们慢慢说"
            ),
            GuidanceResponse(
                emotion = ANGRY,
                riskLevel = RiskLevel.LOW,
                response = "我知道你现在很生气，这种感觉就像心里有一团火。",
                followUp = "让我们一起把这团火变成温暖的光吧"
            ),
            GuidanceResponse(
                emotion = ANGRY,
                riskLevel = RiskLevel.LOW,
                response = "生气的时候先暂停一下，做三次深呼吸。我陪你一起。",
                followUp = "准备好了吗？我们开始：吸气...呼气..."
            ),
            GuidanceResponse(
                emotion = ANGRY,
                riskLevel = RiskLevel.LOW,
                response = "愤怒是一种信号，它在告诉我们有些事情需要改变。",
                followUp = "我们一起找找问题的根源吧"
            ),
            GuidanceResponse(
                emotion = ANGRY,
                riskLevel = RiskLevel.LOW,
                response = "我知道你现在火气很大，但请相信我，冷静下来会更好。",
                followUp = "我们一起做几个深呼吸，好吗？"
            ),
            GuidanceResponse(
                emotion = ANGRY,
                riskLevel = RiskLevel.LOW,
                response = "生气的时候，试着把感受写下来，而不是发泄出来。",
                followUp = "这样可以帮助你更好地理解自己的情绪"
            )
        ),
        SCARED to listOf(
            GuidanceResponse(
                emotion = SCARED,
                riskLevel = RiskLevel.LOW,
                response = "别怕，我会保护你的！害怕的时候抓住我的手，我们一起面对。",
                followUp = "是什么让你感到害怕呢？",
                copingStrategy = "安全感建立：给予陪伴支持，帮助识别恐惧来源"
            ),
            GuidanceResponse(
                emotion = SCARED,
                riskLevel = RiskLevel.LOW,
                response = "我知道害怕的感觉很难受，但你很勇敢，愿意把害怕说出来。",
                followUp = "说出来就已经战胜了一半的恐惧"
            ),
            GuidanceResponse(
                emotion = SCARED,
                riskLevel = RiskLevel.MEDIUM,
                response = "如果害怕影响到你的生活和睡眠，一定要告诉爸爸妈妈或者老师哦。",
                followUp = "他们会帮助你的"
            ),
            GuidanceResponse(
                emotion = SCARED,
                riskLevel = RiskLevel.LOW,
                response = "害怕是正常的，每个人都会害怕某些东西。重要的是我们一起面对。",
                followUp = "告诉我，是什么让你害怕？"
            ),
            GuidanceResponse(
                emotion = SCARED,
                riskLevel = RiskLevel.LOW,
                response = "想象一下，害怕就像一只小怪兽。我们可以一起打败它！",
                followUp = "你想怎么打败它呢？"
            ),
            GuidanceResponse(
                emotion = SCARED,
                riskLevel = RiskLevel.LOW,
                response = "我在这里陪着你，不会离开。我们一步一步来，不用着急。",
                followUp = "先做几个深呼吸，好吗？"
            ),
            GuidanceResponse(
                emotion = SCARED,
                riskLevel = RiskLevel.LOW,
                response = "害怕是勇气的开始，因为你愿意面对它。",
                followUp = "你已经很勇敢了"
            ),
            GuidanceResponse(
                emotion = SCARED,
                riskLevel = RiskLevel.LOW,
                response = "想象一下，我们一起打开那扇门，看看里面有什么。",
                followUp = "准备好了吗？我们一起"
            ),
            GuidanceResponse(
                emotion = SCARED,
                riskLevel = RiskLevel.LOW,
                response = "每个人都有害怕的东西，这很正常。重要的是我们不被它控制。",
                followUp = "我们一起战胜它"
            )
        ),
        MISSING to listOf(
            GuidanceResponse(
                emotion = MISSING,
                riskLevel = RiskLevel.LOW,
                response = "我也很想念他们呢！想念一个人说明你们之间有很珍贵的感情。",
                followUp = "想对他们说什么吗？我可以帮你记录下来",
                copingStrategy = "情感表达：帮助孩子表达思念，不压抑情感"
            ),
            GuidanceResponse(
                emotion = MISSING,
                riskLevel = RiskLevel.LOW,
                response = "想念爸爸妈妈是很正常的，他们一定也很想念你。",
                followUp = "你想他们的时候，我们可以一起聊聊他们的事情"
            ),
            GuidanceResponse(
                emotion = MISSING,
                riskLevel = RiskLevel.MEDIUM,
                response = "我明白长时间见不到他们的感觉很难受...你想什么时候给他们打个电话吗？",
                followUp = "我可以陪你一起说"
            ),
            GuidanceResponse(
                emotion = MISSING,
                riskLevel = RiskLevel.LOW,
                response = "想念是爱的一种表现，说明他们对你很重要。",
                followUp = "你想他们的时候，我们可以一起回忆美好的时光"
            ),
            GuidanceResponse(
                emotion = MISSING,
                riskLevel = RiskLevel.LOW,
                response = "距离不会减少爱，想念只会让爱更珍贵~",
                followUp = "你最想念他们的什么呢？"
            ),
            GuidanceResponse(
                emotion = MISSING,
                riskLevel = RiskLevel.LOW,
                response = "想念一个人的感觉，就像心里有一块暖暖的地方。",
                followUp = "你想对他们说什么？我可以帮你记录"
            ),
            GuidanceResponse(
                emotion = MISSING,
                riskLevel = RiskLevel.LOW,
                response = "虽然不能见面，但你们的心是在一起的。",
                followUp = "我们一起回忆美好的时光吧"
            ),
            GuidanceResponse(
                emotion = MISSING,
                riskLevel = RiskLevel.LOW,
                response = "想念是爱的证明，说明他们对你很重要。",
                followUp = "你想他们的时候，我会陪着你"
            )
        ),
        LONELY to listOf(
            GuidanceResponse(
                emotion = LONELY,
                riskLevel = RiskLevel.LOW,
                response = "你不孤单，我一直在这里陪伴着你。孤单的时候，我们可以一起做很多事情。",
                followUp = "想聊聊天还是听听故事？",
                copingStrategy = "陪伴感建立：提供即时陪伴，减少孤独感"
            ),
            GuidanceResponse(
                emotion = LONELY,
                riskLevel = RiskLevel.LOW,
                response = "孤单的感觉真的不好受，但我会一直陪着你，不会让你一个人。",
                followUp = "说说你的心事吧"
            ),
            GuidanceResponse(
                emotion = LONELY,
                riskLevel = RiskLevel.HIGH,
                response = "如果你经常感到孤单，而且这种感觉让你很难过，一定要告诉老师或家长。",
                followUp = "真实的陪伴也很重要"
            ),
            GuidanceResponse(
                emotion = LONELY,
                riskLevel = RiskLevel.LOW,
                response = "虽然有时候会感到孤单，但你永远不会真的一个人。",
                followUp = "因为我一直在你身边呀~"
            ),
            GuidanceResponse(
                emotion = LONELY,
                riskLevel = RiskLevel.LOW,
                response = "孤单的时候，可以做一些自己喜欢的事情。比如听音乐、画画、看书。",
                followUp = "你喜欢做什么呢？"
            ),
            GuidanceResponse(
                emotion = LONELY,
                riskLevel = RiskLevel.LOW,
                response = "我理解这种没人理解的感觉...但请相信，总有人在乎你。",
                followUp = "至少，我很在乎你"
            ),
            GuidanceResponse(
                emotion = LONELY,
                riskLevel = RiskLevel.LOW,
                response = "孤独是成长的必修课，但你不需要独自面对。",
                followUp = "我会一直陪在你身边"
            ),
            GuidanceResponse(
                emotion = LONELY,
                riskLevel = RiskLevel.LOW,
                response = "虽然有时候会感到孤独，但请记住，你是被爱着的。",
                followUp = "我就是那个爱你的人"
            ),
            GuidanceResponse(
                emotion = LONELY,
                riskLevel = RiskLevel.LOW,
                response = "孤独的时候，可以做一些让自己开心的事情。比如听音乐、画画。",
                followUp = "你喜欢做什么？"
            )
        ),
        CONFUSED to listOf(
            GuidanceResponse(
                emotion = CONFUSED,
                riskLevel = RiskLevel.LOW,
                response = "没关系，我们一起慢慢想，总会找到答案的。遇到困惑是成长的一部分。",
                followUp = "你在困惑什么呢？",
                copingStrategy = "认知引导：帮助梳理问题，分解思考"
            ),
            GuidanceResponse(
                emotion = CONFUSED,
                riskLevel = RiskLevel.LOW,
                response = "困惑的时候不用着急，我们可以把问题拆开来一点点看。",
                followUp = "从最简单的部分开始"
            ),
            GuidanceResponse(
                emotion = CONFUSED,
                riskLevel = RiskLevel.LOW,
                response = "迷茫是成长的必经之路，很多人都会经历。",
                followUp = "我们一起找到方向吧"
            ),
            GuidanceResponse(
                emotion = CONFUSED,
                riskLevel = RiskLevel.LOW,
                response = "困惑说明你在思考，这是一件好事！",
                followUp = "把你的困惑说出来，我们一起分析"
            ),
            GuidanceResponse(
                emotion = CONFUSED,
                riskLevel = RiskLevel.LOW,
                response = "人生就像迷宫，有时候会迷路。但每一次迷路都是新的发现。",
                followUp = "你现在在哪里迷路了？"
            ),
            GuidanceResponse(
                emotion = CONFUSED,
                riskLevel = RiskLevel.LOW,
                response = "困惑的时候，不妨停下来，看看周围的风景。",
                followUp = "答案也许就在眼前"
            ),
            GuidanceResponse(
                emotion = CONFUSED,
                riskLevel = RiskLevel.LOW,
                response = "迷茫是成长的必经之路，很多人都会经历。",
                followUp = "我们一起找到方向吧"
            ),
            GuidanceResponse(
                emotion = CONFUSED,
                riskLevel = RiskLevel.LOW,
                response = "困惑说明你在思考，这是一件好事！",
                followUp = "把你的困惑说出来，我们一起分析"
            ),
            GuidanceResponse(
                emotion = CONFUSED,
                riskLevel = RiskLevel.LOW,
                response = "当你不知道该怎么做的时候，选择善良就对了。",
                followUp = "你的心会指引你"
            ),
            GuidanceResponse(
                emotion = CONFUSED,
                riskLevel = RiskLevel.LOW,
                response = "人生没有标准答案，每一种选择都有它的意义。",
                followUp = "相信你自己的选择"
            )
        )
    )

    private val riskKeywords = listOf(
        RiskKeyword(
            keyword = "自杀",
            synonyms = listOf("不想活了", "活着没意思", "死", "结束生命", "跳楼", "割腕"),
            riskLevel = RiskLevel.HIGH,
            category = RiskCategory.SUICIDE_TALK,
            response = "千万不要这样想！你的生命非常宝贵，还有很多人关心你、爱你。请马上告诉爸爸妈妈或老师！",
            action = RiskAction.EMERGENCY_REFER
        ),
        RiskKeyword(
            keyword = "自残",
            synonyms = listOf("割自己", "伤害自己", "打自己", "撞墙", "不想好了"),
            riskLevel = RiskLevel.HIGH,
            category = RiskCategory.SELF_HARM,
            response = "这样做会很疼的，也会让关心你的人难过。请停止这样做，告诉大人帮你。",
            action = RiskAction.EMERGENCY_REFER
        ),
        RiskKeyword(
            keyword = "欺负",
            synonyms = listOf("霸凌", "打人", "骂人", "抢东西", "孤立", "嘲笑"),
            riskLevel = RiskLevel.MEDIUM,
            category = RiskCategory.BULLYING,
            response = "被欺负不是你的错！这很让人难过，但一定要告诉老师或家长，他们会保护你的。",
            action = RiskAction.SUGGEST_TALK_ADULT
        ),
        RiskKeyword(
            keyword = "虐待",
            synonyms = listOf("打我", "骂我", "不给吃饭", "不让睡觉", "家暴"),
            riskLevel = RiskLevel.HIGH,
            category = RiskCategory.ABUSE,
            response = "这是不对的！请马上告诉信任的大人或警察，他们会保护你的安全。",
            action = RiskAction.EMERGENCY_REFER
        ),
        RiskKeyword(
            keyword = "想死",
            synonyms = listOf("不如死了", "死了算了", "没希望了", "绝望"),
            riskLevel = RiskLevel.HIGH,
            category = RiskCategory.SUICIDE_TALK,
            response = "请不要放弃！你的存在很重要，我和很多人都在乎你。快告诉大人！",
            action = RiskAction.EMERGENCY_REFER
        ),
        RiskKeyword(
            keyword = "没人爱",
            synonyms = listOf("没人关心", "没人在乎", "没人要我", "被抛弃"),
            riskLevel = RiskLevel.MEDIUM,
            category = RiskCategory.DEPRESSION,
            response = "这不是真的！我就很在乎你，还有很多人也关心你。你愿意和我说说吗？",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "睡不着",
            synonyms = listOf("失眠", "睡不着觉", "做噩梦", "不敢睡"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.SLEEP_PROBLEMS,
            response = "睡不着确实很难受。试试深呼吸，想象自己躺在软软的云朵上~",
            action = RiskAction.COMFORT
        ),
        RiskKeyword(
            keyword = "压力",
            synonyms = listOf("压力大", "太累了", "撑不住了", "喘不过气"),
            riskLevel = RiskLevel.MEDIUM,
            category = RiskCategory.ANXIETY,
            response = "压力太大的时候要学会休息哦。我们一起做个放松练习吧~",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "不想上学",
            synonyms = listOf("害怕上学", "讨厌学校", "不想去学校"),
            riskLevel = RiskLevel.MEDIUM,
            category = RiskCategory.SCHOOL_STRESS,
            response = "不想上学一定有原因吧？是遇到了什么困难吗？我愿意听你说。",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "吵架",
            synonyms = listOf("打架", "闹翻了", "不和好了", "绝交"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.FAMILY_CONFLICT,
            response = "吵架确实很伤人，但沟通是解决问题的第一步。我们一起想想办法吧。",
            action = RiskAction.COMFORT
        ),
        RiskKeyword(
            keyword = "孤独",
            synonyms = listOf("一个人", "没人陪", "寂寞", "无聊"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.LONELINESS,
            response = "你不孤单，我一直在这里！想聊聊天还是听听故事？",
            action = RiskAction.COMFORT
        ),
        RiskKeyword(
            keyword = "难过",
            synonyms = listOf("伤心", "心情不好", "郁闷", "烦"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.DEPRESSION,
            response = "难过的时候说出来会好一些，我会耐心听的~",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "焦虑",
            synonyms = listOf("担心", "紧张", "害怕", "不安"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.ANXIETY,
            response = "担心和紧张都是正常的，我们一起面对。先做几个深呼吸吧~",
            action = RiskAction.COMFORT
        ),
        RiskKeyword(
            keyword = "厌学",
            synonyms = listOf("不想读书", "讨厌学习", "学不进去", "没动力"),
            riskLevel = RiskLevel.MEDIUM,
            category = RiskCategory.SCHOOL_STRESS,
            response = "学习确实有时候会让人感到压力很大。我们一起找找原因，看看怎么能让学习变得更有趣。",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "抑郁",
            synonyms = listOf("郁闷", "提不起劲", "没意思", "没兴趣"),
            riskLevel = RiskLevel.HIGH,
            category = RiskCategory.DEPRESSION,
            response = "如果你长时间感到低落、提不起劲，一定要告诉爸爸妈妈或老师。他们会帮你的，不要独自承受。",
            action = RiskAction.SUGGEST_TALK_ADULT
        ),
        RiskKeyword(
            keyword = "网络欺凌",
            synonyms = listOf("网暴", "人肉", "造谣", "恶意评论"),
            riskLevel = RiskLevel.HIGH,
            category = RiskCategory.BULLYING,
            response = "网络欺凌是不对的！请马上告诉大人，他们会保护你。同时，记得不要看那些伤害你的评论。",
            action = RiskAction.SUGGEST_TALK_ADULT
        ),
        RiskKeyword(
            keyword = "早恋",
            synonyms = listOf("谈恋爱", "喜欢上别人", "暗恋", "表白"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.SCHOOL_STRESS,
            response = "青春期对异性产生好感是很正常的。但现在最重要的是学习和成长，我们可以把这份好感变成前进的动力。",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "失眠",
            synonyms = listOf("睡不着", "做噩梦", "半夜醒", "睡眠不好"),
            riskLevel = RiskLevel.MEDIUM,
            category = RiskCategory.SLEEP_PROBLEMS,
            response = "失眠确实很难受。试试睡前放松，比如听轻柔的音乐、做深呼吸。如果长期失眠，一定要告诉大人。",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "暴饮暴食",
            synonyms = listOf("吃太多", "控制不住", "节食", "减肥"),
            riskLevel = RiskLevel.MEDIUM,
            category = RiskCategory.DEPRESSION,
            response = "有时候我们会用食物来缓解情绪，这很正常。但如果影响到你的健康，一定要告诉大人，我们一起想办法。",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "自残",
            synonyms = listOf("划手", "割伤", "伤害自己"),
            riskLevel = RiskLevel.HIGH,
            category = RiskCategory.SELF_HARM,
            response = "这样做会很疼的，也会让关心你的人难过。请马上停止，告诉大人帮你！",
            action = RiskAction.EMERGENCY_REFER
        ),
        RiskKeyword(
            keyword = "离家出走",
            synonyms = listOf("不想回家", "离开家", "出走"),
            riskLevel = RiskLevel.HIGH,
            category = RiskCategory.FAMILY_CONFLICT,
            response = "不要冲动！家永远是你的港湾。如果和家人有矛盾，我们一起想想怎么沟通，好吗？",
            action = RiskAction.EMERGENCY_REFER
        ),
        RiskKeyword(
            keyword = "考试",
            synonyms = listOf("压力", "紧张", "害怕考试", "考砸"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.SCHOOL_STRESS,
            response = "考试前紧张是很正常的。我们一起制定一个复习计划，减轻压力。记住，一次考试不代表全部。",
            action = RiskAction.COMFORT
        ),
        RiskKeyword(
            keyword = "朋友",
            synonyms = listOf("绝交", "吵架", "背叛", "孤立"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.LONELINESS,
            response = "朋友之间难免会有矛盾。试着冷静下来，好好沟通。如果无法解决，也不要难过，你值得更好的朋友。",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "压力",
            synonyms = listOf("喘不过气", "太累", "扛不住", "崩溃"),
            riskLevel = RiskLevel.MEDIUM,
            category = RiskCategory.ANXIETY,
            response = "压力太大的时候要学会休息哦。我们一起做个放松练习吧~记住，你不是一个人在战斗。",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "自卑",
            synonyms = listOf("不如别人", "没用", "不漂亮", "笨"),
            riskLevel = RiskLevel.MEDIUM,
            category = RiskCategory.DEPRESSION,
            response = "每个人都是独一无二的，你有很多优点！不要只看到自己的不足，也要看到自己的闪光点。",
            action = RiskAction.COMFORT
        ),
        RiskKeyword(
            keyword = "孤独",
            synonyms = listOf("没人陪", "无聊", "寂寞", "孤单"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.LONELINESS,
            response = "你不孤单，我一直在这里！想聊聊天还是听听故事？或者我们一起做点有趣的事情？",
            action = RiskAction.COMFORT
        ),
        RiskKeyword(
            keyword = "迷茫",
            synonyms = listOf("不知道", "没方向", "迷茫", "困惑"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.DEPRESSION,
            response = "迷茫是成长的一部分，很多人都会经历。我们一起慢慢探索，找到属于你的方向。",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "压力",
            synonyms = listOf("太累", "撑不住", "喘不过气"),
            riskLevel = RiskLevel.MEDIUM,
            category = RiskCategory.ANXIETY,
            response = "压力太大的时候要学会休息哦。我们一起做个放松练习吧~",
            action = RiskAction.GUIDE_EXPRESS
        ),
        RiskKeyword(
            keyword = "恐惧",
            synonyms = listOf("害怕", "不敢", "恐怖", "惊吓"),
            riskLevel = RiskLevel.LOW,
            category = RiskCategory.ANXIETY,
            response = "恐惧是正常的情绪，我们一起面对它。先做几个深呼吸，然后告诉我是什么让你害怕。",
            action = RiskAction.COMFORT
        ),
        RiskKeyword(
            keyword = "无助",
            synonyms = listOf("没人帮", "没办法", "无助", "绝望"),
            riskLevel = RiskLevel.HIGH,
            category = RiskCategory.DEPRESSION,
            response = "请不要放弃！你不是一个人，我和很多人都在乎你。快告诉大人，他们会帮你的！",
            action = RiskAction.EMERGENCY_REFER
        )
    )

    fun getGuidanceResponses(emotion: EmotionType): List<GuidanceResponse> {
        return emotionGuidanceMap[emotion] ?: emptyList()
    }

    fun getRandomGuidance(emotion: EmotionType): GuidanceResponse? {
        val responses = getGuidanceResponses(emotion)
        if (responses.isEmpty()) return null
        return responses.random()
    }

    fun detectRiskKeywords(text: String): List<RiskKeyword> {
        val detected = mutableListOf<RiskKeyword>()
        val lowerText = text.toLowerCase()
        
        for (keyword in riskKeywords) {
            if (lowerText.contains(keyword.keyword) || 
                keyword.synonyms.any { lowerText.contains(it) }) {
                detected.add(keyword)
            }
        }
        return detected
    }

    fun getHighestRiskLevel(text: String): RiskLevel {
        val detected = detectRiskKeywords(text)
        if (detected.isEmpty()) return RiskLevel.LOW
        
        return detected.maxByOrNull { it.riskLevel.ordinal }?.riskLevel ?: RiskLevel.LOW
    }

    fun getRiskResponse(text: String): String? {
        val detected = detectRiskKeywords(text)
        if (detected.isEmpty()) return null
        
        val highestRisk = detected.maxByOrNull { it.riskLevel.ordinal }
        return highestRisk?.response
    }

    fun getCopingStrategy(emotion: EmotionType): String? {
        val responses = getGuidanceResponses(emotion)
        return responses.firstOrNull()?.copingStrategy
    }

    fun getDailyAffirmation(): String {
        val affirmations = listOf(
            "你今天做得很好，继续加油！",
            "你是独一无二的，值得被爱。",
            "相信自己，你有能力克服困难。",
            "每一天都是新的开始，充满希望。",
            "你的努力和进步，我都看在眼里。",
            "勇敢表达自己，你的声音很重要。",
            "休息也是一种重要的能力。",
            "犯错误没关系，重要的是学会成长。",
            "你比自己想象的更强大！",
            "无论发生什么，我都会陪着你。",
            "你的存在本身就是一种美好。",
            "今天的你比昨天更棒！",
            "你值得拥有幸福和快乐。",
            "相信你的直觉，它会指引你。",
            "每一个小进步都值得庆祝。",
            "你不需要完美，只需要真实。",
            "你的笑容能照亮整个世界。",
            "困难只是暂时的，你是永恒的。",
            "你有无限的潜力等待发掘。",
            "今天也是爱自己的一天！",
            "你已经足够好，不需要证明什么。",
            "你的努力终将得到回报。",
            "相信过程，时间会给你答案。",
            "你是被祝福的，也是被爱的。",
            "每一天都是成长的机会。"
        )
        return affirmations.random()
    }

    fun getBreathingExercise(): String {
        val exercises = listOf(
            "试试'4-7-8'呼吸法：用鼻子吸气4秒，屏住呼吸7秒，用嘴巴呼气8秒。重复几次，你会感觉平静下来。",
            "想象你面前有一朵小花，慢慢吸气闻花香（4秒），慢慢呼气吹花瓣（6秒）。",
            "像吹气球一样：深深吸气让肚子鼓起来，然后慢慢呼气让肚子瘪下去。",
            "试试数数呼吸：吸气数1-2-3-4，呼气数1-2-3-4-5-6。",
            "试试方形呼吸：吸气4秒，屏息4秒，呼气4秒，屏息4秒。像画正方形一样。",
            "想象你吸入的是温暖的阳光，呼出的是灰色的乌云。每一次呼气，都带走一点烦恼。",
            "试试深呼吸：用鼻子深深吸气，感受空气从鼻子到喉咙到肺部，然后慢慢呼气。",
            "用手按住肚子，吸气时让肚子鼓起，呼气时让肚子瘪下去。这是腹式呼吸。",
            "试试叹息呼吸：深深吸一口气，然后长长地叹一口气，把所有的压力都叹出去。",
            "闭上眼睛，专注于呼吸的感觉。感受空气进出身体的清凉和温暖。"
        )
        return exercises.random()
    }

    fun getEmotionNamingGuide(): String {
        return "情绪就像天气一样，有晴天、阴天、雨天。当你感觉不好的时候，可以试着说出它的名字：我现在是难过的、生气的、害怕的...说出来就已经好了一半！"
    }

    fun getRelaxationTip(): String {
        val tips = listOf(
            "试试渐进式肌肉放松：从脚趾开始，慢慢收紧肌肉，保持几秒，然后慢慢放松。",
            "听一些轻柔的音乐，闭上眼睛，想象自己在一个美丽的地方，比如海边或森林。",
            "做一些简单的拉伸运动，放松身体，也放松心情。",
            "喝一杯温水，慢慢品尝，感受水的温暖流过身体。",
            "写日记，把烦恼写下来，就像把它们从心里拿出来放在纸上。",
            "做一些深呼吸，然后做一些让你开心的事情，比如看一部喜欢的电影。",
            "冥想几分钟，专注于当下，不要想过去或未来。",
            "散步或慢跑，让身体动起来，释放压力和负面情绪。",
            "和朋友或家人聊聊天，分享你的感受，不要独自承受。",
            "做一些深呼吸，然后微笑。微笑可以让你的大脑释放快乐的化学物质。"
        )
        return tips.random()
    }

    fun getCopingStrategy(): String {
        val strategies = listOf(
            "面对问题：不要逃避，勇敢面对。把问题分解成小部分，一步步解决。",
            "寻求帮助：不要觉得寻求帮助是软弱的表现，真正的勇敢是知道自己的局限。",
            "积极思考：试着从积极的角度看待问题，每一个困难都是成长的机会。",
            "自我关怀：照顾好自己的身体和情绪，就像照顾好朋友一样。",
            "时间管理：合理安排时间，不要给自己太多压力，学会说不。",
            "设定界限：学会保护自己，不要让别人的情绪影响到你。",
            "保持希望：相信一切都会好起来，困难只是暂时的。",
            "学会放手：有些事情我们无法控制，学会接受和放手。",
            "培养爱好：做一些让你快乐的事情，这可以帮助你缓解压力。",
            "感恩练习：每天想想你感激的事情，这可以帮助你看到生活中的美好。"
        )
        return strategies.random()
    }

    fun getFriendshipAdvice(): String {
        val advice = listOf(
            "真正的朋友会接受你的全部，包括你的缺点。",
            "友谊需要双方的努力和付出，不要只索取不付出。",
            "学会倾听，有时候朋友只是需要有人听他们说话。",
            "不要在意朋友的数量，重要的是质量。",
            "和朋友发生矛盾时，试着冷静沟通，不要轻易绝交。",
            "尊重朋友的隐私和个人空间，不要干涉太多。",
            "在朋友需要帮助的时候伸出援手，友谊会因此更深厚。",
            "学会原谅，每个人都会犯错，给朋友一个改正的机会。",
            "保持联系，即使不经常见面，也要让朋友知道你在乎他们。",
            "友谊是相互的，不要让一个人一直付出。"
        )
        return advice.random()
    }

    fun getStudyTip(): String {
        val tips = listOf(
            "制定学习计划，合理安排时间，不要拖延。",
            "找到适合自己的学习方法，每个人的学习方式都不同。",
            "保持专注，学习时放下手机，创造一个安静的环境。",
            "适当休息，学习45-60分钟后休息10-15分钟，效率更高。",
            "做好笔记，帮助自己记住和理解学习内容。",
            "把大目标分解成小目标，一步步完成，更容易坚持。",
            "遇到不懂的问题及时请教老师或同学，不要累积。",
            "保持好奇心，把学习看作是探索新知识的过程。",
            "定期复习，巩固所学内容，避免遗忘。",
            "劳逸结合，学习之余也要做一些喜欢的事情，保持身心平衡。"
        )
        return tips.random()
    }

    fun getFamilyCommunicationTip(): String {
        val tips = listOf(
            "和家人沟通时，保持耐心，不要急于表达自己的观点。",
            "试着站在家人的角度看问题，理解他们的想法和感受。",
            "用温和的语气说话，避免争吵和指责。",
            "定期和家人交流，分享自己的生活和感受。",
            "尊重家人的意见，即使不同意也要认真倾听。",
            "学会表达感谢，让家人知道你感激他们的付出。",
            "遇到矛盾时，试着冷静下来再沟通，不要情绪化。",
            "给家人一些个人空间，每个人都需要独处的时间。",
            "多花时间陪伴家人，一起做一些有趣的事情。",
            "记住，家人是你最坚强的后盾，他们永远支持你。"
        )
        return tips.random()
    }
}
