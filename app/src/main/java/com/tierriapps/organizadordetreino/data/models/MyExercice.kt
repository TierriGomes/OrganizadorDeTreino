package com.tierriapps.organizadordetreino.data.models

data class MyExercice(
    var exerciceName: String,
    var series: Byte?,
    var rest: Short?,
    val repsOfDay: MutableMap<Int, MutableList<String>> = mutableMapOf()
)