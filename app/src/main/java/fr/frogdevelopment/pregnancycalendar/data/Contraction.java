package fr.frogdevelopment.pregnancycalendar.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity
public class Contraction {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public Integer id;

    @ColumnInfo(name = "datetime")
    public LocalDateTime dateTime;

    @ColumnInfo
    public Long duration;

}
