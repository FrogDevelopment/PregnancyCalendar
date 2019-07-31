/*
 * Copyright (c) Frog Development 2015.
 */

package fr.frogdevelopment.pregnancycalendar.contraction;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDateTime;

class ContractionContract implements BaseColumns {

    static final String TABLE_NAME = "CONTRACTION";

    static final String DATETIME = "DATETIME";
    static final String DURATION = "DURATION";

    static final int INDEX_ID = 0;
    static final int INDEX_DATETIME = 1;
    static final int INDEX_DURATION = 2;

    static final String[] COLUMNS = {_ID, DATETIME, DURATION};

    // Queries
    private static final String SQL_CREATE = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER NOT NULL, %s INTEGER NOT NULL);", TABLE_NAME, _ID, DATETIME, DURATION);

    static void create(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    static class Contraction {

        String id;
        LocalDateTime dateTime;
        Long duration;

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("id", id)
                    .append("dateTime", dateTime)
                    .append("duration", duration)
                    .toString();
        }
    }

}
