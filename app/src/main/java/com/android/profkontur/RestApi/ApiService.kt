package com.android.profkontur.RestApi

import com.android.profkontur.Model.MethodicItemModel
import com.android.profkontur.Model.MethodicTestListAdapter
import com.android.profkontur.Model.QuestionsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("eNB-rCM-S7r-xWp/test")
    fun getTestByName(@Query("name") name: String): Call<QuestionsData>
    @GET("eNB-rCM-S7r-xWp/methodic")
    fun getMethodicTestsByName(@Query("name") name: String): Call<List<MethodicItemModel>>
}