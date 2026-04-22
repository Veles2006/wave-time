package com.sae.wavetime.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.sae.wavetime.domain.model.KeyInfoPopulated
import com.sae.wavetime.domain.model.Penalty
import com.sae.wavetime.domain.model.Reward

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromReward(reward: Reward): String {
        return gson.toJson(reward)
    }

    @TypeConverter
    fun toReward(json: String): Reward {
        return gson.fromJson(json, Reward::class.java)
    }

    @TypeConverter
    fun fromPenalty(penalty: Penalty): String {
        return gson.toJson(penalty)
    }

    @TypeConverter
    fun toPenalty(json: String): Penalty {
        return gson.fromJson(json, Penalty::class.java)
    }

    @TypeConverter
    fun fromKeyInfo(keyInfo: KeyInfoPopulated?): String? {
        return gson.toJson(keyInfo)
    }

    @TypeConverter
    fun toKeyInfo(json: String?): KeyInfoPopulated? {
        return json?.let { gson.fromJson(it, KeyInfoPopulated::class.java) }
    }
}