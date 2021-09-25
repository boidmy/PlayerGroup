package com.example.playergroup.data.room

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun listToJsonString(value: List<VoteData>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonStringToList(value: String) = Gson().fromJson(value, Array<VoteData>::class.java).toList()

    @TypeConverter
    fun reviewListToJsonString(value: List<VoteReview>?): String = Gson().toJson(value)

    @TypeConverter
    fun reviewJsonStringToList(value: String) = Gson().fromJson(value, Array<VoteReview>::class.java).toList()
}