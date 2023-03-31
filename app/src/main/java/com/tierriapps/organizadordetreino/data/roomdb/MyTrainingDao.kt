package com.tierriapps.organizadordetreino.data.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MyTrainingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(myTraining: MyTrainingEntity)

    @Query("DELETE FROM my_training WHERE id = :id")
    suspend fun deleteById(id: Byte)

    @Update
    suspend fun update(myTraining: MyTrainingEntity)

    @Query("SELECT * FROM my_training ORDER BY id DESC LIMIT 1")
    suspend fun getLastItem(): MyTrainingEntity?

    @Query("SELECT * FROM my_training WHERE id = :id")
    suspend fun getById(id: Byte): MyTrainingEntity?

    @Query("SELECT COUNT(*) FROM my_training")
    suspend fun getItemCount(): Byte

    @Query("SELECT * FROM my_training")
    suspend fun getAll(): List<MyTrainingEntity>
}