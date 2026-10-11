from fastapi.testclient import TestClient
from app import app
client = TestClient(app)

def sample(**kwargs):
    d = {"customerId":"c1","merchant":"Shop","merchantCategory":"electronics","amount":750,
         "currency":"USD","deviceId":"d2","deviceConsistent":False,"country":"US",
         "transactionsLastHour":5,"transactionsLast24Hours":8,"isCardNotPresent":True,"distanceFromUsualKm":1200}
    d.update(kwargs); return d

def test_health(): assert client.get('/health').json()['status'] == 'ok'
def test_score_shape():
    r=client.post('/score', json=sample()); assert r.status_code == 200
    body=r.json(); assert 0 <= body['riskScore'] <= 1; assert body['riskLevel'] in {'low','medium','high'}
def test_reject_negative_amount(): assert client.post('/score', json=sample(amount=-1)).status_code == 422
