import random
import string
from firebase_config import db

# 1. 랜덤 페어링 코드 생성 (예: "K7M2P9")
def generate_code(length=6):
    chars = string.ascii_uppercase + string.digits  # 영문대문자 + 숫자
    return "".join(random.choices(chars, k=length))

# 2. 돌봄자가 가족 만들기 → 코드 발급
def create_family(caregiver_name):
    code = generate_code()
    # families 컬렉션에 저장
    family_ref = db.collection("families").document()
    family_ref.set({
        "pairing_code": code,
        "caregiver_name": caregiver_name
    })
    print(f"✅ 가족 생성됨! 페어링 코드: {code}")
    return code

# 3. 노인이 코드 입력 → 같은 가족으로 연결
def join_family(senior_name, code):
    # 입력한 코드와 일치하는 가족 찾기
    families = db.collection("families").where("pairing_code", "==", code).get()

    if not families:
        print("❌ 코드가 틀렸어요. 다시 확인해주세요.")
        return None

    family = families[0]
    family_id = family.id
    # users 컬렉션에 노인 등록
    db.collection("users").document().set({
        "name": senior_name,
        "role": "senior",
        "family_id": family_id
    })
    print(f"✅ {senior_name}님이 가족에 연결됐어요! (family_id: {family_id})")
    return family_id