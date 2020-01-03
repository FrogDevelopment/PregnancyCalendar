package fr.frogdevelopment.pregnancycalendar.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ContractionDao {

    @Query("SELECT * FROM contraction")
    LiveData<List<Contraction>> getAll();

    @Insert
    Single<Long> insert(Contraction contraction);

    @Delete
    void delete(Contraction contraction);

    @Query("DELETE FROM contraction")
    void deleteAll();
}
