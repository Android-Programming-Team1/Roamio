package com.team1.roamio.utility.database;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList; // FileList 임포트 추가
import com.team1.roamio.R; // R.string.app_name 사용을 위해 임포트

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List; // List 임포트 추가
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Google Drive API와의 상호작용을 처리하는 유틸리티 클래스입니다.
 * UI 스레드로부터 네트워크 작업을 분리하고, Drive 서비스 초기화 및 파일 관리를 담당합니다.
 */
public class DriveManager {

    private static final String TAG = "DriveManager";
    private final Context context;
    private Drive driveService;

    // 백그라운드 스레드에서 네트워크 작업을 수행하기 위한 Executor
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // UI 스레드에서 UI 업데이트 및 토스트 메시지를 표시하기 위한 Handler
    private final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * DriveManager 생성자.
     * @param context 애플리케이션 Context (토스트 메시지 및 리소스 접근에 사용).
     */
    public DriveManager(Context context) {
        this.context = context;
    }

    /**
     * GoogleSignInAccount를 사용하여 Drive 서비스 객체를 초기화합니다.
     * 이 메서드는 로그인 성공 직후 호출되어야 합니다.
     * @param signInAccount 구글 로그인 계정 정보.
     */
    public void initialize(GoogleSignInAccount signInAccount) {
        // 인증 정보를 사용하여 사용자 자격 증명을 설정합니다.
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(signInAccount.getAccount());

        // Drive Service 객체를 빌드합니다.
        driveService = new Drive.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(context.getString(R.string.app_name)) // 앱 이름을 설정
                .build();

        Log.d(TAG, "Drive service initialized successfully.");
    }

    /**
     * 현재 Drive 서비스가 초기화되었는지 확인합니다.
     * @return 초기화되었으면 true, 아니면 false.
     */
    public boolean isInitialized() {
        return driveService != null;
    }

    /**
     * Google Drive에 새 파일을 생성하거나 기존 파일을 업데이트합니다.
     * 요청하신 saveFile() 메서드입니다. (네트워크 작업이므로 백그라운드 스레드에서 실행)
     * @param fileName 생성/업데이트할 파일 이름
     * @param content 파일에 들어갈 내용
     * @param ext 파일 확장자 (예: ".txt")
     */
    public void saveFile(String fileName, String content, String ext) {
        if (!isInitialized()) {
            Log.e(TAG, "Drive service is not initialized.");
            handler.post(() -> Toast.makeText(context, "Drive 서비스가 초기화되지 않았습니다. 로그인을 확인하세요.", Toast.LENGTH_LONG).show());
            return;
        }

        // 백그라운드 스레드에서 Drive API 호출
        executor.execute(() -> {
            try {
                // 1. 임시 로컬 파일 생성 (드라이브에 업로드할 내용)
                java.io.File tempFile = java.io.File.createTempFile("roamio_temp", ext);
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(content);
                }

                // 2. 'RoamIO' 폴더 ID를 가져오거나 새로 생성합니다.
                String folderId = getOrCreateFolderId("RoamIO");

                // 3. Drive 파일 메타데이터 설정 (새 파일 생성용)
                File fileMetadata = new File();
                fileMetadata.setName(fileName);
                fileMetadata.setMimeType("text/plain"); // 텍스트 파일 MIME 타입

                // 4. 파일이 저장될 부모 폴더를 설정합니다.
                if (folderId != null) {
                    fileMetadata.setParents(Collections.singletonList(folderId));
                    Log.d(TAG, "Setting parent folder ID: " + folderId);
                } else {
                    Log.w(TAG, "Could not find or create RoamIO folder. Saving to root.");
                }

                // 5. 파일 콘텐츠 정의
                FileContent mediaContent = new FileContent("text/plain", tempFile);

                // 6. Drive API 호출 (새 파일 생성)
                File driveFile = driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id, name") // 응답으로 받을 필드만 지정
                        .execute();

                // 7. UI 스레드에서 결과 처리
                handler.post(() -> {
                    if (driveFile != null) {
                        Log.d(TAG, "File created successfully. ID: " + driveFile.getId());
                        Toast.makeText(context, "드라이브의 RoamIO 폴더에 파일 생성 성공: " + driveFile.getName(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "파일 생성 실패", Toast.LENGTH_SHORT).show();
                    }
                    // 임시 로컬 파일 정리
                    tempFile.delete();
                });

            } catch (IOException e) {
                Log.e(TAG, "Error creating file in Drive", e);
                handler.post(() -> Toast.makeText(context, "파일 생성 중 오류 발생: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    /**
     * Google Drive에서 특정 이름의 폴더를 찾거나 없으면 새로 생성하여 ID를 반환합니다.
     * @param folderName 찾거나 생성할 폴더 이름.
     * @return 폴더 ID, 오류 발생 시 null.
     * @throws IOException Drive API 호출 중 오류 발생 시.
     */
    private String getOrCreateFolderId(String folderName) throws IOException {
        String folderMimeType = "application/vnd.google-apps.folder";

        // 1. 폴더 검색 (이름 일치, 폴더 타입, 휴지통에 없는지 확인)
        String query = String.format("name = '%s' and mimeType = '%s' and trashed = false", folderName, folderMimeType);

        FileList result = driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute();

        List<File> files = result.getFiles();

        if (files != null && !files.isEmpty()) {
            // 이미 폴더가 존재하면 해당 ID를 반환
            Log.d(TAG, "Folder '" + folderName + "' already exists. ID: " + files.get(0).getId());
            return files.get(0).getId();
        } else {
            // 폴더가 없으면 새로 생성
            File folderMetadata = new File();
            folderMetadata.setName(folderName);
            folderMetadata.setMimeType(folderMimeType);

            File folder = driveService.files().create(folderMetadata)
                    .setFields("id")
                    .execute();

            Log.d(TAG, "Folder '" + folderName + "' created. ID: " + folder.getId());
            return folder.getId();
        }
    }
}