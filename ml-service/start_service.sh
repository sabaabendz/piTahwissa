#!/bin/bash

# Get the directory where this script is located
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

# Activate virtual environment
source ml-env/bin/activate

# Check if dependencies are installed
if ! pip show fastapi > /dev/null 2>&1; then
    echo "Installing dependencies..."
    pip install -r requirements.txt
fi

# Check if model exists
if [ ! -f "model/password_model.joblib" ]; then
    echo "Model not found. Training..."
    python train.py
fi

# Start the service
echo "Starting Password Strength API on http://127.0.0.1:8001"
python -m uvicorn api:app --host 127.0.0.1 --port 8001 --reload
