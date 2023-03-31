package com.tierriapps.organizadordetreino.data.repository


import androidx.lifecycle.LiveData
import com.tierriapps.organizadordetreino.data.models.MyTraining

interface MyRepository {

    suspend fun insert(myTraining: MyTraining)

    suspend fun deleteById(id: Byte)

    suspend fun update(myTraining: MyTraining)

    suspend fun getLastItem(): MyTraining?

    suspend fun getById(id: Byte): MyTraining?

    suspend fun getItemCount(): Byte

    suspend fun getAll(): LiveData<List<MyTraining>>
}