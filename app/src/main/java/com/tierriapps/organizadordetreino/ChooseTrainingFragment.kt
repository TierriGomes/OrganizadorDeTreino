package com.tierriapps.organizadordetreino

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tierriapps.organizadordetreino.adapters.AdapterChooseTraining
import com.tierriapps.organizadordetreino.data.models.MyTraining
import com.tierriapps.organizadordetreino.data.repository.MyRoomDataSource
import com.tierriapps.organizadordetreino.data.repository.MyViewModel
import com.tierriapps.organizadordetreino.data.repository.MyViewModelFactory
import com.tierriapps.organizadordetreino.data.roomdb.AppDataBase
import com.tierriapps.organizadordetreino.data.roomdb.MyTrainingDao
import com.tierriapps.organizadordetreino.databinding.FragmentChooseTrainingBinding


class ChooseTrainingFragment : Fragment() {
    private lateinit var binding: FragmentChooseTrainingBinding
    private lateinit var recyclerView: RecyclerView

    //DB VARIABLES
    private lateinit var myDAO: MyTrainingDao
    private lateinit var myViewModel: MyViewModel
    private var myTraining: MyTraining? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseTrainingBinding.inflate(inflater, container, false)

        myDAO = AppDataBase.getDatabase(requireContext()).myTrainingDao
        val repository = MyRoomDataSource(myDAO)
        myViewModel = ViewModelProvider(requireActivity(), MyViewModelFactory(repository))[MyViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerViewChooseTraining
        recyclerView.layoutManager = LinearLayoutManager(context)
        myViewModel.getAll()
        myViewModel.myTrainings.observe(viewLifecycleOwner){ listOfMyTrainings ->
            if (listOfMyTrainings != null){
                val adapter = AdapterChooseTraining(listOfMyTrainings, myViewModel)
                recyclerView.adapter = adapter
            }
        }
    }
}