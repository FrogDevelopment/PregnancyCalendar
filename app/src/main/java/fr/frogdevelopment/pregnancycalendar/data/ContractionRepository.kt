package fr.frogdevelopment.pregnancycalendar.data

import android.app.Application
import androidx.lifecycle.LiveData

class ContractionRepository(application: Application) {

    private val contractionDao: ContractionDao
    val allContractions: LiveData<List<Contraction>>

    init {
        val db = AppDatabase.getDatabase(application)
        contractionDao = db.contractionDao()
        allContractions = contractionDao.all()
    }

    suspend fun insert(contraction: Contraction?) {
        contractionDao.insert(contraction)
    }

    suspend fun delete(contraction: Contraction?) {
        contractionDao.delete(contraction)
    }

    suspend fun deleteAll() {
        contractionDao.deleteAll()
    }
}