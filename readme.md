# ✈️ ROAMIO (로미오) — Serverless AI 여행 플래너

"Destination, Your Travel"  
서버 비용 0원, 개인정보 보안 강화, 90MB 경량화된 AI 여행 플래너  
사용자의 Google Drive를 데이터베이스로 활용하는 Unhosted Architecture 기반의 Android 네이티브 앱입니다.

## 프로젝트 개요
ROAMIO는 인터넷 서핑으로 소모되는 여행 계획 시간을 줄이기 위해 기획된 생성형 AI 기반 여행 플래너입니다. Google Gemini(Generative AI)를 활용해 사용자의 입력(기간, 동행, 스타일 등)을 반영한 맞춤형 여행 코스를 즉시 생성하며, 사용자의 Google Drive(AppData)에 JSON으로 데이터를 저장해 서버 운영비를 없앴습니다.

개발 기간: 2025-2학기 (5주, 애자일/스크럼)  
개발 인원: 5명 (Android Native Team)

## 핵심 기능
1. AI 여행 플래닝 & 추천
   - Google Gemini API 연동으로 초개인화 일정 생성
   - DTO 파서 + Builder 패턴으로 비정형 AI 응답 안정적 파싱
   - 재사용 가능한 AI 모듈(주변 관광지, 맛집 추천 등)
2. Unhosted Architecture (Serverless)
   - Google Drive AppData에 JSON 형태로 CRUD 저장/동기화
   - 개발사 서버 없이 운영비용 0원 달성
   - 개인정보는 사용자의 클라우드에만 저장(Privacy First)
3. 사용자 친화적 UI/UX
   - 마스코트 '로미' 인터랙션, 로딩 GIF, 부드러운 전환 애니메이션
   - 탭 네비게이션, Dynamic View Generation
   - ConstraintLayout 기반의 반응형 레이아웃
4. 경량화 & 오프라인 고려
   - 앱 용량 약 90MB 목표, 로컬 캐싱(SQLite)으로 오프라인 사용성 보강

## ROAMIO의 강점
- 유지비용 0원: 자체 서버 없음, Google API 기반 운영(단, Gemini API 사용 시 비용 정책 확인 필요)  
- 보안성: 사용자의 데이터는 개인 Drive에만 저장되어 개인정보 노출 리스크 감소  
- 경량성: 저용량 앱 설계로 다양한 디바이스 호환성 확보

## 기술 스택
- 언어: Java (Android SDK)  
- 아키텍처: Unhosted (Client-Centric), MVVM  
- AI: Google Gemini API  
- 인증/저장: Google OAuth 2.0, Google Drive API v3 (AppData)  
- 로컬 DB: SQLite (오프라인 캐시)  
- UI: Android XML
- 도구: Android Studio, GitHub, Figma, Slack, Notion

## 설치 및 실행 (일반 유저용)
리포지토리의 apk 폴더 내 apk 다운로드 후 설치

## 설치 및 실행 (개발자 가이드)
1. 레포지토리 클론
   - git clone [https://github.com/your-repo/roamio-android.git](https://github.com/Android-Programming-Team1/Roamio.git)
2. Android Studio에서 프로젝트 열기 및 Gradle Sync
3. Google Cloud 설정
   - Gemini API 활성화(데모 버전에 따라 필요)
   - OAuth 2.0 클라이언트 생성(패키지명 + SHA-1 등록)
   - Drive API v3 활성화
4. 로컬 설정 (데모 버전에 따라 필요, local.properties)
   - 아래 예시발)  
- 주민재 — Scrum Master & Full-stack (아키텍처, OAuth/Drive 연동, 실제 UI)  
- 조윤주 — Co-Developer (서브 기능, 캐쉬 관리, QA)  
- 강진아 — UI/UX Design (로고, 테마, 마스코트, 디자인 데모)  
- 유찬우 — AI Developer (데이터 분석 및 AI유틸리티 개발)

## 라이선스
MIT License

