package com.tierriapps.organizadordetreino

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tierriapps.organizadordetreino.adapters.AdapterShowTrainings
import com.tierriapps.organizadordetreino.adapters.CustomizedLayoutManager
import com.tierriapps.organizadordetreino.data.models.MyTraining
import com.tierriapps.organizadordetreino.data.repository.MyRoomDataSource
import com.tierriapps.organizadordetreino.data.repository.MyViewModel
import com.tierriapps.organizadordetreino.data.repository.MyViewModelFactory
import com.tierriapps.organizadordetreino.data.roomdb.AppDataBase
import com.tierriapps.organizadordetreino.data.roomdb.MyTrainingDao
import com.tierriapps.organizadordetreino.databinding.FragmentNewTrainingBinding

class NewTrainingFragment : Fragment() {
    private lateinit var binding: FragmentNewTrainingBinding

    //DB VARIABLES
    private lateinit var myDao: MyTrainingDao
    private lateinit var myViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentNewTrainingBinding.inflate(inflater, container, false)

        //DB SETTINGS
        myDao = AppDataBase.getDatabase(requireContext()).myTrainingDao
        val repository = MyRoomDataSource(myDao)
        myViewModel = ViewModelProvider(requireActivity(), MyViewModelFactory(repository))[MyViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.recyclerviewShow
        val adapter = AdapterShowTrainings(modeCreator = true)
        adapter.getTrainings()
        recyclerView.layoutManager = CustomizedLayoutManager(binding.root.context)
        recyclerView.adapter = adapter
        binding.buttonSave.setOnClickListener {
            val iten = adapter.returnTraining()[0]
            if(vasculhaItem(iten)){
                myViewModel.insert(iten)
                val nav = findNavController()
                nav.navigate(R.id.action_newTrainingFragment_to_mainFragment)
            }
        }
    }

    private fun vasculhaItem(iten: MyTraining): Boolean {
        if(iten.descrition.trim() == "" || iten.name.trim() == ""){
            Log.d("erro", "primeiro bloco")
            return false
        }
        for(i in iten.divisionsList){
            if (i.descrition == ""){
                Log.d("erro", "segundo bloco")
                return false
            }
            for (e in i.exercicesList){
                if (e.rest?.toInt() == 0 || e.series?.toInt() == 0 || e.exerciceName == ""){
                    Log.d("erro", "terceiro bloco${e.toString()} eee ${i.exercicesList.indexOf(e)}")
                    return false
                }
            }
        }
        return true
    }
}