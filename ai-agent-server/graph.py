import re
from typing import List, TypedDict

from langgraph.graph import StateGraph, START, END

from openrouter_client import call_openrouter


DEFAULT_LIMIT = 4
MIN_LIMIT = 3
MAX_LIMIT = 4
MIN_TAG_LEN = 2
MAX_TAG_LEN = 10

BANNED_TAGS = {
    "요리", "레시피", "음식", "맛집", "먹방", "간식", "집밥", "자취요리"
}


class HashtagState(TypedDict, total=False):
    title: str
    intro: str
    ingredients: str
    steps: str
    limit: int
    raw_hashtags: List[str]
    hashtags: List[str]
    retry_count: int
    error: str


def safe(value: str | None) -> str:
    return value.strip() if value else ""


def normalize_limit(limit: int | None) -> int:
    if limit is None:
        return DEFAULT_LIMIT
    if limit <= MIN_LIMIT:
        return MIN_LIMIT
    return min(limit, MAX_LIMIT)


def validate_input(state: HashtagState) -> HashtagState:
    title = safe(state.get("title"))
    steps = safe(state.get("steps"))

    if not title:
        raise ValueError("title은 필수입니다.")
    if not steps:
        raise ValueError("steps는 필수입니다.")

    return {
        **state,
        "title": title,
        "intro": safe(state.get("intro")),
        "ingredients": safe(state.get("ingredients")),
        "steps": steps,
        "limit": normalize_limit(state.get("limit")),
        "retry_count": state.get("retry_count", 0),
    }


def generate_hashtags(state: HashtagState) -> HashtagState:
    raw_hashtags = call_openrouter(state)
    return {
        **state,
        "raw_hashtags": raw_hashtags,
    }


def ensure_hash_prefix(tag: str) -> str:
    return tag if tag.startswith("#") else f"#{tag}"


def sanitize_body(tag: str) -> str:
    body = tag[1:] if tag.startswith("#") else tag
    body = re.sub(r"[^0-9A-Za-z가-힣]", "", body)
    return f"#{body}"


def trim_to_max_len(tag: str) -> str:
    body = tag[1:] if tag.startswith("#") else tag
    return f"#{body[:MAX_TAG_LEN]}"


def is_valid_len(tag: str) -> bool:
    body = tag[1:] if tag.startswith("#") else tag
    return MIN_TAG_LEN <= len(body) <= MAX_TAG_LEN


def contains_korean(tag: str) -> bool:
    body = tag[1:] if tag.startswith("#") else tag
    return bool(re.search(r"[가-힣]", body))


def is_not_banned(tag: str) -> bool:
    body = tag[1:] if tag.startswith("#") else tag
    return body not in BANNED_TAGS


def post_process(state: HashtagState) -> HashtagState:
    limit = state.get("limit", DEFAULT_LIMIT)
    raw_hashtags = state.get("raw_hashtags", [])

    result = []
    seen = set()

    for tag in raw_hashtags:
        if tag is None:
            continue

        normalized = str(tag).strip()
        if not normalized:
            continue

        normalized = re.sub(r"\s+", "", normalized)
        normalized = re.sub(r"[\"'`]", "", normalized)
        normalized = ensure_hash_prefix(normalized)
        normalized = sanitize_body(normalized)
        normalized = trim_to_max_len(normalized)

        if not is_valid_len(normalized):
            continue
        if not contains_korean(normalized):
            continue
        if not is_not_banned(normalized):
            continue
        if normalized in seen:
            continue

        seen.add(normalized)
        result.append(normalized)

        if len(result) >= limit:
            break

    return {
        **state,
        "hashtags": result,
    }


def should_retry_or_finish(state: HashtagState) -> str:
    limit = state.get("limit", DEFAULT_LIMIT)
    hashtags = state.get("hashtags", [])
    retry_count = state.get("retry_count", 0)

    if len(hashtags) >= limit:
        return "finish"

    if retry_count >= 2:
        return "finish"

    return "retry"


def increase_retry_count(state: HashtagState) -> HashtagState:
    return {
        **state,
        "retry_count": state.get("retry_count", 0) + 1,
    }


def build_graph():
    graph = StateGraph(HashtagState)

    graph.add_node("validate_input", validate_input)
    graph.add_node("generate_hashtags", generate_hashtags)
    graph.add_node("post_process", post_process)
    graph.add_node("increase_retry_count", increase_retry_count)

    graph.add_edge(START, "validate_input")
    graph.add_edge("validate_input", "generate_hashtags")
    graph.add_edge("generate_hashtags", "post_process")

    graph.add_conditional_edges(
        "post_process",
        should_retry_or_finish,
        {
            "retry": "increase_retry_count",
            "finish": END,
        },
    )

    graph.add_edge("increase_retry_count", "generate_hashtags")

    return graph.compile()


hashtag_graph = build_graph()