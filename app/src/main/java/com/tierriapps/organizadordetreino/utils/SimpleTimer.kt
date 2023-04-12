package com.tierriapps.organizadordetreino.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.Vibrator
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.tierriapps.organizadordetreino.R
import com.tierriapps.organizadordetreino.notifications.NotificationDoTraining

class SimpleTimer(
    val context: Context,
    val remoteViews: RemoteViews,
    val id: Int,
    val notificationManager: NotificationManager,
    val builder: NotificationCompat.Builder,
    val secondsToNotificate: Int = 3600000) {
    private var countDownTimer: CountDownTimer? = null
    var isRunning = false
    private var secondsElapsed: Int = 0

    fun start() {
        // Se o cronômetro já estiver em execução, não faça nada
        if (isRunning) {
            return
        }
        // Começar a contagem regressiva
        countDownTimer = object : CountDownTimer(6000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsElapsed++
                updateTextView()
            }
            override fun onFinish() {}
        }
        countDownTimer?.start()
        isRunning = true
    }
    fun pause() {
        // Se o cronômetro não estiver em execução, não faça nada
        if (!isRunning) {
            return
        }
        // Pausar o cronômetro
        countDownTimer?.cancel()
        countDownTimer = null
        isRunning = false
    }
    fun reset() {
        // Zerar os segundos decorridos e atualizar o TextView
        secondsElapsed = 0
        updateTextView()
    }

    private fun updateTextView() {
        remoteViews.setTextViewText(id, secondsElapsed.toString())
        notificationManager.notify(123, builder.build())
        if (secondsElapsed == secondsToNotificate){
            val mediaPlayer = MediaPlayer.create(context, R.raw.alert)
            mediaPlayer.start()
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(500)
            mediaPlayer.release()
        }
    }
}