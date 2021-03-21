package fr.frogdevelopment.pregnancycalendar.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "CONTRACTION")
class Contraction {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int? = null

    @NonNull
    @ColumnInfo(name = "DATETIME")
    var dateTime: ZonedDateTime? = null

    @NonNull
    @ColumnInfo(name = "DURATION")
    var duration: Long? = null
}
