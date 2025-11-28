package com.team1.roamio.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.team1.roamio.data.TravelDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CountryDao {

    private final TravelDatabaseHelper dbHelper;

    public CountryDao(Context context) {
        // DB Helper 인스턴스 생성 (싱글톤 패턴을 권장하나 여기선 직접 생성)
        this.dbHelper = new TravelDatabaseHelper(context);
    }

    /**
     * 국가 ID를 이용해 해당 국가에 지정된 스탬프 이미지 이름(stampName)을 가져옴
     */
    public String getStampNameByCountryId(long countryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String stampName = null;

        // 쿼리: id가 countryId인 행에서 stampName 컬럼을 선택
        String query = "SELECT stampName FROM " + TravelDatabaseHelper.TABLE_COUNTRIES + " WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(countryId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // 인덱스 0에서 stampName을 가져옴
                stampName = cursor.getString(0);
            }
            cursor.close();
        }
        db.close();
        return stampName;
    }

    /** * ID로 국가 이름 조회
     * (StampListFragment에서 사용)
     */
    public String getCountryNameById(long countryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String name = "Unknown";

        // id 컬럼과 매칭되는 name 조회
        String query = "SELECT name FROM countries WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(countryId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(0); // 첫 번째 컬럼(name)
            }
            cursor.close();
        }
        db.close();
        return name;
    }

    /**
     * ISO 코드(KR, JP)로 DB의 Country ID 조회
     * (AddStampActivity GPS 로직에서 사용)
     */
    public Long getCountryIdByIsoCode(String isoCode) {
        if (isoCode == null) return null;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Long countryId = null;

        // 1. ISO 코드를 DB에 저장된 'name'으로 변환 (매핑)
        // Country 모델에 isoCode 필드가 없으므로 이름으로 검색해야 함
        String searchName = mapIsoToDbName(isoCode);

        if (searchName == null) {
            Log.e("CountryDao", "지원하지 않는 국가 코드입니다: " + isoCode);
            return null;
        }

        // 2. 이름이 포함된 국가 검색 (LIKE 사용)
        // 예: "Korea"로 검색하면 "South Korea" 등을 찾음
        String query = "SELECT id FROM countries WHERE name LIKE ? LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchName + "%"});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                countryId = cursor.getLong(0); // id 반환
            }
            cursor.close();
        }
        db.close();

        return countryId;
    }

    /**
     * ISO 코드를 DB 검색용 이름으로 변환하는 헬퍼 메서드
     */
    private String mapIsoToDbName(String isoCode) {
        switch (isoCode.toUpperCase()) {
            case "KR": return "한국";
            case "JP": return "일본";
            case "FR": return "프랑스";
            case "US": return "미국";
            case "CN": return "중국";
            case "VN": return "베트남";
            default: return null;
        }
    }

    // (참고용) 모든 국가 가져오기
    public List<Country> getAllCountries() {
        List<Country> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM countries", null);

        if (cursor.moveToFirst()) {
            do {
                // 컬럼 인덱스 조회
                int idIndex = cursor.getColumnIndex("id");
                int regionIndex = cursor.getColumnIndex("region");
                int nameIndex = cursor.getColumnIndex("name");
                int cityIndex = cursor.getColumnIndex("city");
                int descIndex = cursor.getColumnIndex("description");
                int photoIndex = cursor.getColumnIndex("photoUrl");
                int stampIndex = cursor.getColumnIndex("stampName");

                if (idIndex != -1) {
                    list.add(new Country(
                            cursor.getLong(idIndex),
                            cursor.getString(regionIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(cityIndex),
                            cursor.getString(descIndex),
                            cursor.getString(photoIndex),
                            cursor.getString(stampIndex)
                    ));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}