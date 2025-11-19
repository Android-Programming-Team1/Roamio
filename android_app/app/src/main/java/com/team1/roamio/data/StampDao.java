package com.team1.roamio.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class StampDao {

    private final TravelDatabaseHelper dbHelper;

    public StampDao(Context context) {
        dbHelper = new TravelDatabaseHelper(context);
    }

    /** 스탬프 전체 조회 */
    public List<Stamp> getAllStamps() {
        List<Stamp> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TravelDatabaseHelper.TABLE_STAMPS,
                null, null, null, null, null,
                "id ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Stamp s = new Stamp(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("countryId")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("stampedAt")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("imageResId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUri")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
                );
                list.add(s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    /** 스탬프 추가 */
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
}
