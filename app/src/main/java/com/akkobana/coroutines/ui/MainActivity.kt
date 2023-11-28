package com.akkobana.coroutines.ui

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import com.akkobana.coroutines.MainApplication
import com.akkobana.coroutines.R
import com.akkobana.coroutines.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter


class MainActivity : MvpAppCompatActivity(), MainView {

    private lateinit var binding: ActivityMainBinding

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun provideMainPresenter(): MainPresenter = MainApplication.appComponent.provideMainPresenter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        setupListeners()
    }

    private fun setupListeners() = with(binding) {
        capybaraButton.setOnClickListener {
            presenter.loadCapybaraImage()
            setCapybaraButtonEnabled(false)
        }
        timer.etHours.doAfterTextChanged {
            if (it.toString() != "") {
                presenter.hours = it.toString().toLong()
            }
        }
        timer.etMinutes.doAfterTextChanged {
            if (it.toString() != "") {
                presenter.minutes = it.toString().toInt()
            }
        }
        timer.etSeconds.doAfterTextChanged {
            if (it.toString() != "") {
                presenter.seconds = it.toString().toInt()
            }
        }
        timerButton.setOnClickListener {
            if (presenter.isTimerWorking) {
                presenter.stopTimer()
            } else {
                presenter.startTimer()
            }
            setTimerIsEnabled(presenter.isTimerWorking)
            changeTimerButtonUi(presenter.isTimerWorking)
        }
    }

    override fun setDownloadingState(downloadingState: Int) {
        binding.progress.progress = downloadingState
    }

    private fun setTimerIsEnabled(isEnabled: Boolean) = with(binding) {
        setEditTextIsEnabled(isEnabled, timer.etHours)
        setEditTextIsEnabled(isEnabled, timer.etMinutes)
        setEditTextIsEnabled(isEnabled, timer.etSeconds)
        timer.etHours.isClickable = isEnabled
        timer.etMinutes.isClickable = isEnabled
        timer.etSeconds.isClickable = isEnabled
    }

    private fun setEditTextIsEnabled(isEnabled: Boolean, editText: EditText) = with(editText) {
        isFocusable = isEnabled
        this.isEnabled = isEnabled
        isCursorVisible = isEnabled
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun showErrorSnackbar(errorMessage: Throwable) {
        Snackbar.make(binding.root, makeErrorText(errorMessage), Snackbar.LENGTH_LONG)
            .show()
    }

    override fun setCapybaraButtonEnabled(isEnabled: Boolean) {
        binding.capybaraButton.isEnabled = isEnabled
    }


    private fun makeErrorText(errorMessage: Throwable): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        builder.setSpan(
            ImageSpan(
                this@MainActivity,
                R.drawable.baseline_warning_amber_24
            ),
            0, 0, 0
        )
        builder.append(" $errorMessage")
        return builder
    }

    override fun showSuccessSnackbar() {
        Snackbar.make(binding.root, "Таймер окончен", Snackbar.LENGTH_LONG)
            .show()
    }

    override fun changeTimerButtonUi(isTimerWorking: Boolean) {
        if (isTimerWorking) binding.timerButton.text = "stop timer"
        else binding.timerButton.text = "start timer"
    }

    override fun setTimerValue(hours: String, minutes: String, seconds: String) = with(binding) {
        timer.etHours.setText(hours)
        timer.etMinutes.setText(minutes)
        timer.etSeconds.setText(seconds)
    }

    override fun setTimerProgressBar(progressValue: Int) {
        binding.timerProgressBar.progress = progressValue
    }
}