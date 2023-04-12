package com.tierriapps.organizadordetreino.adapters

import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import com.tierriapps.organizadordetreino.databinding.RecyclerviewRepsBinding

class AdapterShowReps(val mapOfReps: MutableMap<Int, MutableList<String>> = mutableMapOf(),
                      val series: Byte, val istraining: Boolean = false,
                      val modeCreator: Boolean = false): RecyclerView.Adapter<ViewHolder>() {
    init {
        Log.d("taag", "istraining: $istraining modecrator $modeCreator")
        if(istraining && !modeCreator){
            mapOfReps[mapOfReps.count()+1] = mutableListOf()
            Log.d("taaaaaag", "criou essa poha")
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerviewRepsBinding.inflate(inflater)
        val myViewHolder = MyViewHolder(binding.root, series, istraining, modeCreator)
        return myViewHolder
    }

    override fun getItemCount(): Int {
        return if (istraining || modeCreator) 1 else mapOfReps.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myViewHolder = holder as MyViewHolder
        if (mapOfReps.count() > 0){
            if(istraining || modeCreator){
                myViewHolder.bind(mapOfReps[mapOfReps.count()]!!, mapOfReps)
            }else {
                myViewHolder.bind(mapOfReps[position + 1], mapOfReps)
            }
        }else{
            myViewHolder.bind(mutableListOf("", "", "", "", "", ""), mapOfReps)
        }
    }
    inner class MyViewHolder(view: View,
                       val series: Byte,
                       val istraining: Boolean,
                       val modeCreator: Boolean): ViewHolder(view){
        val binding = RecyclerviewRepsBinding.bind(view)
        val textList = listOf<EditText>(binding.editTextNumber1, binding.editTextNumber2,
                                        binding.editTextNumber3, binding.editTextNumber4,
                                        binding.editTextNumber5, binding.editTextNumber6)

        // INICIALIZAÇÃO PADRÃO DEIXANDO AS ET VISIVIEIS DE ACORDO COM A QUANTIDADE DE SERIES
        // E DASABILITANDO-AS APENAS PARA SEREM MOSTRADAS
        init {
            for(i in 0  until series){
                textList[i].visibility = View.VISIBLE
                if(!modeCreator){
                    textList[i].isEnabled = false
                }
            }
        }

        // BINDANDO OS ITENS PELA LISTA
        fun bind(list: MutableList<String>?, mapOfReps: MutableMap<Int, MutableList<String>>){
            if(!istraining && list != null) {
                for (c in list.withIndex()) {
                    textList[c.index].setText(c.value.toString())
                }
            }
            else if (istraining){
                for(c in 0  until series){

                    if(list!!.size > c){
                        textList[c].setText(list!![c].toString())
                    }else{
                        list!!.add("0")
                    }
                    textList[c].isEnabled = true
                    textList[c].setOnKeyListener { view, i, keyEvent ->
                        if(i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP){
                            textList[c].isEnabled = false
                            list[c] = (textList[c].text.toString())
                            return@setOnKeyListener true
                        }
                        return@setOnKeyListener false
                    }
                }
            }
            if (modeCreator && !istraining){
                for(c in 0  until series){
                    textList[c].isEnabled = true
                    textList[c].requestFocus()
                    textList[c].isActivated = true
                    textList[c].setOnKeyListener { view, i, keyEvent ->
                        if(i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP){

                            list?.set(c, textList[c].text.toString())
                            textList[c].isEnabled = false
                            return@setOnKeyListener true
                        }
                        return@setOnKeyListener false
                    }
                }
            }
        }
    }
}