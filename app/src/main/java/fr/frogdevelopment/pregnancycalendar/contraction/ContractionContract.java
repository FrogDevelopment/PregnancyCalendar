/*
 * Copyright (c) Frog Development 2015.
 */

package fr.frogdevelopment.pregnancycalendar.contraction;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.threeten.bp.LocalDateTime;

class ContractionContract implements BaseColumns {

    static final String TABLE_NAME = "CONTRACTION";

    static final String DATETIME = "DATETIME";
    static final String DURATION = "DURATION";

    public static final int INDEX_ID = 0;
    public static final int INDEX_DATETIME = 1;
    public static final int INDEX_DURATION = 2;

    public static final String[] COLUMNS = {_ID, DATETIME, DURATION};

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
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Contraction that = (Contraction) o;

            return new EqualsBuilder()
                    .append(id, that.id)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(id)
                    .toHashCode();
        }
    }

}
