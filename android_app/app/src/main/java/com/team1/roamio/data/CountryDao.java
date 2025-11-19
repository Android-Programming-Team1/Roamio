package com.team1.roamio.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.team1.roamio.data.Country;

import java.util.ArrayList;
import java.util.List;

public class CountryDao {

    private final TravelDatabaseHelper dbHelper;

    public CountryDao(Context context) {
        this.dbHelper = new TravelDatabaseHelper(context);
    }

    /** ISO 코드(KR, JP, US...)로 country.id 가져오기 */
    // CountryDao.java
    public Long getCountryIdByIsoCode(String isoCode) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM countries WHERE isoCode = ?",
                new String[]{isoCode}
        );
        Long id = null;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }


    public String getCountryNameById(long countryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String name = null;

        Cursor cursor = db.rawQuery(
                "SELECT name FROM countries WHERE id = ?",
                new String[]{String.valueOf(countryId)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            cursor.close();
        }
        db.close();
        return name;
    }


    /** 특정 ID 국가 조회 */
    public Country getCountryById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, region, name, city, description, photoUrl " +
                        "FROM country WHERE id = ?",
                new String[]{ String.valueOf(id) }
        );

        Country country = null;

        if (cursor.moveToFirst()) {
            country = new Country(
                    cursor.getLong(0),
                    cursor.getString(1),      // region
                    cursor.getString(2),      // name
                    cursor.getString(3),      // city
                    cursor.getString(4),      // description
                    cursor.getString(5)       // photoUrl
            );

            country.setRegion(cursor.getString(1));
            country.setCity(cursor.getString(3));
        }

        cursor.close();
        return country;
    }

    /** 모든 국가 목록 가져오기 */
    public List<Country> getAllCountries() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Country> list = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT id, region, name, city, description, photoUrl, isoCode FROM country",
                null
        );

        while (cursor.moveToNext()) {

            Country country = new Country(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
            );

            country.setRegion(cursor.getString(1));
            country.setCity(cursor.getString(3));

            list.add(country);
        }

        cursor.close();
        return list;
    }
}