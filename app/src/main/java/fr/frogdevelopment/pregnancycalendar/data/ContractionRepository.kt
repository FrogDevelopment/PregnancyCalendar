package fr.frogdevelopment.pregnancycalendar.data

import android.app.Application
import androidx.lifecycle.LiveData

class ContractionRepository(application: Application?) {

    private val contractionDao: ContractionDao
    val allContractions: LiveData<List<Contraction>>

    init {
        val db = AppDatabase.getDatabase(application)
        contractionDao = db.contractionDao()
        allContractions = contractionDao.all
    }

    fun insert(contraction: Contraction?) {
        AppDatabase.databaseWriteExecutor.execute { contractionDao.insert(contraction) }
    }

    fun delete(contraction: Contraction?) {
        AppDatabase.databaseWriteExecutor.execute { contractionDao.delete(contraction) }
    }

    fun deleteAll() {
        AppDatabase.databaseWriteExecutor.execute { contractionDao.deleteAll() }
    }
}