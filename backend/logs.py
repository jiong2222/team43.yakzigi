from firebase_config import db
from datetime import datetime

# 1. 복용 완료 기록 (노인이 "먹었어요" 버튼 누르면)
def record_taken(senior_id, medication_id, medication_name):
    db.collection("medication_logs").document().set({
        "senior_id": senior_id,
        "medication_id": medication_id,
        "medication_name": medication_name,
        "status": "taken",                    # 복용함
        "taken_at": datetime.now().isoformat() # 먹은 시각
    })
    print(f"✅ 복용 기록됨: {medication_name} ({datetime.now().strftime('%H:%M')})")
    return True


# 2. 복용 이력 조회 (돌봄자가 확인)
def get_logs(senior_id):
    logs = db.collection("medication_logs").where("senior_id", "==", senior_id).get()

    result = []
    for log in logs:
        data = log.to_dict()
        result.append({
            "name": data["medication_name"],
            "status": data["status"],
            "taken_at": data.get("taken_at", "")
        })

    print(f"📋 복용 기록 {len(result)}개")
    for r in result:
        # 시각을 보기 좋게 (예: 2026-06-23T08:05 → 06-23 08:05)
        time_str = r["taken_at"][5:16].replace("T", " ") if r["taken_at"] else "-"
        mark = "✅" if r["status"] == "taken" else "❌"
        print(f"  {mark} {r['name']} - {time_str}")
    return result