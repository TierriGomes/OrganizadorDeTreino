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
import com.tierriapps.organizadordetreino.adapters.AdapterShowTrainings
import com.tierriapps.organizadordetreino.adapters.CustomizedLayoutManager
import com.tierriapps.organizadordetreino.data.models.MyTraining
import com.tierriapps.organizadordetreino.data.repository.MyRoomDataSource
import com.tierriapps.organizadordetreino.data.repository.MyViewModel
import com.tierriapps.organizadordetreino.data.repository.MyViewModelFactory
import com.tierriapps.organizadordetreino.data.roomdb.AppDataBase
import com.tierriapps.organizadordetreino.data.roomdb.MyTrainingDao
import com.tierriapps.organizadordetreino.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    //DB VARIABLES
    private lateinit var myDAO: MyTrainingDao
    private lateinit var myViewModel: MyViewModel
    private var myTraining: MyTraining? = null
    private lateinit var sharedPreferencesID: SharedPreferences
    private var trainingId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        //DB SETTINGS
        myDAO = AppDataBase.getDatabase(requireContext()).myTrainingDao
        val repository = MyRoomDataSource(myDAO)
        myViewModel = ViewModelProvider(requireActivity(), MyViewModelFactory(repository))[MyViewModel::class.java]
        sharedPreferencesID = requireContext().getSharedPreferences("TRAINING_ID", Context.MODE_PRIVATE)
        trainingId = sharedPreferencesID.getInt("id", 0)

        return binding.root
    }

    override fun onResume() {
        try {
            val recyclerView = binding.recyclerviewMain
            val adapter = AdapterShowTrainings()
            myViewModel.getById(trainingId.toByte()).observe(viewLifecycleOwner) { iten ->
                Log.d("mytag", "mainfragment")
                adapter.getTrainings(listOf(iten))
            }
            recyclerView.layoutManager = CustomizedLayoutManager(binding.root.context)
            recyclerView.adapter = adapter
        }catch (e: Exception){
            Log.d("mytag", e.toString())
        }
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}