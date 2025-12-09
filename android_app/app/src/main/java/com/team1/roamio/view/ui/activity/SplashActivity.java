package com.team1.roamio.view.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;
import com.team1.roamio.R;
import com.team1.roamio.utility.planner.SavedUserData;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "GoogleSignIn";
    private static final int RC_SIGN_IN = 9001; // 요청 코드
    private GoogleSignInClient mGoogleSignInClient;

    private ImageView icon;

    // TODO: 4.1 Drive 서비스 초기화를 위해 필요한 필드를 여기에 추가하세요.
    // private Drive driveService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        icon = findViewById(R.id.roamio_icon);

        Glide.with(this).asGif().load(R.drawable.romeo1).into(icon);

        // 1. Google Sign-In 옵션 구성 (필요한 Scope 추가)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();

        // 2. GoogleSignInClient 생성
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 3. 로그인 버튼 리스너 설정
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> signIn());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 이미 로그인 되어있는지 확인
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && account.getGrantedScopes().contains(new Scope(DriveScopes.DRIVE_FILE))) {
            // 이미 로그인되어 있고 Drive 권한이 있다면 바로 메인으로 이동
            handleSignInSuccess(account);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // 로그인 성공 및 권한 확인
            if (account != null && account.getGrantedScopes().contains(new Scope(DriveScopes.DRIVE_FILE))) {
                Log.d(TAG, "Sign-in successful, Drive scope granted.");
                handleSignInSuccess(account);
            } else {
                // 로그인 성공했지만 Drive 권한이 없거나, 다른 문제가 있다면 재로그인 유도
                Toast.makeText(this, "구글 드라이브 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            }

        } catch (ApiException e) {
            // 로그인 실패
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "로그인 실패: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleSignInSuccess(GoogleSignInAccount account) {
        SavedUserData.userName = account.getDisplayName();
        Toast.makeText(this, account.getDisplayName() + "님 환영합니다!", Toast.LENGTH_SHORT).show();

        // 1. Google Drive 서비스 초기화
        // TODO: 4.1에서 구현했던 initializeDriveService(account) 메서드를 여기서 호출하여 Drive 객체를 만드세요.
        // initializeDriveService(account);

        // 2. 메인 화면으로 전환
        Intent intent = new Intent(SplashActivity.this, LayoutActivity.class); // ***수정 필요***
        startActivity(intent);
        finish();

        // 3. (선택 사항) 로그인 직후 파일 생성 테스트를 하고 싶다면
        // TODO: 4.2에서 구현했던 createFileInDrive() 메서드를 여기서 호출해 볼 수 있습니다.
        // createFileInDrive();
    }

    // TODO: 4.1 Drive 서비스 초기화 메서드 (이전 답변 참고)
    // private void initializeDriveService(GoogleSignInAccount signInAccount) { ... }

    // TODO: 4.2 파일 생성 메서드 (이전 답변 참고)
    // private void createFileInDrive() { ... }
}