package com.android.profkontur.RestApi

import com.android.profkontur.Model.MethodicAnswerResponse
import com.android.profkontur.Model.MethodicItemInformation
import com.android.profkontur.Model.MethodicItemModel
import com.android.profkontur.Model.MethodicTestListAdapter
import com.android.profkontur.Model.QuestionsData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("eNB-rCM-S7r-xWp/test")
    fun getTestByName(@Query("name") name: String): Call<QuestionsData>
    @GET("eNB-rCM-S7r-xWp/methodic")
    fun getMethodicTestsByName(@Query("name") name: String): Call<List<MethodicItemModel>>
    @POST("eNB-rCM-S7r-xWp/result/save")
    fun saveMethodicResult(@Body methodicItem: MethodicItemInformation): Call<MethodicAnswerResponse>
    @GET
    fun downloadPdf(@Url fileUrl: String): Call<ResponseBody>
    
}