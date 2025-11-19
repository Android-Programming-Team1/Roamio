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
        values.put("region", country.getRegion());
        values.put("name", country.getName());
        values.put("city", country.getCity());
        values.put("description", country.getDescription());
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
            String region = cursor.getString(cursor.getColumnIndexOrThrow("region"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String photo = cursor.getString(cursor.getColumnIndexOrThrow("photoUrl"));
            countries.add(new Country(id, region, name, city, desc, photo));
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
        values.put("imageName", stamp.getImageName());
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
            String imageName = cursor.getString(cursor.getColumnIndexOrThrow("imageName"));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"));

            stamps.add(new Stamp(id, countryId, stampedAt, imageName, imageUri, imageUrl));
        }
        cursor.close();
        db.close();
        return stamps;
    }
}
