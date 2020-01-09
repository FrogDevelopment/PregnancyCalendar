package fr.frogdevelopment.pregnancycalendar.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime

@Entity
class Contraction {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Int? = null
    @ColumnInfo(name = "datetime")
    var dateTime: ZonedDateTime? = null
    @ColumnInfo
    var duration: Long? = null
}