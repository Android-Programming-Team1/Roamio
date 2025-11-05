package com.example.stamps.Data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TravelDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "travel_stamp.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_COUNTRIES = "countries";
    public static final String TABLE_STAMPS = "stamps";

    public TravelDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCountries = "CREATE TABLE " + TABLE_COUNTRIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "flagResId INTEGER," +
                "photoUrl TEXT" +
                ");";

        String createStamps = "CREATE TABLE " + TABLE_STAMPS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "countryId INTEGER NOT NULL," +
                "stampedAt INTEGER," +
                "imageResId INTEGER," +
                "imageUri TEXT," +
                "imageUrl TEXT," +
                "FOREIGN KEY(countryId) REFERENCES countries(id) ON DELETE CASCADE" +
                ");";

        db.execSQL(createCountries);
        db.execSQL(createStamps);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAMPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRIES);
        onCreate(db);
    }
}
