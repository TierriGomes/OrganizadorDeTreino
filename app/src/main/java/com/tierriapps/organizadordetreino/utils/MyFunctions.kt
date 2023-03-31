package com.tierriapps.organizadordetreino.utils

fun fromByteToCharName(int: Int): Char{
    when(int){
        0 -> return 'A'
        1 -> return 'B'
        2 -> return 'C'
        3 -> return 'D'
        4 -> return 'E'
        5 -> return 'F'
        6 -> return 'G'
    }
    return 'Y'
}