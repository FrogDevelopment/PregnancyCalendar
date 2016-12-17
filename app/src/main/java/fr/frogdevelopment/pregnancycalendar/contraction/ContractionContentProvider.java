/*
 * Copyright (c) Frog Development 2015.
 */

package fr.frogdevelopment.pregnancycalendar.contraction;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ContractionContentProvider extends ContentProvider {

    private class DictionaryOpenHelper extends SQLiteOpenHelper {

        // When changing the database schema, increment the database version.
        private static final int    DATABASE_VERSION = 1;
        private static final String DATABASE_NAME    = "PREGNANCY_CALENDAR";

        private DictionaryOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Creating Tables
        @Override
        public void onCreate(SQLiteDatabase db) {
            ContractionContract.create(db);
        }

        // Upgrading database
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }


    private DictionaryOpenHelper mOpenHelper;

    public static final String AUTHORITY = "fr.frogdevelopment.pregnancycalendar.contraction.ContractionContentProvider";

    private static final int CONTRACTION_ID = 10;
    private static final int CONTRACTIONS = 20;
    private static final String BASE_PATH_CONTRACTION = "contraction";
    private static final String CONTENT_CONTRACTION_TYPE      = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_PATH_CONTRACTION + "s";
    private static final String CONTENT_CONTRACTION_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_PATH_CONTRACTION;

    public static final Uri URI_CONTRACTION = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_CONTRACTION);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_CONTRACTION, CONTRACTIONS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_CONTRACTION + "/#", CONTRACTION_ID);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DictionaryOpenHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = sURIMatcher.match(uri);
        switch (match) {

            case CONTRACTIONS:
                return CONTENT_CONTRACTION_TYPE;

            case CONTRACTION_ID:
                return CONTENT_CONTRACTION_ITEM_TYPE;

            default:
                return null;
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        switch (sURIMatcher.match(uri)) {

            case CONTRACTIONS:
                queryBuilder.setTables(ContractionContract.TABLE_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        SQLiteDatabase sqlDB = mOpenHelper.getWritableDatabase();

        long id;
        switch (sURIMatcher.match(uri)) {
            case CONTRACTIONS:
                id = sqlDB.insert(ContractionContract.TABLE_NAME, null, contentValues);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Uri newUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(newUri, null);

        return newUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sURIMatcher.match(uri)) {

            case CONTRACTION_ID:
                rowsDeleted = sqlDB.delete(ContractionContract.TABLE_NAME, ContractionContract._ID + "=" + uri.getLastPathSegment(), null);
                break;

            case CONTRACTIONS:
                rowsDeleted = sqlDB.delete(ContractionContract.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }
}
