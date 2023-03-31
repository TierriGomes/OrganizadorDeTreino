package com.tierriapps.organizadordetreino.data.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tierriapps.organizadordetreino.data.models.MyDivision

@Entity("my_training")
data class MyTrainingEntity(
    @PrimaryKey(autoGenerate = true) val id: Byte = 0,
    @ColumnInfo("trining_name") val name: String,
    @ColumnInfo("divisions_list") val divisionsList: MutableList<MyDivision>,
    @ColumnInfo("descrition") val descrition: String
)

class DivisionsListConverter(){
    @TypeConverter
    fun toGson(divisionsList: MutableList<MyDivision>): String{
        return Gson().toJson(divisionsList)
    }
    @TypeConverter
    fun fromGson(string: String): MutableList<MyDivision>{
        if(string == ""){
            return mutableListOf()
        }
        val type = object : TypeToken<MutableList<MyDivision>>() {}.type
        return Gson().fromJson(string, type)
    }
}
