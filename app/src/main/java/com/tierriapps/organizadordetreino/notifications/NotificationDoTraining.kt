package com.tierriapps.organizadordetreino.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.tierriapps.organizadordetreino.R
import com.tierriapps.organizadordetreino.data.models.MyDivision
import com.tierriapps.organizadordetreino.utils.SecondsToTime
import com.tierriapps.organizadordetreino.utils.SimpleTimer
import java.text.SimpleDateFormat
import java.util.*

class NotificationDoTraining(val context: Context, val division: MyDivision) {

    // VARIAVEIS DE NOTIFICAÇAO E DE LAYOUT
    companion object{
        private lateinit var notificationManager: NotificationManager
        private lateinit var remoteViews: RemoteViews
        private lateinit var builder: NotificationCompat.Builder
        private var notify = true
        private val numericKeyBoard = listOf(
            R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6,
            R.id.button7, R.id.button8, R.id.button9, R.id.buttonDot, R.id.buttonSendText)
        private val tvExerciceName = R.id.textviewExerciceToDo
        private val tvShowWhatIsDigited = R.id.textviewDigited
        private val tvShowWhatIsInserted = R.id.textviewRepsInserted
        private val chronometerView = R.id.textviewChronometer

        // VARIAVEIS DE DADOS
        private lateinit var division: MyDivision
        private var exerciceShowing = 0
        private var exercicesCount: Int? = null
        private val listsOfRepsDid = mutableListOf<MutableList<Float>>()
        private val temporaryList = mutableListOf<Float>()
        private var stringDigited = ""

        // VARIAVEIS DO CHRONOMETER
        private lateinit var chronometer: SimpleTimer
        private lateinit var chronometerTotalTime: SimpleTimer
        private var simpleSeconds = 0
        private var constantSeconds = 0
        private var secondsToShow = ""

        fun initialize(context: Context, adivision: MyDivision) {
            division = adivision
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            remoteViews = RemoteViews(context.packageName, R.layout.notification_layout)
            notify = true
            exercicesCount = division.exercicesList.size
            chronometer = SimpleTimer().apply { setfuckingContext(context) }
            chronometerTotalTime = SimpleTimer(true).apply{ setfuckingContext(context) }
            builder = NotificationCompat.Builder(context!!, "trainingChannel")
            builder.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setCustomBigContentView(remoteViews)
                .setOngoing(true)
                .setStyle(NotificationCompat.BigTextStyle())
                .setSound(null)
        }
        fun finishNotification(context: Context){
            // SALVA OS DADOS DA LISTA
            while (exerciceShowing <= exercicesCount!!-1) {
                if (listsOfRepsDid.size < exercicesCount!!) {
                    for (c in temporaryList.size until division.exercicesList[exerciceShowing].series!!.toInt()) {
                        temporaryList.add(0f)
                    }
                    listsOfRepsDid.add(temporaryList.toMutableList())
                    temporaryList.clear()
                    exerciceShowing ++
                }
            }
            val sharedPreferences = context.getSharedPreferences("NOTIFICATION_DATA", Context.MODE_PRIVATE)
            val stringList = Gson().toJson(listsOfRepsDid)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            val lastDate = sharedPreferences.getString("lastDate", "")
            val totalSavedSeconds = sharedPreferences.getInt("totalSavedSeconds", 0)
            val totalDaysTrained = sharedPreferences.getInt("totalDaysTrained", 0)
            Log.d("oiaso", "${lastDate} ${totalSavedSeconds} $totalDaysTrained")
            if(currentDate != lastDate && constantSeconds > 1800){
                sharedPreferences.edit().putInt("lastTrainingTime", constantSeconds).apply()
                sharedPreferences.edit().putString("lastDate", currentDate).apply()
                sharedPreferences.edit().putInt("totalSavedSeconds", totalSavedSeconds+ constantSeconds).apply()
                sharedPreferences.edit().putInt("totalDaysTrained", totalDaysTrained + 1).apply()
            }
            sharedPreferences.edit().putString("stringList", stringList).apply()

            exerciceShowing = 0
            listsOfRepsDid.clear()
            temporaryList.clear()
            stringDigited = ""
            chronometer.pause()
            chronometerTotalTime.pause()

            notify = false
            notificationManager.cancelAll()
        }
    }
    fun onDestroyNotification(){
        finishNotification(context)
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
        notificationManager.notify(123, builder.build())
    }
    private fun defIntents(){
        // intents do teclado: buttonDot = 10 e buttonSendText = 11
        for (key in  1..numericKeyBoard.size) {
            val intentKey = Intent(context, MyBroadcastReceiver::class.java).apply {
                action = "NOTIFICATION_KEY_PRESSED"
                putExtra("keyPressed", key) }
            val pendingIntentKey = PendingIntent.getBroadcast(
                context, key, intentKey, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(numericKeyBoard[key-1], pendingIntentKey) }

        // intent do click no cronometro
        val intentChronometer = Intent(context, MyBroadcastReceiver::class.java).apply {
            action = "CHRONOMETER_CLICKED" }
        val pendingIntentChronometer = PendingIntent.getBroadcast(
            context, 0, intentChronometer, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setOnClickPendingIntent(chronometerView, pendingIntentChronometer)
    }


    class MyBroadcastReceiver constructor(): BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var soundUri: Uri? = null
            var pattern: LongArray? = null
            var esperevibrar = false
            if(exerciceShowing > division.exercicesList.lastIndex){
                finishNotification(context)
            }

            // SE RECEBER UMA INTENT DE CLIQUE NO TECLADO
            if (intent.action == "NOTIFICATION_KEY_PRESSED"){
                val keyPressed = intent.getIntExtra("keyPressed", -1)
                // se a key pressionada for enter (11) adicionamos a string na lista de reps
                // e atualizamos a textview com lista modificada
                if (keyPressed == 11) {
                    if (stringDigited.length <= 4) {
                        temporaryList.add(stringDigited.toFloatOrNull() ?: 0f)
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
                // se for um outro numero ou "." adicionamos os dados a string e atualizamos o textview
                else if(keyPressed != -1){
                    if(keyPressed != 10 || !stringDigited.contains('.')){
                        stringDigited += if (keyPressed != 10) keyPressed.toString() else "."
                    }
                    remoteViews.setTextViewText(tvShowWhatIsDigited, stringDigited)
                }
            }

            // SE RECEBER UMA INTENT DE CLIQUE NO CRONOMETRO
            else if(intent.action == "CHRONOMETER_CLICKED"){
                if(!chronometerTotalTime.isRunning){
                    chronometerTotalTime.start()
                }
                if(chronometer.isRunning){
                    chronometer.pause()
                    chronometer.reset()
                }else{
                    chronometer.start()
                }
            }

            // SE RECEBER UMA ATUALIZAÇAO DE TEMPO DO CRONOMETRO
            else if(intent.action == "CHRONOMETER_ATUALIZED"){
                val timeToVibrate = division.exercicesList[exerciceShowing].rest?.toInt()
                simpleSeconds = intent.getIntExtra("seconds", 0)
                remoteViews.setTextViewText(R.id.textviewChronometer, "${simpleSeconds}\n\n${secondsToShow}")
                if (simpleSeconds == timeToVibrate?:60){
                    // Configura o som da notificação
                    val mediaPlayer = MediaPlayer.create(context, R.raw.alert)
                    mediaPlayer.start()
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(500) // Vibra por 500 milissegundos
                    Thread.sleep(1000)
                    mediaPlayer.release()
                }
            }

            // SE RECEBER UMA ATUALIZAÇAO DE TEMPO DO CRONOMETRO TOTAl
            else if(intent.action == "CHRONOMETER_TOTAL_ATUALIZED"){
                constantSeconds = intent.getIntExtra("seconds2", 0)
                secondsToShow = SecondsToTime.segundosParaHorario(constantSeconds)
                remoteViews.setTextViewText(R.id.textviewChronometer, "${simpleSeconds}\n\n${secondsToShow}")
            }

            if(exerciceShowing <= division.exercicesList.lastIndex && notify){
                remoteViews.setTextViewText(tvExerciceName, division.exercicesList[exerciceShowing].exerciceName)
                notificationManager.notify(123, builder.build())
            }
        }
    }
}