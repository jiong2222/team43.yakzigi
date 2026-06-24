REC_MODEL_DIR      = "/content/drive/MyDrive/ocr/parser/config/iter_epoch_50"
REC_CHAR_DICT_PATH = "/content/drive/MyDrive//ocr/parser/korean_dict.txt"

import re, io, nest_asyncio, uvicorn
from fastapi import FastAPI, File, UploadFile
from fastapi.responses import JSONResponse
from paddleocr import PaddleOCR
from pyngrok import ngrok
from PIL import Image
import numpy as np
import requests as req_lib

DRUG_API_KEY = "YOUR_API_KEY"
# 보건복지부 관련 서비스로 정확한 가이드 매핑
DRUG_API_URL = "https://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList"

ocr_engine = PaddleOCR(
    use_angle_cls=True,
    lang="korean",
    rec_model_dir=REC_MODEL_DIR,
    rec_char_dict_path=REC_CHAR_DICT_PATH,
    show_log=False,
)

def correct_drug_name(raw_name: str) -> str:
    cleaned = raw_name
    cleaned = re.sub(r'캡슬', '캡슐', cleaned)
    cleaned = re.sub(r'n9|ng', 'mg', cleaned)
    cleaned = re.sub(r'(\d+)m\b', r'\1mg', cleaned)
    
    korean_only = re.sub(r'[^가-힣]', '', cleaned)
    search_keyword = korean_only[:6]
    
    if len(search_keyword) < 2:
        return cleaned

    try:
        resp = req_lib.get(DRUG_API_URL, params={
            "serviceKey": DRUG_API_KEY,
            "itemName": search_keyword,
            "type": "json",
            "numOfRows": 5,
        }, timeout=3)
        data = resp.json()
        
        # 공공데이터 JSON의 다양한 중첩 구조(item 또는 items) 안전하게 방어 파싱
        items_data = data.get("body", {}).get("items", [])
        items = []
        if isinstance(items_data, list):
            items = items_data
        elif isinstance(items_data, dict):
            items = items_data.get("item", [])
            if isinstance(items, dict): items = [items]

        if items:
            from difflib import get_close_matches
            candidates = [i["itemName"] for i in items]
            match = get_close_matches(cleaned, candidates, n=1, cutoff=0.3)
            return match[0] if match else candidates[0]
    except Exception:
        pass
    return cleaned

def _build_medicine(name, digits, timing):
    """ 처방전 고유의 3자리/4자리 복용량 수치를 정확하게 언팩하는 엔진 """
    d = re.sub(r'^0+', '', digits) or '0'
    tpd, dd = 3, 3 # 기본값 방어선
    
    if len(d) == 2:
        if d.endswith('0'):
            dd = int(d)
        else:
            tpd, dd = int(d[0]), int(d[1])
    elif len(d) == 3:
        # 예: "137" -> 하루 3회(d[1]), 총 7일분(d[2])
        tpd, dd = int(d[1]), int(d[2])
    elif len(d) >= 4:
        # 예: "1330" -> 하루 3회(d[1]), 총 30일분(d[2:])
        tpd, dd = int(d[1]), int(d[2:])
        
    corrected_name = correct_drug_name(name)
    return {"name": corrected_name, "duration_days": dd, "times_per_day": tpd, "take_timing": timing}

def extract_global_timing(lines):
    """ 처방전 전체 텍스트에서 공통 복용 타이밍을 스캔하는 함수 """
    for line in lines:
        if "식후" in line or "식사후" in line:
            m = re.search(r'(\d+)분', line)
            return f"식후 {m.group(1)}분" if m else "식후 30분"
        if "식전" in line or "식사전" in line:
            m = re.search(r'(\d+)분', line)
            return f"식전 {m.group(1)}분" if m else "식전 30분"
        if "취침전" in line or "취침시" in line:
            return "취침 전"
    return "식후 30분"

def get_clean_prescription_lines(ocr_result, y_thresh=16):
    """ [★핵심 추가] 따로 노는 약 이름과 수치 데이터를 가로선 기준으로 병합 """
    if not ocr_result or not ocr_result[0]: return []
    boxes = ocr_result[0]
    boxes.sort(key=lambda x: x[0][0][1]) # Y축 정렬
    
    combined_lines, current_line, current_y = [], [], None
    for box in boxes:
        coords, (text, score) = box[0], box[1]
        if score < 0.4: continue # 저품질 텍스트 제거
        box_y = coords[0][1]
        
        if current_y is None:
            current_y = box_y
            current_line.append((coords[0][0], text))
        elif abs(box_y - current_y) < y_thresh:
            current_line.append((coords[0][0], text))
        else:
            current_line.sort(key=lambda x: x[0]) # X축 정렬 후 병합
            combined_lines.append(" ".join([item[1] for item in current_line]))
            current_line, current_y = [(coords[0][0], text)], box_y
            
    if current_line:
        current_line.sort(key=lambda x: x[0])
        combined_lines.append(" ".join([item[1] for item in current_line]))
    return combined_lines

def parse_ocr_lines(lines, global_timing):
    medicines = []
    seen_names = set()
    
    # 영수증, 금액 관련 노이즈 단어 원천 차단 블랙리스트
    exclude_keywords = ["계산서", "영수증", "수납", "금액", "원", "번", "일자", "약국", "처방전", "발행", "기관", "합계"]

    for line in lines:
        line = line.strip()
        if not line or any(k in line for k in exclude_keywords): 
            continue

        # 우측 끝에 붙은 복용량 숫자 세트 분리 패턴 저격 (예: "노르믹스정... 137")
        match = re.search(r'^(.*?)\s*(\d{2,6})$', line)
        if match:
            raw_name, digits = match.group(1).strip(), match.group(2).strip()
            
            # 불필요 특수문자 및 잔재 수식어 정제
            name = re.sub(r'^[\*>\s\d]+', '', raw_name)
            name = re.sub(r'\[.*$', '', name).strip() # [02g/1정] 등 데브리 제거
            name = re.sub(r'약효.*$', '', name).strip()

            if len(name) >= 2 and any(k in name for k in ["정", "캡슐", "캡슬", "산", "액", "시럽", "mg", "슐"]):
                if name not in seen_names:
                    medicines.append(_build_medicine(name, digits, global_timing))
                    seen_names.add(name)
                    
    return medicines

def run_ocr(image_bytes):
    img = np.array(Image.open(io.BytesIO(image_bytes)).convert("RGB"))
    # 후처리 병합을 위해 raw 좌표 구조 전체를 리턴하도록 수정
    return ocr_engine.ocr(img, cls=True)

app = FastAPI(title="약지기 OCR API")

@app.get("/")
def health(): return {"status": "ok"}

@app.post("/ocr/parse")
async def ocr_parse(file: UploadFile = File(...)):
    content_type = file.content_type or ""
    if content_type and not content_type.startswith("image/"):
        return JSONResponse(status_code=400, content={"error": "이미지만 가능합니다"})
    try:
        # 1. OCR 좌표 데이터 수집
        raw_ocr_result = run_ocr(await file.read())
        
        # 2. 가로 라인 병합 정렬 수행
        merged_lines = get_clean_prescription_lines(raw_ocr_result)
        
        # 3. 전체 공통 타이밍 추출 및 파싱 연동
        global_timing = extract_global_timing(merged_lines)
        medicines = parse_ocr_lines(merged_lines, global_timing)
        
    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})
        
    return {
        "total_medicines_count": len(medicines), 
        "medicines": medicines, 
        "_debug_ocr_raw": merged_lines
    }

# Ngrok 및 Uvicorn 실행 구문 유지
ngrok.set_auth_token("YOUR_API_KEY") 
public_url = ngrok.connect(8000)
print(f"✅ 엔드포인트: {public_url}/ocr/parse")
print(f"⚠️  앱에서 요청 시 헤더 추가 필요: ngrok-skip-browser-warning: true")

config = uvicorn.Config(app, host="0.0.0.0", port=8000)
server = uvicorn.Server(config)
await server.serve()