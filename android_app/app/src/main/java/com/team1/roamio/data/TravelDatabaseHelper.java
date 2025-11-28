package com.team1.roamio.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TravelDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "stampDB.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_COUNTRIES = "countries";
    public static final String TABLE_STAMPS = "stamps";

    private static String DB_PATH = "";
    private final Context context;

    public TravelDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        // DB 파일이 저장될 시스템 경로 설정 (/data/data/패키지명/databases/)
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

        // ★ 핵심: 생성자에서 DB 복사 로직 실행
        try {
            createDataBase();
        } catch (IOException e) {
            Log.e("TravelDBHelper", "Database creation failed", e);
        }
    }

    /**
     * DB가 없으면 assets에서 복사하고, 있으면 넘어갑니다.
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (!dbExist) {
            // 빈 DB를 생성하여 폴더 경로를 확보
            this.getReadableDatabase();
            this.close();

            try {
                copyDataBase();
                Log.d("TravelDBHelper", "DB copied from assets successfully");
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * 내부 저장소에 DB 파일이 존재하는지 확인
     */
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DATABASE_NAME);
        return dbFile.exists();
    }

    /**
     * Assets 폴더의 DB 파일을 시스템 경로로 복사
     */
    private void copyDataBase() throws IOException {
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 이미 완성된 DB 파일을 복사하므로, 테이블 생성 쿼리(CREATE TABLE)는 필요 없습니다.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 버전 업그레이드 시 로직 (개발 중에는 앱 삭제 후 재설치 권장)
        if (newVersion > oldVersion) {
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}