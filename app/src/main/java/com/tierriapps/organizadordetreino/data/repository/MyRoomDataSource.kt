package com.tierriapps.organizadordetreino.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.tierriapps.organizadordetreino.data.models.MyTraining
import com.tierriapps.organizadordetreino.data.roomdb.MyTrainingDao
import com.tierriapps.organizadordetreino.data.roomdb.MyTrainingEntity

class MyRoomDataSource(val myTrainingDao: MyTrainingDao): MyRepository {
    override suspend fun insert(myTraining: MyTraining) {
        val id = myTraining.id
        val name = myTraining.name
        val descrition = myTraining.descrition
        val divisionList = myTraining.divisionsList
        val value = MyTrainingEntity(name = name, divisionsList = divisionList, descrition = descrition)
        myTrainingDao.insert(value)
    }

    override suspend fun deleteById(id: Byte) {
        myTrainingDao.deleteById(id)
    }

    override suspend fun update(myTraining: MyTraining) {
        val id = myTraining.id
        val name = myTraining.name
        val divisionList = myTraining.divisionsList
        val descrition = myTraining.descrition
        val value = MyTrainingEntity(id, name, divisionList, descrition)
        myTrainingDao.update(value)
    }

    override suspend fun getLastItem(): MyTraining? {
        val value = myTrainingDao.getLastItem()

        if (value != null) {
            return MyTraining(value.id, value.name, value.divisionsList, value.descrition)
        }
        return null
    }

    override suspend fun getById(id: Byte): MyTraining? {
        val value = myTrainingDao.getById(id)
        if (value != null) {
            return MyTraining(value.id, value.name, value.divisionsList, value.descrition)
        }
        return null
    }

    override suspend fun getItemCount(): Byte {
        return myTrainingDao.getItemCount()
    }

    override suspend fun getAll(): LiveData<List<MyTraining>> {
        val value = myTrainingDao.getAll()
        val list = MutableLiveData<List<MyTraining>>()
        val l = mutableListOf<MyTraining>()
        for(v in value){
            val obj = MyTraining(v.id, v.name, v.divisionsList, v.descrition)
            l.add(obj)
        }
        list.value = l
        return list
    }


}