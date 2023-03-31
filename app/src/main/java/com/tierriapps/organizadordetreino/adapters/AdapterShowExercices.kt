package com.tierriapps.organizadordetreino.adapters

import android.annotation.SuppressLint
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.tierriapps.organizadordetreino.data.models.MyExercice
import com.tierriapps.organizadordetreino.databinding.RecyclerviewExerciceBinding

class AdapterShowExercices(val exerciceList: MutableList<MyExercice>,
                           val modeCreator: Boolean,
                           val istraining: Boolean,
                           val modeEditor: Boolean,
                           val viewDay: TextView? = null): RecyclerView.Adapter<ViewHolder>() {
    val horizontalRecyclerViews: MutableList<RecyclerView> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerviewExerciceBinding.inflate(inflater)
        return MyViewHolder(binding.root, modeCreator = modeCreator, istraining = istraining, modeEditor = modeEditor)
    }

    override fun getItemCount(): Int {
        return exerciceList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val myViewHolder = holder as MyViewHolder
        myViewHolder.bind(exerciceList[position])

        if(!modeCreator && !istraining || modeEditor && exerciceList[position].repsOfDay.isNotEmpty()){
            // Adicione a RecyclerView interna à lista
            val innerRecyclerView = myViewHolder.recyclerView
            horizontalRecyclerViews.add(innerRecyclerView)

            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(innerRecyclerView)

            // Configure o comportamento de rolagem
            innerRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    // Obtenha a posição de rolagem da primeira RecyclerView
                    val firstRecyclerView = horizontalRecyclerViews[position]
                    val scrollX = firstRecyclerView.computeHorizontalScrollOffset()
                    // Atualize a posição de rolagem de todas as outras RecyclerViews
                    for (view in horizontalRecyclerViews) {
                        view.scrollBy(scrollX - view.computeHorizontalScrollOffset(), 0)
                    }
                    if(viewDay != null){
                        val ddd = myViewHolder.recyclerView.layoutManager as LinearLayoutManager
                        val aaa = ddd.findFirstCompletelyVisibleItemPosition()+1
                        if(aaa != 0){
                            viewDay.text = "Day $aaa"
                        }
                    }
                }
            })
        }
    }

    class MyViewHolder(view: View,
                       val modeCreator: Boolean,
                       val istraining: Boolean,
                       val modeEditor: Boolean): ViewHolder(view){
        val binding = RecyclerviewExerciceBinding.bind(view)
        val recyclerView = binding.recyclerviewReps
        val exerciceName = binding.edittextExerciceName
        val series = binding.edittextSeries
        val rest = binding.edittextRest

        init {
            if(!modeCreator && !modeEditor || istraining){
                exerciceName.isEnabled = false
                series.isEnabled = false
                rest.isEnabled = false
            }
        }

        fun bind(exercice: MyExercice){
            exerciceName.setText(exercice.exerciceName)
            series.setText(if(exercice.series?.toInt() == 0)"" else "${exercice.series} x")
            rest.setText(if(exercice.rest?.toInt() == 0)"" else "rest: ${exercice.rest}")
            val adapter = AdapterShowReps(exercice.repsOfDay, exercice.series?:1,
                istraining = istraining,
                modeCreator = if(modeEditor) modeEditor else modeCreator)
            val layoutManager = CustomizedLayoutManager(binding.root.context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter

            if(modeCreator || modeEditor){
                series.setText(if(exercice.series?.toInt() == 0)"" else "${exercice.series}")
                rest.setText(if(exercice.rest?.toInt() == 0)"" else "${exercice.rest}")

                exerciceName.setOnKeyListener { view, i, keyEvent ->
                    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                        series.requestFocus()
                        return@setOnKeyListener true
                    }
                    return@setOnKeyListener false
                }
                series.setOnKeyListener { view, i, keyEvent ->
                    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                        rest.requestFocus()
                        return@setOnKeyListener true
                    }
                    return@setOnKeyListener false

                }
                rest.setOnKeyListener { view, i, keyEvent ->
                    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                        if(exerciceName.text.toString() == "" || series.text.toString() == "" || rest.text.toString() == ""){
                            Toast.makeText(binding.root.context, "nao fode irmao, preenche tudo ae", Toast.LENGTH_LONG).show()
                        }else {
                            exerciceName.isEnabled = false
                            series.isEnabled = false
                            rest.isEnabled = false

                            exercice.exerciceName = exerciceName.text.toString().trim()
                            exercice.series = series.text.toString().toByteOrNull() ?: 0
                            if (exercice.series!! > 6){
                                exercice.series = 6
                                Toast.makeText(binding.root.context,
                                    "Max number of series is six per exercice\n add a new exercice",
                                Toast.LENGTH_SHORT).show()
                                series.isEnabled = true
                                rest.isEnabled = true
                            }
                            exercice.rest = rest.text.toString().toShortOrNull() ?: 0
                            if (exercice.rest!! > 300){
                                exercice.rest = 300
                                Toast.makeText(binding.root.context,
                                "Max time of rest is 300 seconds\n are you sleeping during the training?",
                                Toast.LENGTH_LONG).show()
                                rest.isEnabled = true
                            }
                            return@setOnKeyListener true
                        }
                    }
                    return@setOnKeyListener false
                }
            }
        }
    }
}