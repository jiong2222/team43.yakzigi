# 약지기 OCR 서버 실행 가이드

## 🚨 [중요] 코랩 환경 버전 충돌 및 실행 순서 안내
PaddleOCR 및 가상환경 내부 패키지 버전 마찰(특히 Numpy 2.0+ 이슈)을 방지하기 위해 **반드시 아래 가이드라인의 순서와 런타임 재시작을 준수**해 주세요.

---

## 1. 코랩 환경 세팅 및 설치

### 1) 코랩 셀 1 — 핵심 OCR 엔진 설치 (버전 고정)
가장 먼저 버전 충돌을 막기 위해 아래 패키지들을 고정 버전으로 설치합니다.
```bash
!pip install paddleocr==2.7.3 paddlepaddle==2.6.2 "numpy<2.0.0" -q

```

### 2) 🔥 [필수] 코랩 런타임 재시작

1번 셀 설치가 끝난 후, 다음 단계로 넘어가기 전에 반드시 코랩 상단 메뉴의 [런타임] ➡️ [세션 다시 시작] (단축키: `Ctrl + M .`)을 실행해 주세요.

> 세션을 재시작하지 않으면 C-바이너리 컴파일 버전에 마찰이 생겨 `ImportError`가 발생합니다.

### 3) 코랩 셀 2 — 웹 서버 관련 패키지 설치

런타임이 재시작된 것을 확인한 뒤, 아래 명령어로 API 구동 패키지들을 설치합니다.

```bash
!pip install fastapi uvicorn python-multipart pyngrok nest_asyncio -q

```

### 4) 코랩 셀 3 — 구글 드라이브 마운트

지오님이 빌드한 학습 가중치와 사전을 로드하기 위해 드라이브를 연결합니다.

```python
from google.colab import drive
drive.mount("/content/drive")

```

---

## 2. ngrok 토큰 발급 및 세팅

1. [ngrok 공식 홈페이지](https://dashboard.ngrok.com) 가입 후 `Your Authtoken` 메뉴에서 문자열 복사
2. `ocr_server.py` 코드 하단의 `ngrok.set_auth_token("YOUR_NGROK_TOKEN")` 공간에 해당 토큰 입력

---

## 3. 코랩 셀 4 — OCR API 서버 구동

```bash
# 본인의 워킹 디렉토리 경로에 맞춰 cd 이동 후 실행하세요.
!python ocr_server.py

```

정상 구동되면 터미널 최하단에 다음과 같은 ngrok 포워딩 주소가 출력됩니다:

```text
✅ 엔드포인트: [https://xxxx-xxxx.ngrok-free.app/ocr/parse](https://xxxx-xxxx.ngrok-free.app/ocr/parse)
⚠️ 앱에서 요청 시 헤더 추가 필요: ngrok-skip-browser-warning: true

```

해당 외부 주소의 포트 엔드포인트를 복사하여 백엔드(김준영님) 시스템에 연동하시면 됩니다.

---

## 4. 파일 구조

구글 드라이브 마운트 경로 및 깃 레포지토리는 아래 구조를 유지해야 엔진이 정상 로드됩니다.

```text
깃레포/ocr/
├── ocr_server.py            ← 최종 FastAPI 기반 백엔드 연동 파일
├── README.md                ← 본 설명서
└── parser/
    ├── korean_dict.txt      # 한국어 가중치 사전 자전 파일
    └── iter_epoch_50/       # 파인튜닝 가중치 폴더
        ├── inference.pdmodel
        ├── inference.pdiparams
        └── inference.pdiparams.info

```

### ⚠️ 경로 설정 체크 (`ocr_server.py` 상단)

본인의 구글 드라이브 내 실제 파일 저장 위치와 일치하는지 확인 후 수정해 주세요:

```python
REC_MODEL_DIR      = "/content/drive/MyDrive/OCR/team43.yakzigi/ocr/parser/iter_epoch_50"
REC_CHAR_DICT_PATH = "/content/drive/MyDrive/OCR/team43.yakzigi/ocr/parser/korean_dict.txt"

```

---

## 5. 테스트 (curl)

로컬 터미널이나 포스트맨을 활용해 서버가 정상적으로 파싱 결과 JSON을 반환하는지 검증합니다.

```bash
curl -X POST "[https://xxxx-xxxx.ngrok-free.app/ocr/parse](https://xxxx-xxxx.ngrok-free.app/ocr/parse)" \
     -H "ngrok-skip-browser-warning: true" \
     -F "file=@prescript_example.jpg"

```

---

## 6. 최종 응답 규격 예시

가로 라인 병합 및 노이즈 필터링이 적용되어, 약품명 뒤에 붙은 복용량 데이터(예: `137`)를 기반으로 일수와 횟수가 안전하게 정제되어 출력됩니다.

```json
{
  "total_medicines_count": 2,
  "medicines": [
    {
      "name": "노르믹스정",
      "duration_days": 7,
      "times_per_day": 3,
      "take_timing": "식후 30분"
    },
    {
      "name": "벤투룩스30mg캡슐",
      "duration_days": 7,
      "times_per_day": 3,
      "take_timing": "식후 30분"
    }
  ],
  "_debug_ocr_raw": [
    "노르믹스정[02g/1정] 137",
    "벤투룩스30mg캡슐 137"
  ]
}

```

* `_debug_ocr_raw`: 라인 병합 후 텍스트 필터링 엔진이 전처리한 원본 문자열 배열입니다. (디버깅 완료 후 배포 단계에서 제거 가능)

---

## 7. 자주 겪는 예외 및 오류 해결

| 오류 메시지 / 증상 | 원인 | 해결 방안 |
| --- | --- | --- |
| `ImportError: Python version mismatch` 또는 `libpaddle.so` 관련 충돌 | 코랩 내 기본 파이썬 환경과 패키지 바이너리 마찰 | **Step 1 실행 후 반드시 [런타임 다시 시작]**을 누르고 Step 2로 진입했는지 재확인하세요. |
| `FileNotFoundError: rec_model_dir` | 구글 드라이브 연동 해제 혹은 마운트 경로 오기입 | 좌측 폴더 모양 아이콘에서 `drive/MyDrive/` 하위 경로를 새로고침하여 실제 파일 명칭과 매칭하세요. |
| 영수증 항목(`계산서`, `총수납금액` 등)이 약품으로 오인됨 | 영수증 내부 단어 유출 및 가로선 결합 노이즈 | `ocr_server.py` 내부의 `exclude_keywords` 블랙리스트 배열에 필터링할 영수증 특이 단어를 추가해 주세요. |

```

```
