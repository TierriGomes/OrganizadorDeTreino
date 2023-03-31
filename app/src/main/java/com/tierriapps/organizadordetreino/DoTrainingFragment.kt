package com.tierriapps.organizadordetreino

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tierriapps.organizadordetreino.adapters.AdapterShowDivisions
import com.tierriapps.organizadordetreino.adapters.CustomizedLayoutManager
import com.tierriapps.organizadordetreino.data.models.MyDivision
import com.tierriapps.organizadordetreino.data.models.MyTraining
import com.tierriapps.organizadordetreino.data.repository.MyRoomDataSource
import com.tierriapps.organizadordetreino.data.repository.MyViewModel
import com.tierriapps.organizadordetreino.data.repository.MyViewModelFactory
import com.tierriapps.organizadordetreino.data.roomdb.AppDataBase
import com.tierriapps.organizadordetreino.data.roomdb.MyTrainingDao
import com.tierriapps.organizadordetreino.databinding.FragmentDoTrainingBinding
import com.tierriapps.organizadordetreino.notifications.NotificationDoTraining


class DoTrainingFragment : Fragment() {
    // VARIAVEIS DE LAYOUT
    private lateinit var binding: FragmentDoTrainingBinding
    private lateinit var adapter: AdapterShowDivisions
    private lateinit var recyclerView: RecyclerView
    private var notificationUtils: NotificationDoTraining? = null

    // VARIAVEIS DE DADOS
    private lateinit var trayningDao: MyTrainingDao
    private lateinit var myViewModel: MyViewModel
    private lateinit var sharedPreferencesNotification: SharedPreferences
    private lateinit var sharedPreferencesID: SharedPreferences
    private var trainingId = 0
    private var myTraining: MyTraining? = null
    private var division: MyDivision? = null
    private var divisionChoicedNumber: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentDoTrainingBinding.inflate(inflater, container, false)

        // CONFIGS DE DADOS
        trayningDao = AppDataBase.getDatabase(requireContext()).myTrainingDao
        val repository = MyRoomDataSource(trayningDao)
        myViewModel = ViewModelProvider(requireActivity(), MyViewModelFactory(repository))[MyViewModel::class.java]
        sharedPreferencesNotification = requireContext().getSharedPreferences(
            "NOTIFICATION_DATA", Context.MODE_PRIVATE)
        sharedPreferencesID = requireContext().getSharedPreferences("TRAINING_ID", Context.MODE_PRIVATE)
        trainingId = sharedPreferencesID.getInt("id", 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView
        val buttonlist = listOf<Button>(
            binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6, binding.button7)

        myViewModel.getById(trainingId.toByte()).observe(viewLifecycleOwner) { training ->
            myTraining = training

            // CONFIG DOS BOTOES INICAIS DE ACORDO COM A QUANTIDADE DE DIVISOES DO TREINO
            for (c in 0 until training.divisionsList.size){
                buttonlist[c].visibility = View.VISIBLE
                buttonlist[c].setOnClickListener {
                    binding.linearLayout.visibility = View.GONE
                    binding.buttonNotifycation.visibility = View.VISIBLE
                    binding.buttonSaveAndQuit.visibility = View.VISIBLE
                    recyclerView.visibility = View.VISIBLE

                    division = training.divisionsList[c]
                    divisionChoicedNumber = c
                    adapter = AdapterShowDivisions(mutableListOf(division!!), istraining = true)
                    recyclerView.layoutManager = CustomizedLayoutManager(requireContext())
                    recyclerView.adapter = adapter
                }
            }
        }
        binding.buttonNotifycation.setOnClickListener {
            it.visibility = View.GONE
            binding.buttonGetData.visibility = View.VISIBLE
            notificationUtils = NotificationDoTraining(requireContext(), division!!)
            notificationUtils?.createNotification()
        }
        binding.buttonSaveAndQuit.setOnClickListener {
            buttonSaveAndQuitAction()
        }
        binding.buttonGetData.setOnClickListener {
            buttonGetDataClickAction()
        }
    }

    private fun buttonSaveAndQuitAction() {
        val snackbar = Snackbar.make(binding.root, "confirm saving", Snackbar.LENGTH_LONG)
        snackbar.setAction("YES"){
            do {var finish = false
                try {
                    division = adapter.returndivisionsList()[0]
                    myViewModel.update(myTraining!!)
                    finish = true
                    Toast.makeText(requireContext(), "Treino Salvo", Toast.LENGTH_SHORT).show()
                }catch (e: java.lang.Exception){
                    if (divisionChoicedNumber == null){
                        finish = true
                    }
                }
            }while (!finish)
            val navController = findNavController()
            navController.navigate(R.id.action_doTrainingFragment_to_mainFragment)
        }
        snackbar.show()
    }

    private fun buttonGetDataClickAction() {
        // OBTEM A LISTA DE REPETIÇOES QUE A NOTIFICATION SALVOU NO SHAREDPREFERENCES
        val stringList = sharedPreferencesNotification.getString("stringList", "")
        val gson = Gson()
        val lists = gson.fromJson(stringList, object : TypeToken<List<List<Float>>>() {}.type
        ) as MutableList<MutableList<Float>>

        // SE A LISTA ESTIVER OK, PASSA OS DADOS DELA PRA A POSIÇÃO CORRETA NA DIVISION SE A MESMA
        // TAMBEM ESTIVER OK
        if (lists.isNotEmpty() && division != null) {
            for (ex in division!!.exercicesList.withIndex()) {
                ex.value.repsOfDay[ex.value.repsOfDay.count()] = lists[ex.index]
            }
            adapter = AdapterShowDivisions(mutableListOf(division!!), true, true)
            recyclerView.adapter = adapter
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        try{
            notificationUtils!!.onDestroyNotification()
        }catch (e: Exception){
            Log.d("mytag", e.toString())
        }
    }
}
