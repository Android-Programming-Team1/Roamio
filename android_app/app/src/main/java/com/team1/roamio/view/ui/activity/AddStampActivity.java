package com.team1.roamio.view.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.team1.roamio.R;
import com.team1.roamio.data.Stamp;
import com.team1.roamio.data.StampDao;

public class AddStampActivity extends AppCompatActivity {

    private EditText editCountryId, editImageResId;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stamp);

        editCountryId = findViewById(R.id.edit_country_id);
        editImageResId = findViewById(R.id.edit_image_res_id);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v -> saveStamp());
    }

    private void saveStamp() {
        StampDao dao = new StampDao(this);

        Stamp stamp = new Stamp();
        stamp.setCountryId(Long.parseLong(editCountryId.getText().toString()));
        stamp.setImageResId(Integer.parseInt(editImageResId.getText().toString()));
        stamp.setStampedAt(System.currentTimeMillis());

        dao.insertStamp(stamp);

        finish(); // 돌아가면 Fragment가 onResume()에서 reload함
    }
}
