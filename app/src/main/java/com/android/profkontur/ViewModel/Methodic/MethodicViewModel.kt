package com.android.profkontur.ViewModel.Methodic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.profkontur.Model.LoadingState
import com.android.profkontur.Model.MethodicItemModel
import com.android.profkontur.RestApi.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MethodicViewModel : ViewModel() {
    val testList = MutableLiveData<List<MethodicItemModel>>()
    //Хранение состояния данных
    private val _LoadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val loadingstate: StateFlow<LoadingState> = _LoadingState

    fun loadMethodicTestList(MehodicName:String) {
        RetrofitInstance.api.getMethodicTestsByName(MehodicName).enqueue(object :
            Callback<List<MethodicItemModel>> {
            override fun onResponse(call: Call<List<MethodicItemModel>>, response: Response<List<MethodicItemModel>>) {
                if (response.isSuccessful) {
                    testList.value = response.body()
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
}