# SpendGuard — Real-Time Fraud Detection & Spending Alerts

A portfolio proof of concept for transaction risk scoring and contextual customer alerts. Java/Spring Boot owns transaction ingestion and persistence, a Python/scikit-learn service scores transaction features, Redis caches feature vectors, and a React client receives alerts over WebSockets.

> **Important:** This is a development/demo system, not a production payment or fraud-control service. The bundled model is trained on synthetic data. No real-world latency, fraud precision, or customer-resolution improvements are claimed. Never connect it to live cardholder data without security, privacy, model validation, and operational reviews.

## Architecture

- `backend/`: Spring Boot REST API, transaction validation, fraud-service client, PostgreSQL persistence, and STOMP WebSocket alerts.
- `ml_service/`: FastAPI scoring API with feature engineering, Isolation Forest anomaly score, optional Random Forest labelled-pattern classifier, and plain-language alert reasons.
- `frontend/`: React + TypeScript dashboard with transaction generator, score/severity views, and live alert feed.
- `transaction-generator/`: Java synthetic transaction generator for card-not-present, velocity, and geographic anomaly scenarios.
- Redis: feature-cache service; PostgreSQL: transaction records.

## Requirements

Docker + Docker Compose (recommended), or Java 21, Python 3.11, Node 20, PostgreSQL 16, and Redis 7.

## Quick start

```bash
cp .env.example .env
docker compose up --build
```

- Frontend: http://localhost:5173
- Spring API: http://localhost:8080/api/health
- ML API docs: http://localhost:8000/docs
- PostgreSQL: localhost:5432 (database `spendguard`)
- Redis: localhost:6379

The ML service trains a reproducible model on synthetic examples at startup if no model artifact exists. This is for demonstrating the pipeline only.

## Run services separately

### Python scoring service

```bash
cd ml_service
python -m venv .venv
# Windows: .venv\Scripts\activate
pip install -r requirements.txt
uvicorn app:app --reload --port 8000
```

### Java API

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## API

- `POST /api/transactions`: validate and score a transaction, persist result, and publish an alert.
- `GET /api/transactions`: list recent transactions.
- `GET /api/health`: health check.
- `POST /score`: ML service risk score (internal service).
- WebSocket/STOMP endpoint `/ws`; subscribe to `/topic/alerts`.

Example request:

```json
{
  "customerId": "cust-1001",
  "merchant": "Northstar Electronics",
  "merchantCategory": "electronics",
  "amount": 799.99,
  "currency": "USD",
  "deviceId": "device-a",
  "country": "US",
  "transactionsLastHour": 1,
  "transactionsLast24Hours": 3,
  "isCardNotPresent": true,
  "distanceFromUsualKm": 12
}
```

## Model and evaluation

The Python service combines an Isolation Forest anomaly score with a Random Forest probability trained on generated labels. Feature inputs include amount, hourly/daily velocity, merchant category, device consistency proxy, card-not-present status, and geographic distance. The response contains a risk score, risk level, and an explainable alert message. Synthetic-data metrics are not evidence of real-world model quality. Evaluate on temporally split, representative, privacy-approved data and tune thresholds with fraud operations before any real use.

## Security checklist before production

- Add authentication/authorization and tenant isolation.
- Use TLS, secret manager, request signing/service authentication, and network policy.
- Do not log PAN, CVV, or sensitive card data; use tokenized identifiers only.
- Add rate limiting, idempotency keys, audit retention controls, and abuse protection.
- Calibrate thresholds against cost-weighted false positives/negatives and monitor drift.
- Add model versioning, reproducible training data, bias review, human escalation, and rollback.

## Tests

```bash
cd ml_service && pip install -r requirements.txt && pytest -q
cd ../backend && mvn test
```

## GitHub

```bash
git init
git add .
git commit -m "Initial commit - SpendGuard fraud detection and spending alerts"
git branch -M main
git remote add origin https://github.com/himanshub334/spendguard.git
git push -u origin main
```

Create the repository on GitHub first. Docker Compose is local development configuration; no cloud deployment is performed by this repository.
# SpendGuard-Real-Time-Fraud-Detection-Spending-Alerts-
