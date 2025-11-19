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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Stamp> list = new ArrayList<>();

        Cursor cursor = db.query(
                TravelDatabaseHelper.TABLE_STAMPS,
                null, null, null, null, null,
                "id ASC"
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            long countryId = cursor.getLong(cursor.getColumnIndexOrThrow("countryId"));
            long stampedAt = cursor.getLong(cursor.getColumnIndexOrThrow("stampedAt")); // 날짜 컬럼
            String imageName = cursor.getString(cursor.getColumnIndexOrThrow("imageName"));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri")); // 날짜 컬럼
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")); // 날짜 컬럼

            Stamp stamp = new Stamp(id, countryId, stampedAt, imageName, imageUri, imageUrl);
            list.add(stamp);
        }

        cursor.close();
        return list;
    }

    /** 스탬프 추가 */
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
}
