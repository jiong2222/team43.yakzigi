from firebase_config import db

# 특정 약에 알람 시각 저장하기
def set_alarm_times(medication_id, alarm_times):
    # alarm_times 예시: ["08:00", "20:00"]
    med_ref = db.collection("medications").document(medication_id)

    # 그 약이 진짜 있는지 확인
    if not med_ref.get().exists:
        print("❌ 해당 약을 찾을 수 없어요.")
        return False

    # 알람 시각 업데이트
    med_ref.update({
        "alarm_times": alarm_times
    })
    print(f"✅ 알람 시각 저장됨: {alarm_times}")
    return True


# 한 가족(노인)의 모든 약 + 알람 시각 조회하기
def get_medications(senior_id):
    meds = db.collection("medications").where("senior_id", "==", senior_id).get()

    result = []
    for med in meds:
        data = med.to_dict()
        result.append({
            "id": med.id,
            "name": data["name"],
            "times_per_day": data["times_per_day"],
            "alarm_times": data.get("alarm_times", [])
        })

    print(f"📋 약 {len(result)}개 조회됨")
    for r in result:
        print(f"  - {r['name']} (하루 {r['times_per_day']}번) 알람: {r['alarm_times']}")
    return result