package com.android.profkontur.timer

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class AbstractTimer(
    private val TimerDurationSeconds:Long,
    private val CorutineScope:CoroutineScope
) {
private val _remainingTime = MutableStateFlow(TimerDurationSeconds)
    val remainingTime :StateFlow<Long> = _remainingTime

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning:StateFlow<Boolean> = _isTimerRunning

    private val _isTimerFinished = MutableStateFlow(false)
    val isTimerFinished: StateFlow<Boolean> = _isTimerFinished

    private var timerJob: Job? = null
    private var timerPassed :Long = 0

    init {
        _remainingTime.value = TimerDurationSeconds
    }

    fun StartTimer(){
        if(_isTimerRunning.value){
            return //Для предотвращения повторения таймеров
        }
        _isTimerRunning.value = true
        _isTimerFinished.value = false

        timerJob = CorutineScope.launch {
            for (remaining in _remainingTime.value downTo 0) {
                if(!_isTimerRunning.value){ // Cancel if stopped before naturally completed
                    break;
                }
                _remainingTime.value = remaining
                delay(1000) // Update every 1 second.
            }
            _isTimerRunning.value = false
            _isTimerFinished.value = true
            onTimerFinished()
        }

    }

    fun PauseTimer(){
        _isTimerRunning.value=false
        timerJob?.cancel()
    }

    fun StopTimer(){
        PauseTimer()
        _remainingTime.value - TimerDurationSeconds
        _isTimerFinished.value=false

    }

    // Protected method for concrete class to implement what to do on timer finishes
    protected abstract fun onTimerFinished()

}