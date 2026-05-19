import json
import os
import re
from typing import List

import requests
from dotenv import load_dotenv

load_dotenv()

OPENROUTER_API_KEY = os.getenv("OPENROUTER_API_KEY")
OPENROUTER_API_URL = os.getenv("OPENROUTER_API_URL", "https://openrouter.ai/api/v1/chat/completions")
OPENROUTER_MODEL = os.getenv("OPENROUTER_MODEL", "openrouter/free")
APP_URL = os.getenv("APP_URL", "http://localhost:8001")
APP_NAME = os.getenv("APP_NAME", "15mintable-agent")


def build_system_prompt(limit: int) -> str:
    return f"""
너는 한국어 요리 레시피 해시태그 생성기야.

반드시 JSON만 출력해.
설명, 마크다운, 코드블록은 절대 출력하지 마.

출력 형식:
{{"hashtags":["#...","#..."]}}

규칙:
- 해시태그는 정확히 {limit}개 생성
- 모든 해시태그는 반드시 한국어로 작성
- 각 해시태그는 '#' 제외 2~10자 이내
- 재료, 조리법, 상황, 맛, 난이도를 반영
- 너무 일반적인 단어는 피하기: 요리, 레시피, 음식, 맛집, 먹방
- 영어 해시태그 금지
""".strip()


def build_user_prompt(state: dict) -> str:
    return f"""
아래 레시피 정보를 보고 서비스에 어울리는 한국어 해시태그를 추천해줘.

[제목]
{state.get("title", "")}

[소개]
{state.get("intro", "")}

[재료]
{state.get("ingredients", "")}

[조리순서]
{state.get("steps", "")}
""".strip()


def extract_json_content(content: str) -> dict:
    content = re.sub(r"(?s)<think>.*?</think>", "", content).strip()

    if content.startswith("```json"):
        content = content[7:].strip()
    elif content.startswith("```"):
        content = content[3:].strip()

    if content.endswith("```"):
        content = content[:-3].strip()

    return json.loads(content)


def call_openrouter(state: dict) -> List[str]:
    if not OPENROUTER_API_KEY:
        raise RuntimeError("OPENROUTER_API_KEY가 설정되지 않았습니다.")

    limit = state.get("limit", 4)
    prompt = build_system_prompt(limit) + "\n\n----------------\n\n" + build_user_prompt(state)

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {OPENROUTER_API_KEY}",
        "HTTP-Referer": APP_URL,
        "X-Title": APP_NAME,
    }

    body = {
        "model": OPENROUTER_MODEL,
        "messages": [
            {"role": "user", "content": prompt}
        ],
        "temperature": 0.3,
    }

    response = requests.post(
        OPENROUTER_API_URL,
        headers=headers,
        json=body,
        timeout=30,
    )

    if not response.ok:
        raise RuntimeError(f"OpenRouter 호출 실패: {response.status_code} / {response.text}")

    root = response.json()
    choices = root.get("choices") or []

    if not choices:
        return []

    content = choices[0].get("message", {}).get("content", "")
    parsed = extract_json_content(content)

    hashtags = parsed.get("hashtags", [])
    return hashtags if isinstance(hashtags, list) else []