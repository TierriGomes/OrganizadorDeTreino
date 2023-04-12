package com.tierriapps.organizadordetreino.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.tierriapps.organizadordetreino.R
import com.tierriapps.organizadordetreino.data.models.MyDivision
import com.tierriapps.organizadordetreino.utils.SimpleTimer
import java.text.SimpleDateFormat
import java.util.*

class NotificationDoTraining(val context: Context, val division: MyDivision) {

    // VARIAVEIS DE NOTIFICAÇAO E DE LAYOUT
    companion object{
        private lateinit var notificationManager: NotificationManager
        private lateinit var remoteViews: RemoteViews
        @SuppressLint("StaticFieldLeak")
        private var builder: NotificationCompat.Builder? = null
        private var notify = true
        private val numericKeyBoard = listOf(
            R.id.button0, R.id.button1, R.id.button2,
            R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8,
            R.id.button9, R.id.buttonDot, R.id.buttonSendText, R.id.buttonDel)
        private val tvExerciceName = R.id.textviewExerciceToDo
        private val tvShowWhatIsDigited = R.id.textviewDigited
        private val tvShowWhatIsInserted = R.id.textviewRepsInserted
        private val chronometerView = R.id.textviewChronometer

        // VARIAVEIS DE DADOS
        private lateinit var division: MyDivision
        private var exerciceShowing = 0
        private var exercicesCount: Int? = null
        private val listsOfRepsDid = mutableListOf<MutableList<String>>()
        private val temporaryList = mutableListOf<String>()
        private var stringDigited = ""

        // VARIAVEIS DO CHRONOMETER
        @SuppressLint("StaticFieldLeak")
        private var chronometer: SimpleTimer? = null

        fun initialize(context: Context, adivision: MyDivision) {
            division = adivision
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            remoteViews = RemoteViews(context.packageName, R.layout.notification_layout)
            notify = true
            exercicesCount = division.exercicesList.size
            builder = NotificationCompat.Builder(context, "trainingChannel")
            builder!!.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setCustomBigContentView(remoteViews)
                .setOngoing(true)
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_MAX)
            chronometer = SimpleTimer(
                context, remoteViews, R.id.textviewChronometer,
                notificationManager,
                builder!!, division.exercicesList[exerciceShowing].rest?.toInt()?:60)
        }
        fun finishNotification(context: Context){
            // SALVA OS DADOS DA LISTA
            while (exerciceShowing <= exercicesCount!!-1) {
                if (listsOfRepsDid.size < exercicesCount!!) {
                    for (c in temporaryList.size until division.exercicesList[exerciceShowing].series!!.toInt()) {
                        temporaryList.add("0")
                    }
                    listsOfRepsDid.add(temporaryList.toMutableList())
                    temporaryList.clear()
                    exerciceShowing ++
                }
            }
            val sharedPreferences = context.getSharedPreferences("NOTIFICATION_DATA", Context.MODE_PRIVATE)
            val stringList = Gson().toJson(listsOfRepsDid)
            sharedPreferences.edit().putString("stringList", stringList).apply()

            builder = null
            exerciceShowing = 0
            listsOfRepsDid.clear()
            temporaryList.clear()
            stringDigited = ""
            chronometer?.pause()
            chronometer = null
            notify = false
            notificationManager.cancelAll()
        }
    }
    fun createNotification(){
        initialize(context, division)
        defIntents()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "trainingChannel",
                "Training Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        remoteViews.setTextViewText(tvExerciceName, division.exercicesList[exerciceShowing].exerciceName)
        notificationManager.notify(123, builder!!.build())
    }
    private fun defIntents(){
        // intents do teclado: buttonDot = 10 e buttonSendText = 11
        for (key in  0 until numericKeyBoard.size) {
            val intentKey = Intent(context, MyBroadcastReceiver::class.java).apply {
                action = "NOTIFICATION_KEY_PRESSED"
                putExtra("keyPressed", key) }
            val pendingIntentKey = PendingIntent.getBroadcast(
                context, key, intentKey, PendingIntent.FLAG_IMMUTABLE)
            remoteViews.setOnClickPendingIntent(numericKeyBoard[key], pendingIntentKey) }

        // intent do click no cronometro
        val intentChronometer = Intent(context, MyBroadcastReceiver::class.java).apply {
            action = "CHRONOMETER_CLICKED" }
        val pendingIntentChronometer = PendingIntent.getBroadcast(
            context, 99, intentChronometer, PendingIntent.FLAG_IMMUTABLE)
        remoteViews.setOnClickPendingIntent(chronometerView, pendingIntentChronometer)
    }


    class MyBroadcastReceiver constructor(): BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(exerciceShowing > division.exercicesList.lastIndex){
                finishNotification(context)
            }

            // SE RECEBER UMA INTENT DE CLIQUE NO TECLADO
            if (intent.action == "NOTIFICATION_KEY_PRESSED"){
                val keyPressed = intent.getIntExtra("keyPressed", -1)
                // se a key pressionada for enter (11) adicionamos a string na lista de reps
                // e atualizamos a textview com lista modificada
                if (keyPressed == 11) {
                    if (stringDigited.length <= 12) {
                        temporaryList.add(stringDigited)
                        if (temporaryList.size == division.exercicesList[exerciceShowing].series?.toInt()){
                            listsOfRepsDid.add(temporaryList.toMutableList())
                            exerciceShowing ++
                            temporaryList.clear()
                        }
                        remoteViews.setTextViewText(tvShowWhatIsInserted, temporaryList.toString()
                            .replace(",", "   ")
                            .replace("[", " ")
                            .replace("]", ""))
                    }
                    stringDigited = ""
                    remoteViews.setTextViewText(tvShowWhatIsDigited, stringDigited)
                }
                // se for um outro numero ou " .. " adicionamos os dados a string e atualizamos o textview
                else if(keyPressed != 12){
                    stringDigited += if (keyPressed != 10) keyPressed.toString() else ".."
                    remoteViews.setTextViewText(tvShowWhatIsDigited, stringDigited)
                }
                //se for o botao delete
                else {
                    stringDigited = ""
                    remoteViews.setTextViewText(tvShowWhatIsDigited, stringDigited)
                }
            }

            // SE RECEBER UMA INTENT DE CLIQUE NO CRONOMETRO
            else if(intent.action == "CHRONOMETER_CLICKED"){
                if(chronometer!!.isRunning){
                    chronometer!!.pause()
                    chronometer!!.reset()
                }else{
                    chronometer!!.start()
                }
            }

            if(exerciceShowing <= division.exercicesList.lastIndex && notify){
                remoteViews.setTextViewText(tvExerciceName, division.exercicesList[exerciceShowing].exerciceName)
                notificationManager.notify(123, builder!!.build())
            }
        }
    }
}