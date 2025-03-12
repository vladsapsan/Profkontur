package com.android.profkontur.timer

import kotlinx.coroutines.CoroutineScope

class TestTimer(
    timerDurationSeconds: Long,
    coroutineScope: CoroutineScope,
    private val OnTimeEndAction:()->Unit): AbstractTimer(timerDurationSeconds,coroutineScope) {

    override fun onTimerFinished() {
        // Perform action when timer finishes for Quiz
        OnTimeEndAction.invoke()
    }

    // Extension function to pass lambda expression
    fun interface OnTimerEndAction {
        fun onTimerEnd()
    }
}