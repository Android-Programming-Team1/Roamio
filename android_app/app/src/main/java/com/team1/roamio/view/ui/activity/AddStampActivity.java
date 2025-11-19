package com.team1.roamio.view.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.team1.roamio.R;
import com.team1.roamio.data.CountryDao;
import com.team1.roamio.data.Stamp;
import com.team1.roamio.data.StampDao;

import java.util.List;
import java.util.Locale;

public class AddStampActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText editImageName;
    private Button btnSave;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stamp);

        editImageName = findViewById(R.id.edit_image_name);
        btnSave = findViewById(R.id.btn_save);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 위치 변화 감지 리스너
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                double lat = location.getLatitude();
                double lon = location.getLongitude();

                String isoCode = getCountryCode(lat, lon);

                if (isoCode == null) {
                    Toast.makeText(AddStampActivity.this, "국가 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                CountryDao countryDao = new CountryDao(AddStampActivity.this);
                Long countryId = countryDao.getCountryIdByIsoCode(isoCode);

                if (countryId == null) {
                    Toast.makeText(AddStampActivity.this, "DB에서 국가를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveStampToDB(countryId);

                // 위치 한 번만 저장하면 되므로 업데이트 중단
                if (ActivityCompat.checkSelfPermission(
                        AddStampActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(locationListener);
                }
            }
        };

        btnSave.setOnClickListener(v -> checkPermission());
    }

    /** 위치 권한 확인 */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE
            );
        } else {
            requestGPS();
        }
    }

    /** GPS 요청 */
    private void requestGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "GPS 권한이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "GPS 위치를 가져오는 중...", Toast.LENGTH_SHORT).show();

        // 3초, 10m 기준으로 업데이트 요청
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000, 10,
                locationListener
        );
    }

    /** 나라 코드 획득 */
    private String getCountryCode(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);
            if (list != null && !list.isEmpty()) {
                return list.get(0).getCountryCode(); // 예: KR, JP, US
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** 스탬프 DB 저장 */
    private void saveStampToDB(long countryId) {
        String imageName = editImageName.getText().toString().trim();
        if (imageName.isEmpty()) imageName = "stamp_00"; // 기본 이미지

        Stamp stamp = new Stamp();
        stamp.setCountryId(countryId);
        stamp.setImageName(imageName);
        stamp.setStampedAt(System.currentTimeMillis());

        long newId = new StampDao(this).insertStamp(stamp);

        Toast.makeText(this, "GPS 기반 스탬프 추가 완료! ID: " + newId, Toast.LENGTH_SHORT).show();
        finish();
    }


    /** 권한 결과 콜백 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestGPS();
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
