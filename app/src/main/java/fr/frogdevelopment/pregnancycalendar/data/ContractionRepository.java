package fr.frogdevelopment.pregnancycalendar.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import static fr.frogdevelopment.pregnancycalendar.data.AppDatabase.databaseWriteExecutor;

public class ContractionRepository {

    private final ContractionDao contractionDao;

    private final LiveData<List<Contraction>> allContractions;

    public ContractionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        contractionDao = db.contractionDao();
        allContractions = contractionDao.getAll();
    }

    public LiveData<List<Contraction>> getAllContractions() {
        return allContractions;
    }

    public void insert(Contraction contraction) {
        databaseWriteExecutor.execute(() -> contractionDao.insert(contraction));
    }

    public void delete(Contraction contraction) {
        databaseWriteExecutor.execute(() -> contractionDao.delete(contraction));
    }

    public void deleteAll() {
        databaseWriteExecutor.execute(contractionDao::deleteAll);
    }
}
