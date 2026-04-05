#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

DEFAULT_VENV="/home/mohsen-nabli/smarttask-manager/ml-env"
LOCAL_VENV="$SCRIPT_DIR/ml-env"

if [ -n "${VENV_DIR:-}" ]; then
    RESOLVED_VENV="$VENV_DIR"
elif [ -f "$DEFAULT_VENV/bin/activate" ]; then
    RESOLVED_VENV="$DEFAULT_VENV"
elif [ -f "$LOCAL_VENV/bin/activate" ]; then
    RESOLVED_VENV="$LOCAL_VENV"
else
    echo "[ERROR] Virtual environment not found. Checked:" >&2
    echo "  - $DEFAULT_VENV" >&2
    echo "  - $LOCAL_VENV" >&2
    echo "Set VENV_DIR to the correct path and retry." >&2
    exit 1
fi

echo "[INFO] Activating virtual environment at $RESOLVED_VENV"
source "$RESOLVED_VENV/bin/activate"

VENV_PYTHON="$RESOLVED_VENV/bin/python"

if [ ! -x "$VENV_PYTHON" ]; then
    echo "[ERROR] Python executable not found in $RESOLVED_VENV" >&2
    exit 1
fi

echo "[INFO] Using Python: $VENV_PYTHON"
"$VENV_PYTHON" -m pip --version

REQUIRED_PACKAGES=(
    setuptools
    fastapi
    uvicorn
    face_recognition
    opencv-python
    Pillow
)

missing_packages=()
for package in "${REQUIRED_PACKAGES[@]}"; do
    if ! "$VENV_PYTHON" -m pip show "$package" > /dev/null 2>&1; then
        missing_packages+=("$package")
    fi
done

if [ ${#missing_packages[@]} -gt 0 ]; then
    echo "[INFO] Installing missing dependencies: ${missing_packages[*]}"
    "$VENV_PYTHON" -m pip install "${missing_packages[@]}"
fi

echo "[INFO] Ensuring requirements.txt dependencies are installed"
"$VENV_PYTHON" -m pip install -r requirements.txt

if ! "$VENV_PYTHON" - <<'PY'
import importlib.util
import sys
sys.exit(0 if importlib.util.find_spec("face_recognition_models") else 1)
PY
then
    echo "[INFO] Installing face_recognition_models from GitHub"
    "$VENV_PYTHON" -m pip install "git+https://github.com/ageitgey/face_recognition_models"
fi

# Check if model exists
if [ ! -f "model/password_model.joblib" ]; then
    echo "[INFO] Model not found. Training..."
    "$VENV_PYTHON" train.py
fi

# Start the service
HOST="${HOST:-127.0.0.1}"
PORT="${PORT:-8001}"

echo "[INFO] Starting Password Strength + Face ID API on http://$HOST:$PORT"
exec "$VENV_PYTHON" -m uvicorn api:app --host "$HOST" --port "$PORT" --reload