package com.tierriapps.organizadordetreino.adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import com.tierriapps.organizadordetreino.data.models.MyTraining
import com.tierriapps.organizadordetreino.data.repository.MyViewModel
import com.tierriapps.organizadordetreino.databinding.RecyclerviewChoosetrainigsBinding

class AdapterChooseTraining(
    val listOfTrainings: List<MyTraining>? = listOf(),
    val viewModel: MyViewModel): RecyclerView.Adapter<ViewHolder>() {
    private val listOfButtons = mutableListOf<ImageView>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerviewChoosetrainigsBinding.inflate(inflater)
        return MyViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return if (listOfTrainings != null) listOfTrainings.size else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myViewHolder = holder as MyViewHolder
        myViewHolder.bind(listOfTrainings!![position], position)
    }

    inner class MyViewHolder(view: View): ViewHolder(view){
        private val binding = RecyclerviewChoosetrainigsBinding.bind(view)
        private val buttonDelete = binding.imageViewDeleteTraining
        private val buttonSelect = binding.imageViewSelectTraining
        private val trainingName = binding.textViewTrainingName
        private val trainingDescription = binding.textViewTrainingDescrition
        private val sharedPreferences = binding.root.context.getSharedPreferences("TRAINING_ID", Context.MODE_PRIVATE)

        init {
            listOfButtons.add(buttonSelect)
        }

        fun bind(training: MyTraining, position: Int){
            var id = sharedPreferences.getInt("id", 0)
            trainingName.text = training.name
            trainingDescription.text = training.descrition
            if(training.id == id.toByte()){
                buttonSelect.setImageResource(android.R.drawable.btn_star_big_on)
            }else {
                buttonSelect.setImageResource(android.R.drawable.btn_star_big_off)
            }
            // CONFIG DOS CLICKS NOS BOTOES
            buttonSelect.setOnClickListener {
                sharedPreferences.edit().putInt("id", training.id.toInt()).apply()
                Toast.makeText(binding.root.context, "Selecionou: ${training.name}", Toast.LENGTH_SHORT).show()
                for (b in listOfButtons){
                    if (b == it){
                        b.setImageResource(android.R.drawable.btn_star_big_on)
                    }else {
                        b.setImageResource(android.R.drawable.btn_star_big_off)
                    }
                }
            }
            buttonDelete.setOnClickListener {
                val snackbar = Snackbar.make(binding.root, "Deseja mesmo deletar?", Snackbar.LENGTH_SHORT)
                snackbar.setAction("YES"){
                    viewModel.deleteById(training.id)
                    Thread.sleep(100)
                    viewModel.getAll()
                }
                snackbar.show()
            }
        }


    }
}