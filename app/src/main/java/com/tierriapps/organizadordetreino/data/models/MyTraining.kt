package com.tierriapps.organizadordetreino.data.models

data class MyTraining (
    val id: Byte = 0,
    var name: String,
    var divisionsList: MutableList<MyDivision> = mutableListOf(),
    var descrition: String = ""
)