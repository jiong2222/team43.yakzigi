# team43.yakzigi
오픈소스프로그래밍 1분반 팀43

# 약지기 (Yakzigi) — 가족·돌봄자를 위한 노인 복약 관리 시스템
> 약봉투 사진 한 장으로 약 정보를 자동 등록하고, 돌봄자-노인 분리형 UX로 복약을 관리하는 안드로이드 서비스

---

## 목차

1. [프로젝트 소개](#1-프로젝트-소개)
2. [주요 기능](#2-주요-기능)
3. [시스템 구조](#3-시스템-구조)
4. [기술 스택](#4-기술-스택)
5. [레포 폴더 구조](#5-레포-폴더-구조)
6. [설치 및 실행 방법](#6-설치-및-실행-방법)
7. [데이터 구조 (Firestore)](#7-데이터-구조-firestore)
8. [데이터셋](#8-데이터셋)
9. [팀 및 역할 분담](#9-팀-및-역할-분담)
10. [협업 규칙](#10-협업-규칙)

---

## 1. 프로젝트 소개

초고령사회 진입으로 여러 약을 동시에 복용하는 노인이 늘면서, 약물 오용·중복 복용·복약 누락이 심각한 의료 문제로 떠오르고 있습니다. 기존 복약 앱은 노인이 직접 약을 입력하고 조작해야 해서 고령층에게 부담이 큽니다.

**약지기**는 돌봄자가 약봉투를 촬영하면 OCR로 약 정보를 자동 인식하고, 노인은 알람을 듣고 화면을 한 번 누르는 최소한의 동작만으로 복약을 관리할 수 있는 서비스입니다.

---

## 2. 주요 기능

| 기능 | 설명 |
|------|------|
| 약봉투 OCR 인식 | 약봉투 사진에서 약품명·복용일수·횟수·복용시점을 자동 추출 |
| 가족 페어링 | 돌봄자가 코드를 생성하고 노인이 입력해 가족으로 연결 |
| 복약 정보 등록 | 인식된 약 정보를 Firestore에 저장 |
| 복약 알람 | 지정 시각에 알람과 음성(TTS)으로 복약 안내 |
| 복용 확인 | 노인이 복용 후 버튼을 눌러 복용 이력 기록 |
| 복용 이력 조회 | 돌봄자가 노인의 복약 여부를 확인 |

---

## 3. 시스템 구조

```
[돌봄자 앱]                    [OCR 서버 / Python]
  ① 약봉투 촬영  ─────────────▶  ② OCR 인식 (파인튜닝 모델)
  (CameraX)                       ③ 파싱 + 약품정보 보강
                                      │ JSON
                                      ▼
                            [백엔드 / Firebase Firestore]
                              약 정보 저장 · 알람 시각 · 복용 이력 · 페어링
                                      │
            ┌─────────────────────────┼─────────────────────────┐
            ▼                         ▼                         ▼
     [노인 앱]                  [노인 앱]                  [돌봄자 앱]
     ④ 알람·음성 안내           ⑤ 복용 완료 버튼           ⑥ 복용 이력 확인
     (AlarmManager·TTS)        → medication_logs 기록
```

---

## 4. 기술 스택

| 구분 | 기술 |
|------|------|
| 안드로이드 앱 | Kotlin, Jetpack Compose, Android Studio, CameraX, AlarmManager, TextToSpeech |
| OCR / 파싱 | Python, PaddleOCR (PP-OCRv3 파인튜닝), OpenCV, 정규식 |
| 약품 정보 | 의약품개요 API |
| 백엔드 | Python, firebase-admin, Firebase Firestore / Authentication |
| 데이터 라벨링 | Label Studio |
| 협업 | GitHub (브랜치 + Pull Request) |

---

## 5. 레포 폴더 구조

```
team43.yakzigi/
├── android/                         # 안드로이드 앱 (원예지 담당)
│   └── app/src/main/java/com/example/yakzigi/
│       ├── MainActivity.kt          # 앱 진입점, 화면 네비게이션
│       ├── AlarmReceiver.kt         # 복약 알람 수신·알림 표시
│       ├── MedicineData.kt          # 약 데이터 모델 (data class Medicine)
│       └── screens/
│           ├── HomeScreen.kt        # 홈 (돌봄자/노인 선택)
│           ├── CaregiverScreen.kt   # 돌봄자 화면 (약 등록·저장)
│           ├── ElderlyScreen.kt     # 노인 화면 (약 표시·복용 확인)
│           └── PairingScreen.kt     # 가족 페어링 화면
│
├── ocr/                             # OCR·파싱·약품DB (황지오 담당)
│   ├── data/ocr_dataset/            # 약봉투 데이터셋 (train/test)
│   └── parser/
│       ├── train_ocr.ipynb          # OCR 모델 학습 노트북
│       └── config/                  # 학습 설정 + 추론 모델 가중치
│
├── backend/                         # 백엔드 (김준영 담당)
│   ├── firebase_config.py           # Firebase 연결
│   ├── pairing.py                   # 가족 페어링
│   ├── medications.py               # 약 정보 저장
│   ├── alarm.py                     # 알람 시각 관리·약 조회
│   ├── logs.py                      # 복용 이력 기록·조회
│   └── README.md                    # 백엔드 상세 문서
│
└── README.md                        # (이 문서)
```

---

## 6. 설치 및 실행 방법

### 6-1. 레포 클론

```bash
git clone https://github.com/jiong2222/team43.yakzigi.git
cd team43.yakzigi
```

### 6-2. 백엔드 실행 (Python)

**요구사항:** Python 3.9 이상

```bash
cd backend
pip install firebase-admin
```

Firebase 비밀 키 파일(`serviceAccountKey.json`)을 `backend/` 폴더에 넣습니다.
(이 파일은 보안상 `.gitignore`로 제외되어 있으므로, 팀 Firebase 콘솔에서 발급받아 직접 추가)

```bash
# 페어링 기능 테스트
python pairing.py

# 전체 흐름 통합 테스트
python test_local.py
```

### 6-3. 앱 실행 (안드로이드)

**요구사항:** Android Studio, 안드로이드 에뮬레이터 또는 실기기

1. 앱 코드가 있는 브랜치로 이동
   ```bash
   git checkout yeji/app
   ```
2. Android Studio에서 `android/` 폴더 열기
3. Firebase 설정 파일(`google-services.json`)을 `android/app/`에 넣기
   (팀 Firebase 콘솔 → 안드로이드 앱 등록 → 다운로드)
4. Gradle Sync 완료 후 ▶ Run 버튼으로 실행

### 6-4. OCR 모델 (Python / Jupyter)

```bash
cd ocr/parser
# train_ocr.ipynb 를 Jupyter 또는 Colab에서 실행
```
학습된 추론 모델은 `ocr/parser/config/`에 포함되어 있습니다.

---

## 7. 데이터 구조 (Firestore)

백엔드와 앱이 공통으로 사용하는 4개 컬렉션입니다. (설계: 김준영)

### `families` — 가족 단위
| 필드 | 타입 | 설명 |
|------|------|------|
| (문서 ID) | string | family_id |
| pairing_code | string | 페어링 코드 |
| caregiver_name | string | 돌봄자 이름 |

### `users` — 사용자
| 필드 | 타입 | 설명 |
|------|------|------|
| name | string | 이름 |
| role | string | "caregiver" 또는 "senior" |
| family_id | string | 소속 가족 |
| fcm_token | string | 푸시 알림용 (선택) |

### `medications` — 약 정보
| 필드 | 타입 | 설명 |
|------|------|------|
| family_id | string | 소속 가족 |
| senior_id | string | 약 주인(노인) |
| name | string | 약품명 |
| duration_days | int | 복용 일수 |
| times_per_day | int | 1일 복용 횟수 |
| take_timing | string | 복용 시점 (예: "식후 30분") |
| alarm_times | array | 알람 시각 리스트 |

### `medication_logs` — 복용 이력
| 필드 | 타입 | 설명 |
|------|------|------|
| senior_id | string | 노인 ID |
| medication_id | string | 약 ID |
| medication_name | string | 약품명 |
| status | string | "taken" / "missed" |
| taken_at | timestamp | 복용 시각 |

> 앱이 Firebase에 데이터를 저장할 때는 위 필드명을 정확히 따라야 합니다.
> 자세한 통합 가이드는 `backend/README.md` 참고.

---

## 8. 데이터셋

| 항목 | 내용 |
|------|------|
| 종류 | 한국 약국 조제 약봉투 사진 |
| 생성 방식 | 실제 약봉투를 직접 촬영하여 자체 구축 |
| 규모 | 총 40장 (train 32장 / test 8장) |
| 라벨링 툴 | Label Studio (bounding box + 텍스트 transcription) |
| 저장 위치 | `ocr/data/ocr_dataset/` |
| 학습 모델 | PP-OCRv3 기반 파인튜닝 (50 epoch) |

---

## 9. 팀 및 역할 분담

| 이름 | 역할 | 담당 |
|------|------|------|
| 황지오 (팀장) | OCR·파싱·약품DB | 데이터셋 구축, OCR 모델 파인튜닝, 파싱, 약품정보 API, OCR 서버 |
| 원예지 | 안드로이드 앱 | 돌봄자/노인/페어링 화면, 알람 |
| 김준영 | 백엔드·통합 | Firestore 데이터 구조 설계, 페어링/약저장/이력 로직, 시스템 통합 |

---

## 10. 협업 규칙

### 브랜치 전략
- `main` 브랜치 기준으로 작업
- 기능별 브랜치 분리 → Pull Request → **다른 팀원이 확인 후 merge**
- main에 직접 push 금지

### 커밋 메시지 규칙
| 접두사 | 의미 |
|--------|------|
| `feat:` | 기능 추가 |
| `fix:` | 버그 수정 |
| `docs:` | 문서 수정 |
| `refactor:` | 코드 구조 개선 |
| `chore:` | 기타 설정 |

예시: `git commit -m "feat: 가족 페어링 기능 추가"`

---

## 링크

- GitHub: https://github.com/jiong2222/team43.yakzigi
- 백엔드 상세 문서: [backend/README.md](backend/README.md)
