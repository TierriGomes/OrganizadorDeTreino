package com.tierriapps.organizadordetreino.adapters

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import com.tierriapps.organizadordetreino.data.models.MyDivision
import com.tierriapps.organizadordetreino.data.models.MyExercice
import com.tierriapps.organizadordetreino.databinding.RecyclerviewShowdivisionsBinding

class AdapterShowDivisions(
    val divisionsList: MutableList<MyDivision>,
    val modeCreator: Boolean = false,
    val istraining: Boolean = false,
    val modeEditor: Boolean = false): RecyclerView.Adapter<ViewHolder>() {

    // CRIA O VIEWHOLDER COM OS ATRIBUTOS NECESSARIOS PARA AS CONFIGURAÇOES SE VAI SER UM TREINO,
    // MODE CREATOR, ETC
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerviewShowdivisionsBinding.inflate(inflater)
        return MyViewHolder(binding.root, modeCreator = modeCreator,istraining = istraining, modeEditor = modeEditor)
    }

    // RETORNA O TAMANHO DA LISTA DIVISIONS
    override fun getItemCount(): Int {
        return divisionsList.size
    }

    // BINDA AS INFORMAÇOES DE CADA DIVISION NA RECYCLER VIEW
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myViewHolder = holder as MyViewHolder
        myViewHolder.bind(divisionsList[position])
    }

    // RETORNA A LISTA DE DIVISIONS, E USADO PELO PRINCIPALMENTE PARA ATUALIZAR ALTERÇOES EM UMA UNICA DIVISION
    fun returndivisionsList() = divisionsList

    class MyViewHolder(
        view: View,
        val modeCreator: Boolean,
        val istraining: Boolean,
        val modeEditor: Boolean): ViewHolder(view){

        val binding = RecyclerviewShowdivisionsBinding.bind(view)
        val charName = binding.textviewDivisionName
        val descrition = binding.edittextDescrition
        val buttonAdd = binding.imagebuttonAdd
        val buttonDelete = binding.imagebuttonDelete
        val recyclerView = binding.recyclerviewShowExercices
        val rest = binding.textviewRest
        var mostrarDados = true

        init {
            // CONFIG PRA QUANDO ESTIVER NO MODO SHOW OU NO MODO TRAINING
            if (!modeCreator){
                buttonAdd.visibility = View.GONE
                buttonDelete.visibility = View.GONE
                descrition.isEnabled = false
                rest.setText("Day 0")
            }

            // CONFIG PRA QUANDO ESTIVER SOMENTE NO MODO SHOW
            // AQUI ADICIONAMOS UM BOTAO PRA MOSTRAR OU NAO A RECYCLER VIEW COM EXERCICIOS
            if (!modeCreator && !istraining){
                charName.setOnClickListener {
                    if(mostrarDados){
                        recyclerView.visibility = View.GONE
                        binding.linearLayout2.visibility = View.GONE
                        mostrarDados = false
                    }
                    else{
                        recyclerView.visibility = View.VISIBLE
                        binding.linearLayout2.visibility = View.VISIBLE
                        mostrarDados = true
                    }
                }
            }
            //  CONFIG PARA QUANDO E UM DO TRAINING COM MODECREATOR TRUE
            if(istraining){
                descrition.isEnabled = false
                buttonDelete.visibility = View.GONE
                buttonAdd.visibility = View.GONE
                binding.textviewRest.setText("Descanço")
            }
        }
        fun bind(myDivision: MyDivision) {
            // CONFIGURAÇOES BASICAS PRA BINDAR EM QUALQUER SITUAÇÃO
            // AQUI TEM AS CONFIGURAÇOES DA RECYCLER VIEW QUE EXIBIRA OS EXERCICIOS
            charName.text = myDivision.charName.toString()
            val adapter = AdapterShowExercices(myDivision.exercicesList,
                modeCreator = modeCreator,
                istraining = istraining,
                modeEditor = modeEditor,
                viewDay = rest)
            recyclerView.layoutManager = CustomizedLayoutManager(binding.root.context)
            recyclerView.adapter = adapter
            descrition.setText(myDivision.descrition)

            // SE FOR MODO CREATOR PRECISAMOS DE BOTOES PARA ADICIONAR E REMOVER EXERCICIOS
            if(modeCreator || modeEditor){
                // CRIA E ADICIONA UM EXERCICIO ZERADO
                buttonAdd.setOnClickListener {
                    myDivision.exercicesList.add(MyExercice("", 0, 0))
                    adapter.notifyItemInserted(myDivision.exercicesList.size)
                }

                // DELETA O ULTIMO EXERCICIO DA LISTA OU SE MODE EDITOR DELETA O ULTIMO EXERCICIO
                if (modeEditor){
                    buttonDelete.visibility = View.VISIBLE
                }
                buttonDelete.setOnClickListener {
                    if (modeEditor){
                        val snackbar = Snackbar.make(binding.root, "deseja mesmo deletar?", Snackbar.LENGTH_LONG)
                        snackbar.setAction("YES"){
                            var c = 0
                            for(ex in myDivision.exercicesList){
                                if (ex.repsOfDay.isNotEmpty()){
                                    ex.repsOfDay.remove(ex.repsOfDay.size)
                                    adapter.horizontalRecyclerViews[c].adapter!!.notifyDataSetChanged()
                                    c++
                                }
                            }

                            Toast.makeText(binding.root.context, "feito", Toast.LENGTH_SHORT)
                            }
                        snackbar.show()
                    }else {
                        try {
                            myDivision.exercicesList.removeLast()
                            adapter.notifyItemRemoved(myDivision.exercicesList.size + 1)
                        }catch (a: Exception){
                            Toast.makeText(
                                binding.root.context, "Erro ao remover", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // ADICIONA UMA DESCRIÇAO PRA DIVISION
                descrition.setOnKeyListener { view, i, keyEvent ->
                    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                        myDivision.descrition = descrition.text.toString()
                        descrition.isEnabled = false
                        return@setOnKeyListener false
                    }
                    return@setOnKeyListener true
                }
            }
        }
    }
}