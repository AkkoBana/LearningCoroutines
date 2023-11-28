package com.akkobana.coroutines.ui

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MainPresenter @Inject constructor() : MvpPresenter<MainView>() {

    var isTimerWorking: Boolean = false
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewState.showErrorSnackbar(throwable)
    }
    private var scope =
        CoroutineScope(CoroutineName("CommonCoroutine") + SupervisorJob() + Dispatchers.IO + exceptionHandler)
    private var startTimerScope = CoroutineScope(scope.coroutineContext)
    private var time: Duration = Duration.ZERO
    var hours: Long = 0
    var minutes: Int = 0
    var seconds: Int = 0
    private var progressValue: Float = 0f
    private var progressValueTick: Float = 0f

    fun loadCapybaraImage() {
        scope.launch {
            for (i in 100 downTo 0 step (1..5).random()) {
                delay(100)
                viewState.setDownloadingState(i)
                if (i < 30) {
                    withContext(Dispatchers.Main) {
                        viewState.setCapybaraButtonEnabled(true)
                    }
                    throw Exception("Loading error")
                }
            }
        }
    }

    private fun setTime() {
        time = hours.hours + minutes.minutes + seconds.seconds
    }

    private fun setProgressValue() {
        progressValueTick = time.inWholeMilliseconds / 100f
    }

    private fun formatMilliseconds() {
        try {
            time.toComponents { hours, minutes, seconds, _ ->
                Log.d("MyLog", "$hours $minutes $seconds")
                this.hours = hours
                this.minutes = minutes
                this.seconds = seconds
            }
        } catch (ex: Throwable) {
            viewState.showErrorSnackbar(ex)
        }
    }

    private fun startCoroutineTimer() {
        startTimerScope.launch {
            while (isTimerWorking) {
                delay(1000)
                Log.d("timer", time.toString())
                time = (time.inWholeSeconds - SECOND.seconds.inWholeSeconds).seconds
                scope.launch(Dispatchers.Main) {
                    if (time.inWholeSeconds >= 0) {
                        formatMilliseconds()
                        viewState.setTimerValue(
                            hours.toString().padStart(2, '0'),
                            minutes.toString().padStart(2, '0'),
                            seconds.toString().padStart(2, '0')
                        )
                        progressValue = (SECOND.seconds.inWholeMilliseconds / progressValueTick + progressValue)
                        viewState.setTimerProgressBar(
                            progressValue.toInt()
                        )
                    } else {
                        isTimerWorking = false
                        progressValue = 0f
                        viewState.setTimerProgressBar(100)
                        viewState.changeTimerButtonUi(isTimerWorking)
                        viewState.showSuccessSnackbar()
                    }
                }
            }
        }
    }

    fun startTimer() {
        setTime()
        setProgressValue()
        isTimerWorking = true
        startCoroutineTimer()
    }

    fun stopTimer() {
        isTimerWorking = false
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val SECOND = 1L
    }
}