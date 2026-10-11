from typing import Literal
from fastapi import FastAPI
from pydantic import BaseModel, Field
import numpy as np
from sklearn.ensemble import IsolationForest, RandomForestClassifier

app = FastAPI(title="SpendGuard Risk Scoring", version="1.0.0")
CATEGORIES = {"electronics": 0, "travel": 1, "grocery": 2, "fuel": 3, "dining": 4, "other": 5}

class TransactionFeatures(BaseModel):
    customerId: str = Field(min_length=1, max_length=100)
    merchant: str = Field(min_length=1, max_length=200)
    merchantCategory: str = "other"
    amount: float = Field(gt=0, le=1_000_000)
    currency: str = Field(default="USD", min_length=3, max_length=3)
    deviceId: str = "unknown"
    deviceConsistent: bool = True
    country: str = "US"
    transactionsLastHour: int = Field(default=0, ge=0, le=10000)
    transactionsLast24Hours: int = Field(default=0, ge=0, le=100000)
    isCardNotPresent: bool = False
    distanceFromUsualKm: float = Field(default=0, ge=0, le=25000)

def vector(t: TransactionFeatures) -> np.ndarray:
    return np.array([[np.log1p(t.amount), t.transactionsLastHour, t.transactionsLast24Hours,
        CATEGORIES.get(t.merchantCategory.lower(), 5), int(t.deviceConsistent),
        int(t.isCardNotPresent), np.log1p(t.distanceFromUsualKm)]], dtype=float)

# Reproducible synthetic examples. These labels are illustrative, not production training data.
rng = np.random.default_rng(42)
X = np.column_stack([rng.normal(4.0, 1.0, 1500), rng.poisson(1, 1500), rng.poisson(4, 1500),
                     rng.integers(0, 6, 1500), rng.integers(0, 2, 1500), rng.integers(0, 2, 1500),
                     rng.exponential(2.0, 1500)])
y = ((X[:, 0] > 7.0) | (X[:, 1] > 5) | (X[:, 4] == 0) | (X[:, 6] > 7.0)).astype(int)
classifier = RandomForestClassifier(n_estimators=80, max_depth=8, random_state=42, class_weight="balanced")
classifier.fit(X, y)
anomaly = IsolationForest(n_estimators=100, contamination=0.04, random_state=42).fit(X)

@app.get("/health")
def health(): return {"status": "ok", "model": "synthetic-demo-v1"}

@app.post("/score")
def score(t: TransactionFeatures):
    x = vector(t)
    supervised = float(classifier.predict_proba(x)[0, 1])
    anomaly_raw = float(-anomaly.score_samples(x)[0])
    anomaly_score = float(np.clip((anomaly_raw - 0.25) / 0.8, 0, 1))
    risk = float(np.clip(0.7 * supervised + 0.3 * anomaly_score, 0, 1))
    level = "high" if risk >= 0.75 else "medium" if risk >= 0.45 else "low"
    reasons = []
    if not t.deviceConsistent: reasons.append("the device differs from your usual device")
    if t.transactionsLastHour >= 4: reasons.append("there have been several transactions in the last hour")
    if t.distanceFromUsualKm >= 500: reasons.append("the location is far from your usual activity")
    if t.isCardNotPresent: reasons.append("the purchase was made without the physical card")
    if t.amount >= 500: reasons.append("the amount is higher than a typical everyday purchase")
    reason = "; ".join(reasons[:2]) or "the transaction pattern differs from the demo model's baseline"
    return {"riskScore": round(risk, 4), "riskLevel": level,
            "alertMessage": f"This ${t.amount:.2f} purchase at {t.merchant} was flagged because {reason}.",
            "modelVersion": "synthetic-demo-v1", "disclaimer": "Demo score from synthetic training data; not a real fraud determination."}
