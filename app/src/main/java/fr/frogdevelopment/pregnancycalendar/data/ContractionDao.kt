package fr.frogdevelopment.pregnancycalendar.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContractionDao {

    @Query("SELECT * FROM contraction ORDER BY datetime ASC")
    fun all(): LiveData<List<Contraction>>

    @Insert
    suspend fun insert(contraction: Contraction?)

    @Delete
    suspend fun delete(contraction: Contraction?)

    @Query("DELETE FROM contraction")
    suspend fun deleteAll()
}