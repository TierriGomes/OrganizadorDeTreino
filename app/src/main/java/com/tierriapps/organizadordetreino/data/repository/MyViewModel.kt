package com.tierriapps.organizadordetreino.data.repository

import android.util.Log
import androidx.lifecycle.*
import com.tierriapps.organizadordetreino.data.models.MyTraining
import kotlinx.coroutines.launch

class MyViewModel(val repository: MyRepository): ViewModel() {
    private val _myTrainings = MutableLiveData<List<MyTraining>>()
    val myTrainings: LiveData<List<MyTraining>> = _myTrainings

    fun insert(myTraining: MyTraining) {
        viewModelScope.launch {
            repository.insert(myTraining)
        }
    }

    fun deleteById(id: Byte) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun update(myTraining: MyTraining) {
        viewModelScope.launch {
            repository.update(myTraining)
        }
    }

    fun getLastItem(): LiveData<MyTraining> {
        return liveData {
            repository.getLastItem()?.let { emit(it) }
        }
    }

    fun getById(id: Byte): LiveData<MyTraining> {
        return liveData {
            repository.getById(id)?.let { emit(it) }
        }
    }

    fun getItemCount(): LiveData<Byte> {
        return liveData {
            emit(repository.getItemCount())
        }
    }

    fun getAll() {
        viewModelScope.launch {
            val myTrainingsList = repository.getAll()
            Log.d("mytag", myTrainingsList.value.toString())
            _myTrainings.postValue(myTrainingsList.value)
        }
    }
}
class MyViewModelFactory(private val repository: MyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}