package com.android.profkontur

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class QuestionsData(
    val questions: ArrayList<Question>? = null // Map, где ключ - номер вопроса (String)
){}

@IgnoreExtraProperties
data class Question(
    val question: String? = null,
    val dsc: String? = null,
    val type: String? = null, // Например, "radios", "checkboxes", "text"
    val answers: ArrayList<Answer>? = null // Map, где ключ - номер ответа (String)
){}

@IgnoreExtraProperties
data class Answer(
    val answer: String? = null
){}

