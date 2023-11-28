package com.akkobana.coroutines.ui

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface MainView: MvpView {

    fun showErrorSnackbar(errorMessage: Throwable)

    fun showSuccessSnackbar()

    fun changeTimerButtonUi(isTimerWorking: Boolean)

    fun setCapybaraButtonEnabled(isEnabled: Boolean)

    fun setDownloadingState(downloadingState: Int)

    fun setTimerValue(hours: String, minutes: String, seconds: String)

    fun setTimerProgressBar(progressValue: Int)
}