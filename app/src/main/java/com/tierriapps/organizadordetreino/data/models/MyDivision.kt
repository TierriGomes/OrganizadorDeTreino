package com.tierriapps.organizadordetreino.data.models

data class MyDivision(
    val charName: Char = 'z',
    var descrition: String = "",
    val exercicesList: MutableList<MyExercice> = mutableListOf()
)