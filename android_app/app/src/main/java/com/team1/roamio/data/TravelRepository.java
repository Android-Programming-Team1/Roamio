package com.team1.roamio.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TravelRepository {

    private final TravelDatabaseHelper dbHelper;

    public TravelRepository(Context context) {

        dbHelper = new TravelDatabaseHelper(context);
    }

    // ===== COUNTRY =====
    public long insertCountry(Country country) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", country.getName());
        values.put("description", country.getDescription());
//        values.put("flagResId", country.getFlagResId());
        values.put("photoUrl", country.getPhotoUrl());
        long id = db.insert(TravelDatabaseHelper.TABLE_COUNTRIES, null, values);
        db.close();
        return id;
    }

    public List<Country> getAllCountries() {
        List<Country> countries = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TravelDatabaseHelper.TABLE_COUNTRIES,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            int flag = cursor.getInt(cursor.getColumnIndexOrThrow("flagResId"));
            String photo = cursor.getString(cursor.getColumnIndexOrThrow("photoUrl"));
            countries.add(new Country(id, name, desc, flag, photo));
        }
        cursor.close();
        db.close();
        return countries;
    }

    // ===== STAMP =====
    public long insertStamp(Stamp stamp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("countryId", stamp.getCountryId());
        values.put("stampedAt", stamp.getStampedAt());
        values.put("imageResId", stamp.getImageResId());
        values.put("imageUri", stamp.getImageUri());
        values.put("imageUrl", stamp.getImageUrl());
        long id = db.insert(TravelDatabaseHelper.TABLE_STAMPS, null, values);
        db.close();
        return id;
    }

    public List<Stamp> getAllStamps() {
        List<Stamp> stamps = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TravelDatabaseHelper.TABLE_STAMPS,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long countryId = cursor.getLong(cursor.getColumnIndexOrThrow("countryId"));
            long stampedAt = cursor.getLong(cursor.getColumnIndexOrThrow("stampedAt"));
            int imageRes = cursor.getInt(cursor.getColumnIndexOrThrow("imageResId"));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"));

            stamps.add(new Stamp(id, countryId, stampedAt, imageRes, imageUri, imageUrl));
        }
        cursor.close();
        db.close();
        return stamps;
    }
}
