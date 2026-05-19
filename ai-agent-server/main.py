from fastapi import FastAPI, HTTPException
from graph import hashtag_graph
from schemas import HashtagRequest, HashtagResponse

app = FastAPI(title="15mintable AI Agent Server")


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/agent/hashtags", response_model=HashtagResponse)
def generate_hashtags(req: HashtagRequest):
    try:
        result = hashtag_graph.invoke(req.model_dump())
        return HashtagResponse(hashtags=result.get("hashtags", []))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))