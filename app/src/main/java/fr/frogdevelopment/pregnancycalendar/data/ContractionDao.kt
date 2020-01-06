package fr.frogdevelopment.pregnancycalendar.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ContractionDao {

    @get:Query("SELECT * FROM contraction ORDER BY datetime ASC")
    val all: LiveData<List<Contraction>>

    @Insert
    fun insert(contraction: Contraction?)

    @Delete
    fun delete(contraction: Contraction?)

    @Query("DELETE FROM contraction")
    fun deleteAll()
}