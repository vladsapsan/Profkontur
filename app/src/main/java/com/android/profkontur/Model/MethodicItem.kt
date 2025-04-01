package com.android.profkontur.Model

 data class MethodicItemModel(
     val id: String,
     val num: String,
     val machine_name: String,
     val name: String,
     val authors: String,
     var isDone:Boolean?=false
 )

data class MethodicItemInformation(
    var respondent_id: String,
    var methodic: String,
    val tests:MutableMap<String,answers>
)

data class answers(
    var answer: MutableMap<Int, List<String>>?
)

data class MethodicAnswerResponse(
    val respondent_id: String?,
    val methodic: String?,
    val result_id: String?,
    val result_url: String?,
    val answers: List<String>?,
    val timestamp: String?
)