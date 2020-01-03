package fr.frogdevelopment.pregnancycalendar.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContractionDao {

    @Query("SELECT * FROM contraction ORDER BY datetime ASC")
    LiveData<List<Contraction>> getAll();

    @Insert
    void insert(Contraction contraction);

    @Delete
    void delete(Contraction contraction);

    @Query("DELETE FROM contraction")
    void deleteAll();
}
