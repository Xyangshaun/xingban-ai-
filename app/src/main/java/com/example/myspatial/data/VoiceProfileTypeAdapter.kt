package com.example.myspatial.data

import com.google.gson.*
import java.lang.reflect.Type

class VoiceProfileTypeAdapter : JsonSerializer<VoiceProfile>, JsonDeserializer<VoiceProfile> {

    private companion object {
        const val TYPE_FIELD = "type"
        const val PRESET_TYPE = "PRESET"
        const val CLONED_TYPE = "CLONED"
    }

    override fun serialize(src: VoiceProfile?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (src == null) return JsonNull.INSTANCE

        val jsonObject = JsonObject()
        when (src) {
            is VoiceProfile.Preset -> {
                jsonObject.addProperty(TYPE_FIELD, PRESET_TYPE)
                jsonObject.add("voiceTone", context?.serialize(src.voiceTone) ?: JsonNull.INSTANCE)
            }
            is VoiceProfile.Cloned -> {
                jsonObject.addProperty(TYPE_FIELD, CLONED_TYPE)
                jsonObject.addProperty("cloneId", src.cloneId)
                jsonObject.addProperty("displayName", src.displayName)
                jsonObject.add("relationship", context?.serialize(src.relationship) ?: JsonNull.INSTANCE)
                jsonObject.addProperty("audioSamplePath", src.audioSamplePath)
                jsonObject.addProperty("createdAt", src.createdAt)
            }
        }
        return jsonObject
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): VoiceProfile {
        if (json == null || !json.isJsonObject) {
            throw JsonParseException("VoiceProfile must be an object")
        }

        val jsonObject = json.asJsonObject
        val type = jsonObject.get(TYPE_FIELD)?.asString ?: throw JsonParseException("Missing type field")

        return when (type) {
            PRESET_TYPE -> {
                val voiceTone = context?.deserialize<VoiceTone>(jsonObject.get("voiceTone"), VoiceTone::class.java)
                    ?: throw JsonParseException("Missing voiceTone")
                VoiceProfile.Preset(voiceTone)
            }
            CLONED_TYPE -> {
                val cloneId = jsonObject.get("cloneId")?.asString ?: throw JsonParseException("Missing cloneId")
                val displayName = jsonObject.get("displayName")?.asString ?: throw JsonParseException("Missing displayName")
                val relationship = context?.deserialize<ParentalRelationship>(jsonObject.get("relationship"), ParentalRelationship::class.java)
                    ?: throw JsonParseException("Missing relationship")
                val audioSamplePath = jsonObject.get("audioSamplePath")?.asString ?: throw JsonParseException("Missing audioSamplePath")
                val createdAt = jsonObject.get("createdAt")?.asLong ?: System.currentTimeMillis()
                VoiceProfile.Cloned(cloneId, displayName, relationship, audioSamplePath, createdAt)
            }
            else -> throw JsonParseException("Unknown VoiceProfile type: $type")
        }
    }
}
