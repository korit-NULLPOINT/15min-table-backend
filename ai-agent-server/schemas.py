from pydantic import BaseModel, Field
from typing import List, Optional


class HashtagRequest(BaseModel):
    title: str
    intro: Optional[str] = ""
    ingredients: Optional[str] = ""
    steps: str
    limit: Optional[int] = 4


class HashtagResponse(BaseModel):
    hashtags: List[str] = Field(default_factory=list)