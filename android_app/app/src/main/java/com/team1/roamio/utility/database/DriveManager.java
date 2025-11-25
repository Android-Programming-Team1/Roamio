package com.team1.roamio.utility.database;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair; // Pair 클래스 사용을 위해 추가
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.team1.roamio.R;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Google Drive API와의 상호작용을 처리하는 유틸리티 클래스입니다.
 * UI 스레드로부터 네트워크 작업을 분리하고, Drive 서비스 초기화 및 특정 폴더(Roamio/meta/plan, Roamio/meta/stamp)의 JSON 파일 관리를 담당합니다.
 */
public class DriveManager {

    private static final String TAG = "DriveManager";
    private final Context context;
    private Drive driveService;

    // 기본 경로 설정
    private static final String BASE_FOLDER = "Roamio";

    // 백그라운드 스레드에서 네트워크 작업을 수행하기 위한 Executor
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // UI 스레드에서 UI 업데이트 및 토스트 메시지를 표시하기 위한 Handler
    private final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 파일 목록 조회 결과를 비동기적으로 전달하기 위한 콜백 인터페이스입니다.
     */
    public interface DriveFileCallback {
        /**
         * 파일 목록 조회 성공 시 호출됩니다.
         * @param files 조회된 파일 객체 목록 (com.google.api.services.drive.model.File).
         */
        void onSuccess(List<File> files);

        /**
         * 파일 목록 조회 실패 시 호출됩니다.
         * @param e 발생한 예외.
         */
        void onFailure(Exception e);
    }

    /**
     * 단일 파일의 내용을 비동기적으로 전달하기 위한 콜백 인터페이스입니다.
     */
    public interface DriveFileContentCallback {
        /**
         * 파일 내용 조회 성공 시 호출됩니다.
         * @param content 파일의 내용 (JSON 문자열).
         */
        void onSuccess(String content);

        /**
         * 파일 내용 조회 실패 시 호출됩니다.
         * @param e 발생한 예외.
         */
        void onFailure(Exception e);
    }

    /**
     * 여러 파일의 ID와 내용을 비동기적으로 전달하기 위한 콜백 인터페이스입니다.
     */
    public interface DriveFileContentListCallback {
        /**
         * 파일 목록 및 내용 조회 성공 시 호출됩니다.
         * @param contentList 조회된 파일 ID와 내용 쌍의 목록 (Pair<String, String>).
         */
        void onSuccess(List<Pair<String, String>> contentList);

        /**
         * 파일 목록 및 내용 조회 실패 시 호출됩니다.
         * @param e 발생한 예외.
         */
        void onFailure(Exception e);
    }

    /**
     * DriveManager 생성자.
     * @param context 애플리케이션 Context.
     */
    public DriveManager(Context context) {
        this.context = context;
    }

    /**
     * GoogleSignInAccount를 사용하여 Drive 서비스 객체를 초기화합니다.
     * @param signInAccount 구글 로그인 계정 정보.
     */
    public void initialize(GoogleSignInAccount signInAccount) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(signInAccount.getAccount());

        driveService = new Drive.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName(context.getString(R.string.app_name))
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

    // =========================================================================
    // 파일 저장 (Save) 메서드
    // =========================================================================

    /**
     * 'Roamio/meta/plan' 폴더에 JSON 파일을 저장합니다.
     * @param fileName 저장할 파일 이름 (확장자 제외).
     * @param content 파일에 들어갈 JSON 내용.
     */
    public void savePlanJson(String fileName, String content) {
        saveJsonFile("meta/plan", fileName + ".json", content, "플랜 파일");
    }

    /**
     * 'Roamio/meta/stamp' 폴더에 JSON 파일을 저장합니다.
     * @param fileName 저장할 파일 이름 (확장자 제외).
     * @param content 파일에 들어갈 JSON 내용.
     */
    public void saveStampJson(String fileName, String content) {
        saveJsonFile("meta/stamp", fileName + ".json", content, "스탬프 파일");
    }

    /**
     * 지정된 하위 경로에 JSON 파일을 저장하는 공통 로직입니다.
     * @param subPath 'Roamio/' 다음의 폴더 경로 (예: "meta/plan").
     * @param fullFileName 파일 이름 (확장자 포함).
     * @param content 파일 내용.
     * @param fileType 저장할 파일의 유형 설명 (토스트 메시지용).
     */
    private void saveJsonFile(String subPath, String fullFileName, String content, String fileType)
    {
        if (!isInitialized())
        {
            Log.e(TAG, "Drive service is not initialized.");
            handler.post(() -> Toast.makeText(context, "Drive 서비스가 초기화되지 않았습니다. 로그인을 확인하세요.", Toast.LENGTH_LONG).show());
            return;
        }

        final String fullPath = BASE_FOLDER + "/" + subPath;

        executor.execute(() -> {
            java.io.File tempFile = null;
            try
            {
                // 1. 임시 로컬 파일 생성
                tempFile = java.io.File.createTempFile("roamio_temp", ".json");
                try (FileWriter writer = new FileWriter(tempFile)) { // try-with-resources는 일반적으로 한 줄에 유지
                    writer.write(content);
                }

                // 2. 대상 폴더 ID를 가져오거나 새로 생성합니다.
                String folderId = getOrCreateFolderIdByPath(fullPath);

                // 3. Drive 파일 메타데이터 설정
                File fileMetadata = new File();
                fileMetadata.setName(fullFileName);
                fileMetadata.setMimeType("application/json");

                // 4. 파일이 저장될 부모 폴더를 설정합니다.
                if (folderId != null)
                {
                    fileMetadata.setParents(Collections.singletonList(folderId));
                    Log.d(TAG, "Setting parent folder ID: " + folderId + " for path: " + fullPath);
                }
                else
                {
                    Log.e(TAG, "Could not find or create folder path. Saving to root.");
                }

                // 5. 파일 콘텐츠 정의 및 Drive API 호출
                FileContent mediaContent = new FileContent("application/json", tempFile);
                File driveFile = driveService.files().create(fileMetadata, mediaContent)
                        .setFields("id, name")
                        .execute();

                // 6. UI 스레드에서 결과 처리
                handler.post(() -> {
                    if (driveFile != null)
                    {
                        Log.d(TAG, fileType + " created successfully. ID: " + driveFile.getId());
                    }
                    else
                    {
                    }
                });

            }
            catch (IOException e)
            {
                Log.e(TAG, "Error creating " + fileType + " in Drive", e);
                handler.post(() -> Toast.makeText(context, fileType + " 생성 중 오류 발생했어요. 나중에 다시 시도해주세요.", Toast.LENGTH_LONG).show());
            }
            finally
            {
                // 임시 로컬 파일 정리
                if (tempFile != null)
                {
                    tempFile.delete();
                }
            }
        });
    }

    // =========================================================================
    // 파일 읽기 (Read) 메서드
    // =========================================================================

    /**
     * 지정된 하위 경로의 폴더에 있는 파일 목록을 조회하는 공통 로직입니다.
     * @param subPath 'Roamio/' 다음의 폴더 경로 (예: "meta/plan").
     * @param callback 조회 결과를 비동기적으로 처리할 콜백.
     */
    private void readFilesByPath(String subPath, DriveFileCallback callback)
    {
        if (!isInitialized())
        {
            Log.e(TAG, "Drive service is not initialized.");
            handler.post(() -> callback.onFailure(new IllegalStateException("Drive service is not initialized.")));
            return;
        }

        final String fullPath = BASE_FOLDER + "/" + subPath;

        executor.execute(() -> {
            try
            {
                // 1. 폴더 ID 가져오기 (없으면 null 반환)
                String folderId = getFolderIdByPath(fullPath);

                if (folderId == null)
                {
                    Log.w(TAG, "Folder path not found: " + fullPath + ". Returning empty list.");
                    // 폴더가 없으면 에러가 아닌 빈 목록을 반환하는 것이 사용자 경험상 좋습니다.
                    handler.post(() -> callback.onSuccess(Collections.emptyList()));
                    return;
                }

                // 2. 해당 폴더의 파일 목록 검색 쿼리
                // 지정된 폴더가 부모이고, 휴지통에 없는 모든 파일/폴더를 검색합니다.
                String query = "'" + folderId + "' in parents and trashed = false";

                List<File> resultFiles = new ArrayList<>();
                String pageToken = null;

                // 3. 페이지네이션을 처리하며 파일 목록 조회
                do {
                    FileList result = driveService.files().list()
                            .setQ(query)
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id, name, mimeType, size, modifiedTime)")
                            .setPageToken(pageToken)
                            .execute();

                    if (result.getFiles() != null)
                    {
                        resultFiles.addAll(result.getFiles());
                    }
                    pageToken = result.getNextPageToken();
                } while (pageToken != null);

                // 4. UI 스레드에서 결과 반환
                Log.d(TAG, "Found " + resultFiles.size() + " files in folder: " + fullPath);
                handler.post(() -> callback.onSuccess(resultFiles));

            }
            catch (Exception e)
            {
                Log.e(TAG, "Error reading files from path: " + fullPath, e);
                // 5. UI 스레드에서 오류 반환
                handler.post(() -> callback.onFailure(e));
            }
        });
    }

    /**
     * 'Roamio/meta/plan' 폴더의 파일 목록을 조회합니다. (메타데이터만 포함)
     * @param callback 조회 결과를 비동기적으로 처리할 콜백.
     */
    public void readPlanFiles(DriveFileCallback callback) {
        readFilesByPath("meta/plan", callback);
    }

    /**
     * 'Roamio/meta/stamp' 폴더의 파일 목록을 조회합니다. (메타데이터만 포함)
     * @param callback 조회 결과를 비동기적으로 처리할 콜백.
     */
    public void readStampFiles(DriveFileCallback callback) {
        readFilesByPath("meta/stamp", callback);
    }

    /**
     * 파일 ID를 사용하여 특정 파일의 내용을 읽어옵니다.
     * (네트워크 작업이므로 백그라운드 스레드에서 실행)
     * @param fileId 읽어올 파일의 고유 ID.
     * @param callback 파일 내용을 비동기적으로 처리할 콜백.
     */
    public void readFileContent(String fileId, DriveFileContentCallback callback)
    {
        if (!isInitialized())
        {
            Log.e(TAG, "Drive service is not initialized.");
            handler.post(() -> callback.onFailure(new IllegalStateException("Drive service is not initialized.")));
            return;
        }

        executor.execute(() -> {
            try
            {
                // 1. 파일 내용을 다운로드하기 위한 InputStream 가져오기
                InputStream inputStream = driveService.files().get(fileId).executeMediaAsInputStream();

                // 2. InputStream에서 바이트 배열로 읽기
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // 3. 바이트 배열을 UTF-8 문자열로 변환 (일반적인 텍스트/JSON 파일 처리)
                String content = outputStream.toString("UTF-8");

                // 4. UI 스레드에서 결과 반환 (파일 내부의 String 내용이 콜백을 통해 리턴됨)
                Log.d(TAG, "Successfully read content for file ID: " + fileId);
                handler.post(() -> callback.onSuccess(content));

            }
            catch (IOException e)
            {
                Log.e(TAG, "Error reading file content for ID: " + fileId, e);
                // 5. UI 스레드에서 오류 반환
                handler.post(() -> callback.onFailure(e));
            }
        });
    }

    /**
     * 'Roamio/meta/plan' 폴더의 모든 파일 ID와 내용을 조회합니다.
     * @param callback 조회 결과를 비동기적으로 처리할 콜백.
     */
    public void readPlanFileContents(DriveFileContentListCallback callback) {
        readAllFileContentsInPath("meta/plan", callback);
    }

    /**
     * 'Roamio/meta/stamp' 폴더의 모든 파일 ID와 내용을 조회합니다.
     * @param callback 조회 결과를 비동기적으로 처리할 콜백.
     */
    public void readStampFileContents(DriveFileContentListCallback callback) {
        readAllFileContentsInPath("meta/stamp", callback);
    }

    /**
     * 지정된 하위 경로의 폴더에 있는 모든 파일의 ID와 내용을 조회하는 공통 로직입니다.
     * (네트워크 작업이므로 백그라운드 스레드에서 실행)
     * @param subPath 'Roamio/' 다음의 폴더 경로 (예: "meta/plan").
     * @param callback 조회 결과를 비동기적으로 처리할 콜백.
     */
    private void readAllFileContentsInPath(String subPath, DriveFileContentListCallback callback)
    {
        if (!isInitialized())
        {
            Log.e(TAG, "Drive service is not initialized.");
            handler.post(() -> callback.onFailure(new IllegalStateException("Drive service is not initialized.")));
            return;
        }

        final String fullPath = BASE_FOLDER + "/" + subPath;

        executor.execute(() -> {
            // Pair<String, String> 타입으로 변경
            List<Pair<String, String>> contentList = new ArrayList<>();
            List<Exception> errors = new ArrayList<>(1);

            try
            {
                // 1. 폴더 ID 가져오기
                String folderId = getFolderIdByPath(fullPath);

                if (folderId == null)
                {
                    Log.w(TAG, "Folder path not found: " + fullPath + ". Returning empty list.");
                    handler.post(() -> callback.onSuccess(Collections.emptyList()));
                    return;
                }

                // 2. 해당 폴더의 파일 ID 목록 검색
                String query = "'" + folderId + "' in parents and trashed = false";

                List<File> fileList = new ArrayList<>();
                String pageToken = null;
                do {
                    FileList result = driveService.files().list()
                            .setQ(query)
                            .setSpaces("drive")
                            .setFields("nextPageToken, files(id)") // ID만 가져오도록 최적화
                            .setPageToken(pageToken)
                            .execute();

                    if (result.getFiles() != null)
                    {
                        fileList.addAll(result.getFiles());
                    }
                    pageToken = result.getNextPageToken();
                } while (pageToken != null);

                if (fileList.isEmpty())
                {
                    Log.d(TAG, "No files found in folder: " + fullPath);
                    handler.post(() -> callback.onSuccess(Collections.emptyList()));
                    return;
                }

                // 3. 각 파일의 내용을 순차적으로 읽어와 결과 목록에 추가합니다.
                for (File file : fileList)
                {
                    final String fileId = file.getId();

                    try
                    {
                        // Drive API 호출 (Blocking I/O)
                        InputStream inputStream = driveService.files().get(fileId).executeMediaAsInputStream();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1)
                        {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        String content = outputStream.toString("UTF-8");
                        // Pair<String, String> 객체를 생성하여 리스트에 추가
                        contentList.add(new Pair<>(fileId, content));
                        Log.d(TAG, "Finished reading content for ID: " + fileId);

                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, "Error reading content for file ID: " + fileId, e);
                        // 한 파일의 오류가 전체를 실패시키지 않도록 오류는 기록하고 계속 진행합니다.
                        errors.add(e);
                    }
                }

                // 4. 모든 파일 처리가 완료된 후 결과 반환
                if (!errors.isEmpty())
                {
                    // 최소한 하나의 파일이라도 읽기 오류가 발생했다면 실패 처리 (첫 번째 오류만 보고)
                    handler.post(() -> callback.onFailure(errors.get(0)));
                }
                else
                {
                    Log.d(TAG, "Finished reading all file contents. Total: " + contentList.size());
                    handler.post(() -> callback.onSuccess(contentList));
                }

            }
            catch (Exception e)
            {
                // 폴더 검색 또는 파일 목록 조회 중 오류 발생
                Log.e(TAG, "Error reading all file contents from path: " + fullPath, e);
                handler.post(() -> callback.onFailure(e));
            }
        });
    }

    // =========================================================================
    // 폴더 관리 헬퍼 메서드
    // =========================================================================

    /**
     * 경로에 지정된 모든 폴더를 찾거나 없으면 새로 생성하여 가장 하위 폴더의 ID를 반환합니다.
     * @param path 생성할 폴더의 전체 경로 (예: "Roamio/meta/plan").
     * @return 가장 하위 폴더의 ID, 실패 시 null.
     * @throws IOException Drive API 호출 중 오류 발생 시.
     */
    private String getOrCreateFolderIdByPath(String path) throws IOException
    {
        String[] folderNames = path.split("/");
        String parentId = null; // null은 My Drive 루트를 의미

        for (String folderName : folderNames)
        {
            if (folderName.isEmpty()) continue;

            // 현재 폴더 이름을 parentId 내에서 찾거나 생성합니다.
            String currentFolderId = findOrCreateFolder(folderName, parentId);

            if (currentFolderId == null)
            {
                // 경로 상의 어떤 폴더라도 실패하면 전체 경로 생성 실패
                Log.e(TAG, "Failed to find or create folder: " + folderName + " in path: " + path);
                return null;
            }
            // 다음 폴더를 검색/생성할 때 현재 폴더 ID를 부모로 사용
            parentId = currentFolderId;
        }
        return parentId;
    }

    /**
     * 경로에 지정된 모든 폴더를 찾고 가장 하위 폴더의 ID를 반환합니다. (생성하지 않음)
     * @param path 찾을 폴더의 전체 경로 (예: "Roamio/meta/plan").
     * @return 가장 하위 폴더의 ID, 폴더가 존재하지 않으면 null.
     * @throws IOException Drive API 호출 중 오류 발생 시.
     */
    private String getFolderIdByPath(String path) throws IOException
    {
        String[] folderNames = path.split("/");
        String parentId = null;

        for (String folderName : folderNames)
        {
            if (folderName.isEmpty()) continue;

            String currentFolderId = findFolder(folderName, parentId);

            if (currentFolderId == null)
            {
                // 경로 상의 어떤 폴더라도 없으면 null 반환
                return null;
            }
            parentId = currentFolderId;
        }
        return parentId;
    }

    /**
     * 주어진 부모 ID 내에서 특정 이름의 폴더를 찾거나 없으면 새로 생성하여 ID를 반환합니다.
     * @param folderName 찾거나 생성할 폴더 이름.
     * @param parentId 폴더를 검색할 부모 폴더 ID (루트에서 검색 시 null).
     * @return 폴더 ID, 오류 발생 시 null.
     * @throws IOException Drive API 호출 중 오류 발생 시.
     */
    private String findOrCreateFolder(String folderName, String parentId) throws IOException
    {
        String existingId = findFolder(folderName, parentId);
        if (existingId != null)
        {
            return existingId; // 이미 존재하면 ID 반환
        }

        // 폴더가 없으면 새로 생성
        String folderMimeType = "application/vnd.google-apps.folder";
        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType(folderMimeType);

        if (parentId != null)
        {
            folderMetadata.setParents(Collections.singletonList(parentId));
        }

        File folder = driveService.files().create(folderMetadata)
                .setFields("id")
                .execute();

        Log.d(TAG, "Folder '" + folderName + "' created. ID: " + folder.getId() + (parentId != null ? " in parent " + parentId : " at root"));
        return folder.getId();
    }

    /**
     * 주어진 부모 ID 내에서 특정 이름의 폴더를 찾고 ID를 반환합니다. (생성하지 않음)
     * @param folderName 찾을 폴더 이름.
     * @param parentId 폴더를 검색할 부모 폴더 ID (루트에서 검색 시 null).
     * @return 폴더 ID, 없으면 null.
     * @throws IOException Drive API 호출 중 오류 발생 시.
     */
    private String findFolder(String folderName, String parentId) throws IOException
    {
        String folderMimeType = "application/vnd.google-apps.folder";
        String query = String.format("name = '%s' and mimeType = '%s' and trashed = false", folderName, folderMimeType);

        if (parentId != null)
        {
            // 특정 부모 내에서 찾기
            query += String.format(" and '%s' in parents", parentId);
        } else {
            // 루트에서 찾기 (parent 필드가 없는 파일만 검색)
            // Drive API는 'in parents'가 없을 때 루트에 있는 파일도 검색하므로,
            // 별도의 명시적 쿼리 없이 검색합니다.
        }

        FileList result = driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id)")
                .execute();

        List<File> files = result.getFiles();

        if (files != null && !files.isEmpty())
        {
            // 찾은 폴더 ID 반환
            return files.get(0).getId();
        }
        return null; // 폴더를 찾지 못함
    }
}