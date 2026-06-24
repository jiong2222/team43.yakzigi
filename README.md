약지기 Android App

가족·돌봄자를 위한 노인 복약 관리 시스템 — Android 앱 파트

담당: 원예지 (Android App / UI)

1. 앱이 하는 일

돌봄자와 노인이 사용할 Android 애플리케이션입니다.

사용자는 회원가입 및 로그인 후 가족 페어링을 수행할 수 있으며, 약 정보를 등록하고 복약 알림을 받을 수 있습니다.

Firebase Authentication과 Firestore를 이용하여 사용자 정보와 복약 정보를 관리합니다.

[OCR 서버]
     ↓
[Firebase Firestore]
     ↓
[Android App]
     ↓
사용자

2. 파일 구조
android/
├── app/
│   ├── src/main/java/com/example/yakzigi/
│   │   ├── screens/
│   │   │   ├── HomeScreen.kt
│   │   │   ├── CaregiverScreen.kt
│   │   │   ├── ElderlyScreen.kt
│   │   │   └── PairingScreen.kt
│   │   │
│   │   ├── MainActivity.kt
│   │   ├── AlarmReceiver.kt
│   │   ├── Medicine.kt
│   │   └── MedicineData.kt
│   │
│   └── google-services.json
│
├── build.gradle.kts
└── settings.gradle.kts

3. 실행 준비
3-1. Firebase 설정
Firebase 프로젝트 생성 후 Android 앱 등록
google-services.json 파일을 다운로드하여 아래 위치에 저장
app/google-services.json

3-2. Firebase 기능 활성화
Firebase Authentication
Cloud Firestore

3-3. 실행
Android Studio에서 프로젝트 실행

4. 주요 기능
<로그인 / 회원가입>

<Firebase Authentication 기반>
회원가입
로그인
사용자 인증
가족 페어링
가족 코드 생성
가족 코드 입력
돌봄자와 노인 연결
약 정보 등록

<등록 항목>
약 이름
복용 일수
하루 복용 횟수
복용 시점
알람 시간
복약 정보 저장

<Cloud Firestore 저장>

<복약 알림>
Android AlarmManager 사용
알람 예약
복약 알림 생성

5. 화면 구성
HomeScreen
돌봄자 선택
노인 선택
CaregiverScreen
약 정보 등록
Firestore 저장
알람 설정
ElderlyScreen
복약 정보 확인
PairingScreen
가족 코드 생성
가족 코드 입력

6. 팀원 통합 가이드
Firebase 데이터 사용

<사용 컬렉션>
users
families
medications
medication_logs

앱은 Firestore 데이터를 읽어 사용자 화면에 표시합니다.

OCR 연동
OCR 서버에서 추출된 약 정보를 받아 자동 입력 기능을 추가합니다.

7. 진행 상황

✅ 홈 화면 구현

✅ Firebase Authentication 연동

✅ Firestore 연동

✅ 가족 페어링 구현

✅ 복약 정보 저장

✅ Android 알림 구현

⬜ OCR API 연동

⬜ 통합 테스트
