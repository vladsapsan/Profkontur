package com.android.profkontur.ViewModel

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.android.profkontur.Model.MetaData
import com.android.profkontur.Model.Question
import com.android.profkontur.Model.QuestionsData
import com.android.profkontur.timer.TestTimer
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.android.profkontur.Model.LoadingState
import com.android.profkontur.Model.Scales
import com.android.profkontur.Model.ScalesDeserializer
import com.android.profkontur.RestApi.RetrofitInstance
import com.android.profkontur.RestApi.RetrofitTestInstance
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class TestsVIewModel(private val savedStateHandle: SavedStateHandle):ViewModel() {

    //Хранение общих данных теста
    private val _AllTestData = MutableStateFlow<QuestionsData?>(null)
    val AllTestData: StateFlow<QuestionsData?> = _AllTestData

    //Хранение состояния данных
    private val _LoadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val loadingstate:StateFlow<LoadingState> = _LoadingState

    private val _QurrentQuestionIndex = MutableStateFlow(1)
    val QurrentQuestionIndex :StateFlow<Int> = _QurrentQuestionIndex

    // Отслеживает выбранные ответы (индекс вопроса -> индекс ответа)
    private val _selectedAnswers = MutableStateFlow<MutableList<Int?>>(mutableListOf())
    val selectedAnswers: StateFlow<MutableList<Int?>> = _selectedAnswers

    // Отслеживает набранные очки по шкалам (название шкалы -> очки)
    private val _scaleScores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val scaleScores: StateFlow<Map<String, Int>> = _scaleScores

    private var _TestIsDone =  MutableLiveData(false)
    val TestIsDone : LiveData<Boolean> = _TestIsDone

    init {
        // Restore state from SavedStateHandle
        if(savedStateHandle.get<QuestionsData>("testData")==null){

        }else {
            val testDataJson = savedStateHandle.get<String>("testData")
            if (testDataJson != null) {
                _AllTestData.value = Gson().fromJson(testDataJson, QuestionsData::class.java)
            }
            _QurrentQuestionIndex.value = savedStateHandle.get<Int>("currentQuestionIndex") ?: 0
            _selectedAnswers.value = savedStateHandle.get<MutableList<Int?>>("selectedAnswers") ?: mutableListOf()
            _scaleScores.value = savedStateHandle.get<MutableMap<String, Int>>("totalScore") ?: mutableMapOf()
        }
    }
    // Функция для перехода к следующему вопросу
    @SuppressLint("SuspiciousIndentation")
    fun nextQuestion() {
        val questions = _AllTestData.value?.questions
            if (questions != null && _QurrentQuestionIndex.value < questions.size ) {
                _QurrentQuestionIndex.value++
            } else {
                TestDone()
            }
    }
    // Функция для перехода к следующему вопросу
    @SuppressLint("SuspiciousIndentation")
    fun SelfQuestion() {
        val questions = _AllTestData.value?.questions
        if (questions != null && _QurrentQuestionIndex.value < questions.size ) {
            _QurrentQuestionIndex.value++
        } else {
            TestDone()
        }
    }
    fun getCurrentQuestion(): Question? {
        return _AllTestData.value?.questions?.get(_QurrentQuestionIndex.value.toString())
    }
    fun getCurrentMeta(): MetaData? {
        return _AllTestData.value?.meta
    }
    fun selectAnswer(answerIndex: Int) {
        viewModelScope.launch {
            val currentQuestionIndex = _QurrentQuestionIndex.value-1
            val currentAnswers = _selectedAnswers.value.toMutableList()
            currentAnswers[currentQuestionIndex] = answerIndex
            _selectedAnswers.value = currentAnswers
        }
    }



    fun LoadDataFromApi(TestApiName:String){
        _LoadingState.value = LoadingState.Loading
        RetrofitTestInstance.api.getTestByName(TestApiName).enqueue(object : Callback<QuestionsData>{
            override fun onResponse(call: Call<QuestionsData>, response: Response<QuestionsData>) {
                if (response.isSuccessful) {
                    _AllTestData.value = response.body()
                    _selectedAnswers.value = MutableList(_AllTestData.value!!.questions?.size ?: 0) { null } // инициализируем
                    saveState() //Сохраняем данные для подгрузки при выходе из приложения если они еще есть
                    _LoadingState.value = LoadingState.Ready
                } else {
                    _LoadingState.value = LoadingState.Error("Ошибка загрузки edumotivation данных: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<QuestionsData>, t: Throwable) {
                _LoadingState.value = LoadingState.Error("Ошибка сети при загрузке edumotivation: ${t.message}")
            }
        })
    }


    fun calculateTotalScore(testData: QuestionsData, selectedAnswers: List<Int?>): Map<String,Int> {
        val scales = testData.counting?.scales ?: 0
        val totalScore = mutableMapOf<String, Int>()
        when (scales) {
            is Scales.MapScales -> {
                scales.scales?.forEach { (suff, scale) ->
                    totalScore.put(suff,0)
                    Log.i("Fire", suff)
                    Log.i("Fire", scale.toString())
                    scale.answers?.forEach { (questionNumber, answerScale) ->
                        val questionIndex = questionNumber.toIntOrNull()?.minus(1) ?: return@forEach
                        val selectedAnswerIndex = selectedAnswers.getOrNull(questionIndex)
                        if (selectedAnswerIndex != null && answerScale.answer_num?.toInt() == selectedAnswerIndex ) {
                            totalScore[suff] = totalScore[suff]!! + (answerScale.points?.toInt() ?: 0)
                        }
                    }
                }
            }
            is Scales.SingleScale -> {
                totalScore.put("Общие очки",0)
                scales.answers?.forEach { (_, answerScales) ->
                    answerScales.forEach{(questionNumber, answerScales)->
                        val questionIndex = questionNumber.toIntOrNull()?.minus(1) ?: return@forEach
                        val selectedAnswerIndex = selectedAnswers.getOrNull(questionIndex)
                        if (selectedAnswerIndex != null) {
                            totalScore["Общие очки"] = totalScore["Общие очки"]!! + answerScales.get(selectedAnswerIndex.toString())?.points?.toInt()!!
                        }
                    }

                }
            }
        }
        return totalScore
    }


    private fun TestDone(){
        _TestIsDone.value=true
        _AllTestData.value?.let {_scaleScores.value= calculateTotalScore(it,_selectedAnswers.value) }
    }

    fun TestReset(){
        _QurrentQuestionIndex.value=1
        _TestIsDone.value = false
    }

    private fun saveState() {
        val testDataJson = Gson().toJson(_AllTestData.value)
        savedStateHandle.set("testData", testDataJson)
        savedStateHandle.set("currentQuestionIndex", _QurrentQuestionIndex.value)
        savedStateHandle.set("selectedAnswers", _selectedAnswers.value)
        savedStateHandle.set("totalScore", _scaleScores.value)
    }


}