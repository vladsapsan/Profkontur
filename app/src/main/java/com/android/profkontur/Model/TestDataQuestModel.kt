package com.android.profkontur.Model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

sealed class Scales {
    data class MapScales(val scales: Map<String, Scale>? = null) : Scales()
    data class SingleScale(val answers: Map<String,Map<String, Map<String, AnswerScale>>>? = null) : Scales()
}

data class Counting(
    val scales: Scales? = null
)

data class QuestionsData(
    val meta: MetaData? = null,
    val questions: Map<String, Question>? = null,
    val counting: Counting? = null
)

data class Question(
    val question: String? = null,
    val dsc: String? = null,
    val type: String? = null,
    val answers: Map<String, Answer>? = null,
    val borders:Map<String, Border>? = null
)
data class MetaData(
    val machine_name: String? = null,
    val name: String? = null,
    val prefix_name: String? = null,
    val logo: String? = null,
    val authors: String? = null,
    val short_dsc: String? = null,
    val dsc: String? = null,
    val instruction: String? = null,
    val age: String? = null,
    val time: String? = null,
    val questionsCount: String? = null
)

data class Border(
    val answer: String? = null,
)

data class Answer(
    val answer: String? = null,
    val dsc: String? = null
)

data class Scale(
    val answers: Map<String, AnswerScale>? = null // Key is question number (String)
)

data class AnswerScale(
    val points: String? = null,
    val answer_num: String? = null
)

sealed class LoadingState{
    object Loading:LoadingState()
    object Ready:LoadingState()
    data class Error(val messege:String): LoadingState()
}

//Декодикнг принимаемых тестовых json файлов в разделе начисления очков за ответы
class ScalesDeserializer : JsonDeserializer<Scales> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Scales {
        return if (json.isJsonObject && json.asJsonObject.has("answers")) {
            val answersMapType = object : TypeToken<Map<String, Map<String, Map<String, AnswerScale>>>>() {}.type
            val answersMap: Map<String,Map<String, Map<String, AnswerScale>>> = context.deserialize(json, answersMapType)
            Scales.SingleScale(answersMap)
        } else {
            val scalesMapType = object : TypeToken<Map<String, Scale>>() {}.type
            val scalesMap: Map<String, Scale> = context.deserialize(json, scalesMapType)
            Scales.MapScales(scalesMap)
        }
    }
}


