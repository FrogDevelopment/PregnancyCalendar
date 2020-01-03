package fr.frogdevelopment.pregnancycalendar.ui.contraction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fr.frogdevelopment.pregnancycalendar.data.Contraction
import fr.frogdevelopment.pregnancycalendar.data.ContractionRepository
import kotlinx.coroutines.launch

class ContractionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContractionRepository = ContractionRepository(application)

    val allContractions: LiveData<List<Contraction>>

    init {
        allContractions = repository.allContractions
    }

    fun insert(contraction: Contraction) = viewModelScope.launch {
        repository.insert(contraction)
    }

    fun delete(contraction: Contraction) = viewModelScope.launch {
        repository.delete(contraction)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}