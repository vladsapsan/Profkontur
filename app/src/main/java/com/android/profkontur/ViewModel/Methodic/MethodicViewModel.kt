package com.android.profkontur.ViewModel.Methodic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.profkontur.Model.LoadingState
import com.android.profkontur.Model.MethodicAnswerResponse
import com.android.profkontur.Model.MethodicItemInformation
import com.android.profkontur.Model.MethodicItemModel
import com.android.profkontur.Model.answers
import com.android.profkontur.RestApi.RetrofitInstance
import com.android.profkontur.RestApi.RetrofitInstanceRegions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MethodicViewModel : ViewModel() {
    val testList = MutableLiveData<List<MethodicItemModel>>()

    var _MethodicItemInformation = MutableLiveData<MethodicItemInformation>()
    var MethodicItemInformation :LiveData<MethodicItemInformation> = _MethodicItemInformation
    //Хранение состояния данных
    private val _LoadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val loadingstate: StateFlow<LoadingState> = _LoadingState

    var TestMethodicAnswerResponse = MutableLiveData<MethodicAnswerResponse>()

    private val _TestSCount = MutableStateFlow<Int>(0)
    val TestSCount: StateFlow<Int> = _TestSCount

    private val _DoneTestSCount = MutableLiveData<Int>(0)
    var DoneTestSCount: LiveData<Int> = _DoneTestSCount

    var Methodic = MutableLiveData<String>("")
    var TestRelultUri = MutableLiveData<String?>(null)


    init {
        // Инициализация данных
        _MethodicItemInformation.value = MethodicItemInformation(
            respondent_id = "1948",
            methodic = "motivation",
            tests = mutableMapOf()
        )
    }

    fun setRespondentId(respondentId: String) {
        _MethodicItemInformation.value?.respondent_id = respondentId
    }

    fun setMethodic(methodic: String) {
        _MethodicItemInformation.value?.methodic = methodic
    }

    fun addTest(testName: String, answers: answers) {
        _MethodicItemInformation.value?.tests?.put(testName, answers)
    }

    fun loadMethodicTestList(MehodicName:String) {
        RetrofitInstance.api.getMethodicTestsByName(MehodicName).enqueue(object :
            Callback<List<MethodicItemModel>> {
            override fun onResponse(call: Call<List<MethodicItemModel>>, response: Response<List<MethodicItemModel>>) {
                if (response.isSuccessful) {
                    testList.value = response.body()
                    _TestSCount.value = testList.value?.size ?:0
                    _LoadingState.value = LoadingState.Ready // Загрузка завершена
                } else {
                    _LoadingState.value = LoadingState.Error("Ошибка загрузки списка тестов: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<MethodicItemModel>>, t: Throwable) {
                _LoadingState.value = LoadingState.Error("Ошибка сети: ${t.message}") // Загрузка завершена (с ошибкой)
            }
        })
    }

    fun SendTestResult(){
        _LoadingState.value = LoadingState.Loading
        RetrofitInstanceRegions.api.saveMethodicResult(MethodicItemInformation.value!!).enqueue(object : Callback<MethodicAnswerResponse>{
            override fun onResponse(call: Call<MethodicAnswerResponse>, response: Response<MethodicAnswerResponse>) {
                _LoadingState.value = LoadingState.Ready // Загрузка завершена
                TestMethodicAnswerResponse.value = response.body()
                Log.i("COCO",TestMethodicAnswerResponse.value.toString())
            }

            override fun onFailure(call: Call<MethodicAnswerResponse>, t: Throwable) {
                _LoadingState.value = LoadingState.Error("Ошибка сети: ${t.message}") // Загрузка завершена (с ошибкой)
            }
        })
    }

    fun TestDOneCountReport(){
        _DoneTestSCount.value = _DoneTestSCount.value?.plus(1)
    }

}