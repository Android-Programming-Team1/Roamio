package com.team1.roamio.view.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.team1.roamio.R;
import com.team1.roamio.data.CountryDao;
import com.team1.roamio.data.Stamp;
import com.team1.roamio.data.StampDao;
import com.team1.roamio.utility.database.DriveManager;
import com.team1.roamio.utility.stamp.StampJsonParser;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AddStampActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stamp);

        icon = findViewById(R.id.imageView18);
        Glide.with(this).asGif().load(R.drawable.romeo2).into(icon);

        // 액티비티 시작과 동시에 권한 체크 및 GPS 시작
        checkPermissionAndStartGPS();
    }

    private void checkPermissionAndStartGPS() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        // 위치 리스너 정의
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // 위치 찾음 -> 처리 -> 리스너 해제 -> 종료
                processLocation(location);
                locationManager.removeUpdates(this);
            }
        };

        // GPS 및 네트워크 위치 요청
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        // (옵션) 10초 동안 못 찾으면 타임아웃 처리
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing()) {
                Toast.makeText(this, "위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, 10000);
    }

    private void processLocation(Location location) {
        String isoCode = getIsoCode(location.getLatitude(), location.getLongitude());

        if (isoCode == null) {
            Toast.makeText(this, "국가 정보를 읽지 못했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        CountryDao countryDao = new CountryDao(this);

        Long countryId = countryDao.getCountryIdByIsoCode(isoCode);

        if (countryId == null) {
            Toast.makeText(this, "서비스되지 않는 지역입니다: " + isoCode, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // CountryDao를 통해 countries 테이블에서 저장할 이미지 이름을 가져옴
        String imgNameFromDb = countryDao.getStampNameByCountryId(countryId);

        // 스탬프 DB 저장
        Stamp stamp = new Stamp();
        stamp.setCountryId(countryId);
        stamp.setStampedAt(System.currentTimeMillis());

        // 가져온 이미지 이름을 Stamp 객체에 설정
        if (imgNameFromDb != null && !imgNameFromDb.isEmpty()) {
            stamp.setImageName(imgNameFromDb);
        } else {
            // DB에 stampName이 설정되어 있지 않을 경우의 기본값
            stamp.setImageName("stamp_00");
        }

        // StampDao 인스턴스 생성 및 DB 저장
        StampDao stampDao = new StampDao(this);
        long stampId = stampDao.insertStamp(stamp); // DB에 저장하고 ID를 받음
        stamp.setId(stampId); // 저장된 ID를 Stamp 객체에 설정

        DriveManager driveManager = new DriveManager(this);
        driveManager.initialize(GoogleSignIn.getLastSignedInAccount(this));
        driveManager.saveStampJson("stamp_" + stamp.getId(), StampJsonParser.toJson(stamp));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(()->{
                    finishedProcess();
                });
            }
        }, 3000);
    }

    private String getIsoCode(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.US);
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);
            if (list != null && !list.isEmpty()) {
                return list.get(0).getCountryCode();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private void finishedProcess() {
        Toast.makeText(this, "스탬프 획득 완료!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}