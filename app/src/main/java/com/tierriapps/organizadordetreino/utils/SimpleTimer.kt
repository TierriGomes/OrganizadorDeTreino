package com.tierriapps.organizadordetreino.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import com.tierriapps.organizadordetreino.notifications.NotificationDoTraining

class SimpleTimer(val isTotalTimer: Boolean = false) {
    var context: Context? = null
    private var countDownTimer: CountDownTimer? = null
    var isRunning = false
    private var secondsElapsed: Long = 0


    fun setfuckingContext(context: Context){
        this.context = context
    }
    fun start() {
        // Se o cronômetro já estiver em execução, não faça nada
        if (isRunning) {
            return
        }

        // Começar a contagem regressiva
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsElapsed++
                updateTextView()
            }

            override fun onFinish() {
                // Não precisa fazer nada aqui
            }
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
        if(isTotalTimer){
            val intent = Intent(context, NotificationDoTraining.MyBroadcastReceiver::class.java).apply {
                action = "CHRONOMETER_TOTAL_ATUALIZED"
                putExtra("seconds2", secondsElapsed.toInt())
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, secondsElapsed.toInt(), intent, 0)
            pendingIntent.send()
        }else{
            val intent = Intent(context, NotificationDoTraining.MyBroadcastReceiver::class.java).apply {
                action = "CHRONOMETER_ATUALIZED"
                putExtra("seconds", secondsElapsed.toInt())
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, secondsElapsed.toInt(), intent, 0)
            pendingIntent.send()
        }
    }
}