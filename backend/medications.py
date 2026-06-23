from firebase_config import db

# 황지오가 보내준 JSON을 받아서 Firestore에 저장
def save_medications(family_id, senior_id, ocr_result):
    medicines = ocr_result["medicines"]  # 약 목록 꺼내기
    saved_count = 0

    for med in medicines:
        # medications 컬렉션에 약 하나씩 저장
        db.collection("medications").document().set({
            "family_id": family_id,
            "senior_id": senior_id,
            "name": med["name"],                    # 약 이름
            "duration_days": med["duration_days"],  # 복용 일수
            "times_per_day": med["times_per_day"],  # 하루 횟수
            "take_timing": med["take_timing"],      # 복용 시점
            "alarm_times": []                       # 알람 시각 (나중에 앱에서 채움)
        })
        saved_count += 1
        print(f"  💊 저장됨: {med['name']}")

    print(f"✅ 약 {saved_count}개 저장 완료!")
    return saved_count