# Password Strength ML Service

Machine Learning service for password strength detection.

## Setup
```bash
# Create virtual environment
python3 -m venv ml-env

# Activate virtual environment
source ml-env/bin/activate

# Install dependencies
pip install -r requirements.txt

# Train model (first time only)
python train.py

# Start service
python -m uvicorn api:app --host 127.0.0.1 --port 8001 --reload
```

## Quick Start
```bash
./start_service.sh
```

## API Endpoints

- `GET /health` - Health check
- `POST /predict` - Analyze password strength

## Testing
```bash
curl -X POST http://127.0.0.1:8001/predict \
  -H "Content-Type: application/json" \
  -d '{"password":"test123"}'
```
