package com.tierriapps.organizadordetreino

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.tierriapps.organizadordetreino.databinding.ActivityMainBinding
import com.tierriapps.organizadordetreino.utils.SecondsToTime


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //NAVIGATION AND TOOLBAR: variables
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //NAVIGATION AND TOOLBAR: def variables
        drawerLayout = binding.drawerlayout
        navigationView = binding.navigationview
        setSupportActionBar(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        // DATA
        val sharedPreferences = this.getSharedPreferences("NOTIFICATION_DATA", Context.MODE_PRIVATE)
        val totalSavedSeconds = SecondsToTime.segundosParaHorario(sharedPreferences.getInt("totalSavedSeconds", 0))
        var totalDaysTrained = sharedPreferences.getInt("totalDaysTrained", 0)
        val lastTrainingTime = SecondsToTime.segundosParaHorario(sharedPreferences.getInt("lastTrainingTime", 0))

        //NAVIGATION AND TOOLBAR: def functions
        navController = findNavController(binding.fragmentcontainerview.id)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(navigationView, navController)

        val header = binding.navigationview.getHeaderView(0)
        val viewImage = header.findViewById<ImageView>(R.id.imageViewProfile)
        val textviewDiasEHoras = header.findViewById<TextView>(R.id.textViewDaysAndHours)
        val textviewLastTrainingTime = header.findViewById<TextView>(R.id.textViewLastTrainingTime)

        textviewDiasEHoras.text = "Dias Treinados:\n${totalDaysTrained}\nHoras:\n${totalSavedSeconds}"
        textviewLastTrainingTime.text = "Tempo do ultimo treino:\n${lastTrainingTime}"
        when(totalDaysTrained){
            in 0..29 -> viewImage.setImageResource(R.drawable.galodecalca)
            in 30..89 -> viewImage.setImageResource(R.drawable.galobombado)
            in 90..179 -> viewImage.setImageResource(R.drawable.barata)
            in 180..360 -> viewImage.setImageResource(R.drawable.strong)
            else -> viewImage.setImageResource(R.drawable.brolly)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
    }
}