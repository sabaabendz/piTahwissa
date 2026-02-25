from __future__ import annotations

import base64
import io
from typing import List

import face_recognition
import numpy as np
from fastapi import HTTPException
from PIL import Image


def extract_embedding(image_base64: str) -> List[float]:
    if not image_base64 or not isinstance(image_base64, str):
        raise HTTPException(status_code=400, detail="Invalid image")

    payload = image_base64.strip()
    if "," in payload and payload.lower().startswith("data:image"):
        payload = payload.split(",", 1)[1]

    try:
        image_bytes = base64.b64decode(payload, validate=True)
        pil_image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
        image_array = np.array(pil_image)
    except Exception as exc:
        raise HTTPException(status_code=400, detail="Invalid image") from exc

    face_locations = face_recognition.face_locations(image_array)

    if len(face_locations) == 0:
        raise HTTPException(status_code=400, detail="No face detected")

    if len(face_locations) > 1:
        raise HTTPException(status_code=400, detail="Multiple faces detected")

    encodings = face_recognition.face_encodings(image_array, known_face_locations=face_locations)
    if not encodings:
        raise HTTPException(status_code=400, detail="Could not extract face embedding")

    return encodings[0].astype(float).tolist()


def compare_embeddings(embedding1: List[float], embedding2: List[float]) -> float:
    first = np.asarray(embedding1, dtype=float)
    second = np.asarray(embedding2, dtype=float)

    if first.shape != second.shape:
        raise HTTPException(status_code=400, detail="Embedding dimensions do not match")

    norm_first = np.linalg.norm(first)
    norm_second = np.linalg.norm(second)

    if norm_first == 0.0 or norm_second == 0.0:
        raise HTTPException(status_code=400, detail="Invalid embedding values")

    cosine = float(np.dot(first, second) / (norm_first * norm_second))
    similarity = (cosine + 1.0) / 2.0
    return float(np.clip(similarity, 0.0, 1.0))
