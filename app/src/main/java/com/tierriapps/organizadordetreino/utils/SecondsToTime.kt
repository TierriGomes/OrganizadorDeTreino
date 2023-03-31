package com.tierriapps.organizadordetreino.utils

class SecondsToTime {

    companion object {
        fun segundosParaHorario(segundos: Int): String {
            val horas = segundos / 3600
            val minutos = (segundos % 3600) / 60
            val segundosRestantes = segundos % 60

            return "%02d:%02d:%02d".format(horas, minutos, segundosRestantes)
        }
    }
}