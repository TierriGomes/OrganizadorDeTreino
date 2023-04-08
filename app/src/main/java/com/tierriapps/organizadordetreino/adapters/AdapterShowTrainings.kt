package com.tierriapps.organizadordetreino.adapters

import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import com.tierriapps.organizadordetreino.data.models.MyDivision
import com.tierriapps.organizadordetreino.data.models.MyTraining
import com.tierriapps.organizadordetreino.databinding.RecyclerviewTrainingsBinding
import com.tierriapps.organizadordetreino.utils.fromByteToCharName

class AdapterShowTrainings(
    val modeCreator: Boolean = false,
    val istraining: Boolean = false,
    val modeEditor: Boolean = false) : RecyclerView.Adapter<ViewHolder>() {
    var trainingsList = mutableListOf<MyTraining>()

    // CRIA O VIEWHOLDER E INFORMA SE VAI SER NO modeCreator OU istraining
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerviewTrainingsBinding.inflate(inflater)
        return MyViewHolder(binding.root, modeCreator, istraining, modeEditor)
    }
    // RETORNA O TAMANHO DA LISTA DE TREINOS PARA A EXIBIÇÃO,
    // MEIO DESNECESSARIO JA QUE SEMPRE EXIBIMOS APENAS UM TREINO POR VEZ
    override fun getItemCount(): Int {
        return trainingsList.size
    }

    // USA O myViewHolder.bind() PARA ATUALIZAR OS DADOS DE CADA VIEWHOLDER DE ACORDO COM A LISTA
    // DE EXERCICIOS
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myViewHolder = holder as MyViewHolder
        myViewHolder.bind(trainingsList[position], position)
    }

    // RECEBE UMA LISTA DE MyTraining ADICIONA A  trainingsList E NOTIFICA AS MUDANÇAS
    fun getTrainings(trainings:List<MyTraining> = listOf(MyTraining(name = "", descrition = ""))){
        this.trainingsList.addAll(trainings)
        notifyItemInserted(trainingsList.lastIndex)
        notifyItemChanged(trainingsList.lastIndex)
    }

    // USADA PARA RETORNAR A LISTA NA ACTIVITY COM OS DADOS ATUALIZADOS
    fun returnTraining() = trainingsList

    // CLASSE PARA GERAR OS VIEWHOLDERS, ELA PRECISA SABER SE IRA GERAR OS HOLDERS NO MODO DE EXIBIÇÃO
    // CRIAÇÃO, OU TREINO, PARA CONFIGURAR CORRETAMENTE A UI
    class MyViewHolder(view: View,
                       val modeCreator: Boolean,
                       val istraining: Boolean,
                       val modeEditor: Boolean):ViewHolder(view){
        val binding = RecyclerviewTrainingsBinding.bind(view)
        val trainingName = binding.textviewTrainingName
        val trainingDescrition = binding.textviewTrainingDescrition
        val recyclerview = binding.recyclerviewDivisions
        val buttonAdd = binding.buttonNewDivision
        val buttonDelete = binding.buttonDeleteLastDivision

        // SE NÃO FOR MODO CRIAÇÃO, NÃO PRECISA RECEBER INPUTS DO USUARIO,
        // ENTAO OS EDITTEXT E BOTOES SAO DESABILITADOS (NO XML ELES VEM HABILITADOS POR PADRÃO)
        init {
            if(!modeCreator){
                buttonAdd.visibility = View.GONE
                buttonDelete.visibility = View.GONE
                trainingName.isEnabled = false
                trainingDescrition.isEnabled = false
            }
        }

        fun bind(myTraining: MyTraining, position: Int){
            // CONFIGURAÇÕES DA RECYCLERVIEW QUE EXIBIRA AS DIVISIONS
            val adapter = AdapterShowDivisions(myTraining.divisionsList, modeCreator, istraining, modeEditor)
            recyclerview.layoutManager = CustomizedLayoutManager(binding.root.context)
            recyclerview.adapter = adapter

            if(myTraining.name != ""){
                trainingName.setText(myTraining.name)
                trainingDescrition.setText(myTraining.descrition)
            }

            // CONFIGS SE ESTIVER NO MODO CRIAÇÃO
            if(modeCreator) {
                // CRIA UMA NOVA DIVISION E ADICIONA NA LISTA DE DIVISIONS DO OBJETO MYTRAINING
                buttonAdd.setOnClickListener {
                    myTraining.divisionsList.add(
                        MyDivision(fromByteToCharName(myTraining.divisionsList.size))
                    )
                    adapter.notifyItemInserted(myTraining.divisionsList.lastIndex)
                }

                // DELETA A ULTIMA DIVISÃO ADICIONADA
                buttonDelete.setOnClickListener {
                    autorizationFromSnackBar {
                        try {
                            myTraining.divisionsList.removeLast()
                            adapter.notifyItemRemoved(myTraining.divisionsList.lastIndex + 1)
                        } catch (e: Exception) {
                            Toast.makeText(
                                binding.root.context, "nothing to delete", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                // SALVA 0 NOME DO TREINO CRIADO PELO USUARIO E PULA PARA O DESCRITION
                trainingName.setOnKeyListener { view, i, keyEvent ->
                    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                        trainingName.isEnabled = false
                        myTraining.name = trainingName.text.toString()
                        trainingDescrition.requestFocus()
                        return@setOnKeyListener false
                    } else {
                        return@setOnKeyListener true
                    }
                }

                // SALVA A DESCRIÇÃO CRIADA PELO USUARIO
                trainingDescrition.setOnKeyListener { view, i, keyEvent ->
                    if(i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP){
                        trainingDescrition.isEnabled = false
                        myTraining.descrition = trainingDescrition.text.toString()
                        return@setOnKeyListener false
                    }else {
                        return@setOnKeyListener true
                    }
                }
            }
        }
        // GERA UMA SNACK BAR PRA RECEBER A CONFIRMAÇAO DE DELETE DO USUARIO
        private fun autorizationFromSnackBar(callback: () -> Unit) {
            val snackBar = Snackbar.make(binding.root, "Are you sure?", Snackbar.LENGTH_LONG)
                .setAction("Yes") {
                    callback()
                }
            snackBar.show()
        }
    }
}