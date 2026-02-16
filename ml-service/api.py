from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from joblib import load
from features import extract
import os

app = FastAPI(title="Password Strength API", version="2.0")

# CORS middleware for frontend access
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Configure this based on your Symfony frontend URL
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load model
MODEL_PATH = "model/password_model.joblib"
if not os.path.exists(MODEL_PATH):
    raise Exception(f"Model not found at {MODEL_PATH}. Please run train.py first.")

model = load(MODEL_PATH)

class PasswordRequest(BaseModel):
    password: str

class PasswordResponse(BaseModel):
    score: float
    label: str
    confidence: float
    suggestions: list
    details: dict

def get_recommendations(password, features):
    """Generate specific recommendations based on password analysis"""
    recommendations = []
    
    # Feature indices from the new features.py:
    # [0] length_score, [1] upper_ratio, [2] lower_ratio, [3] digit_ratio, 
    # [4] symbol_ratio, [5] entropy, [6] diversity, [7] char_types,
    # [8] sequential, [9] repeating, [10] has_common, [11] has_keyboard
    
    length = len(password)
    has_upper = any(c.isupper() for c in password)
    has_lower = any(c.islower() for c in password)
    has_digit = any(c.isdigit() for c in password)
    has_symbol = any(not c.isalnum() for c in password)
    
    # Length recommendations
    if length < 8:
        recommendations.append("⚠️ Password is too short. Use at least 12 characters.")
    elif length < 12:
        recommendations.append("💡 Consider increasing length to 12+ characters for better security.")
    
    # Character type recommendations
    if not has_upper:
        recommendations.append("🔤 Add uppercase letters (A-Z)")
    
    if not has_lower:
        recommendations.append("🔡 Add lowercase letters (a-z)")
    
    if not has_digit:
        recommendations.append("🔢 Add numbers (0-9)")
    
    if not has_symbol:
        recommendations.append("🔣 Add special characters (!@#$%^&*)")
    
    # Pattern recommendations
    if features[10] == 1:  # has_common pattern
        recommendations.append("⛔ Avoid common patterns (password, 123, qwerty, abc)")
    
    if features[11] == 1:  # has_keyboard pattern
        recommendations.append("⛔ Avoid keyboard patterns (qwerty, asdf, zxcv)")
    
    if features[8] > 0.2:  # sequential patterns
        recommendations.append("⚠️ Avoid sequential characters (abc, 123, xyz)")
    
    if features[9] > 0.2:  # repeating patterns
        recommendations.append("⚠️ Avoid repeating characters (aaa, 111, @@@)")
    
    # Diversity recommendations
    if features[6] < 0.6:  # low character diversity
        recommendations.append("💡 Use more unique characters for better entropy")
    
    if not recommendations:
        recommendations.append("✅ Password meets all security requirements!")
    
    return recommendations

@app.get("/")
def read_root():
    return {
        "message": "Password Strength API v2.0",
        "endpoints": {
            "POST /predict": "Analyze password strength",
            "GET /health": "Check API health"
        }
    }

@app.get("/health")
def health_check():
    return {
        "status": "healthy",
        "model_loaded": model is not None,
        "version": "2.0"
    }

@app.post("/predict", response_model=PasswordResponse)
def predict(req: PasswordRequest):
    """
    Analyze password strength and provide detailed feedback.
    
    Returns:
    - score: 0-100 strength score
    - label: "weak", "medium", or "strong"
    - confidence: Model confidence percentage
    - suggestions: List of recommendations
    - details: Additional analysis details
    """
    try:
        password = req.password
        
        # Validate input
        if not password:
            raise HTTPException(status_code=400, detail="Password cannot be empty")
        
        if len(password) > 128:
            raise HTTPException(status_code=400, detail="Password too long (max 128 characters)")
        
        # Extract features
        features = extract(password)
        
        # Get model prediction
        probabilities = model.predict_proba([features])[0]  # [weak_prob, strong_prob]
        
        # Calculate score (0-100)
        strength_score = probabilities[1] * 100  # Use strong probability
        
        # Determine label with improved thresholds
        if strength_score < 40:
            label = "weak"
            label_emoji = "🔴"
        elif strength_score < 70:
            label = "medium"
            label_emoji = "🟡"
        else:
            label = "strong"
            label_emoji = "🟢"
        
        # Get recommendations
        suggestions = get_recommendations(password, features)
        
        # Prepare detailed analysis
        details = {
            "length": len(password),
            "has_uppercase": any(c.isupper() for c in password),
            "has_lowercase": any(c.islower() for c in password),
            "has_digits": any(c.isdigit() for c in password),
            "has_symbols": any(not c.isalnum() for c in password),
            "character_types": int(features[7] * 4),  # Number of character types used
            "entropy_score": round(features[5], 2),
            "diversity_score": round(features[6], 2),
            "has_common_patterns": bool(features[10]),
            "has_keyboard_patterns": bool(features[11]),
        }
        
        return {
            "score": round(strength_score, 2),
            "label": label,
            "confidence": round(max(probabilities) * 100, 2),
            "suggestions": suggestions,
            "details": details
        }
    
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Internal server error: {str(e)}")

@app.post("/batch-predict")
def batch_predict(passwords: list[str]):
    """
    Analyze multiple passwords at once.
    """
    if len(passwords) > 100:
        raise HTTPException(status_code=400, detail="Maximum 100 passwords per batch")
    
    results = []
    for pwd in passwords:
        try:
            req = PasswordRequest(password=pwd)
            result = predict(req)
            results.append(result)
        except Exception as e:
            results.append({"error": str(e), "password": pwd})
    
    return {"results": results}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
