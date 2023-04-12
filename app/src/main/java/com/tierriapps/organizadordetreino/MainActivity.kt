package com.tierriapps.organizadordetreino

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.tierriapps.organizadordetreino.databinding.ActivityMainBinding
import com.tierriapps.organizadordetreino.notifications.NotificationDoTraining


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


        //NAVIGATION AND TOOLBAR: def functions
        navController = findNavController(binding.fragmentcontainerview.id)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(navigationView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("carai", "ondestroy chamado")
        try {
            NotificationDoTraining.finishNotification(applicationContext)
            Toast.makeText(
                this, "a notificaçao e activity foi destruida", Toast.LENGTH_SHORT).show()
            Log.d("carai", "notificaçao destruida")
        }catch (e: Exception){
            Toast.makeText(
                this, "Nao destruiu nenhuma notification: ${e}", Toast.LENGTH_LONG).show()
            Log.d("carai", "falhou na destruiçao: ${e}")
        }
    }
}