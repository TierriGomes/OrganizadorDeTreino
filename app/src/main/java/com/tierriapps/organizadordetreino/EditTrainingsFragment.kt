package com.tierriapps.organizadordetreino

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.tierriapps.organizadordetreino.adapters.AdapterShowTrainings
import com.tierriapps.organizadordetreino.adapters.CustomizedLayoutManager
import com.tierriapps.organizadordetreino.data.repository.MyRoomDataSource
import com.tierriapps.organizadordetreino.data.repository.MyViewModel
import com.tierriapps.organizadordetreino.data.repository.MyViewModelFactory
import com.tierriapps.organizadordetreino.data.roomdb.AppDataBase
import com.tierriapps.organizadordetreino.databinding.FragmentEditTrainingsBinding

class EditTrainingsFragment : Fragment() {
    private lateinit var binding: FragmentEditTrainingsBinding

    private lateinit var myViewModel: MyViewModel
    private lateinit var sharedPreferencesID: SharedPreferences
    private var trainingId = 0
    private lateinit var recyclerview: RecyclerView
    private lateinit var adapter: AdapterShowTrainings

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditTrainingsBinding.inflate(inflater, container, false)

        //DB CONFFIG
        val repository = MyRoomDataSource(AppDataBase.getDatabase(requireContext()).myTrainingDao)
        myViewModel = ViewModelProvider(
            requireActivity(),
            MyViewModelFactory(repository))[MyViewModel::class.java]
        sharedPreferencesID = requireContext().getSharedPreferences("TRAINING_ID", Context.MODE_PRIVATE)
        trainingId = sharedPreferencesID.getInt("id", 0)

        //RECYCLER VIEW CONFFIG
        recyclerview = binding.recyclerviewEdit
        recyclerview.layoutManager = CustomizedLayoutManager(requireContext())
        adapter = AdapterShowTrainings(modeEditor = true)
        recyclerview.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.getById(trainingId.toByte()).observe(viewLifecycleOwner){myTraining ->
            adapter.getTrainings(listOf(myTraining))
        }
        binding.buttonSave.setOnClickListener {
            val myTraning = adapter.returnTraining()[0]
            myViewModel.update(myTraning)
            Toast.makeText(requireContext(), "Feito", Toast.LENGTH_SHORT).show()
        }


    }
}