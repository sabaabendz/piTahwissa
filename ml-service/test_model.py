from joblib import load
from features import extract
import os

# Load trained model
model_path = "model/password_model.joblib"

if not os.path.exists(model_path):
    print(f"Error: Model not found at {model_path}")
    print("Please run train.py first to generate the model.")
    exit(1)

model = load(model_path)
print(f"Loaded model from {model_path}\n")

# Test passwords
test_passwords = [
    "password123",                    # Weak
    "Mohsen'123456789123456789",     # Medium-Strong (long with variety)
    "Abcdef12",                       # Weak (short, simple)
    "Qwerty!@#",                      # Weak (keyboard pattern)
    "StrongPass!2026",                # Medium-Strong
    "aB3$xK9#mL2&vN5@qR",            # Strong
    "12345678",                       # Very Weak
    "P@ssw0rd",                       # Weak-Medium
    "MyVerySecurePassword123!",       # Medium-Strong
    "x8K#mQ2$vL9@nR5&",              # Strong
]

def calculate_strength_score(prob_strong):
    """
    Convert model probability to a 0-100 strength score.
    Uses the raw probability with adjusted thresholds.
    """
    # The probability already represents model confidence
    # Scale to 0-100 range
    return prob_strong * 100

def get_strength_label(score):
    """
    Determine strength label based on score.
    Adjusted thresholds for better classification.
    """
    if score < 40:
        return "Weak", "🔴"
    elif score < 70:
        return "Medium", "🟡"
    else:
        return "Strong", "🟢"

def get_recommendations(password, score):
    """Provide specific recommendations for improving password"""
    recommendations = []
    
    length = len(password)
    has_upper = any(c.isupper() for c in password)
    has_lower = any(c.islower() for c in password)
    has_digit = any(c.isdigit() for c in password)
    has_symbol = any(not c.isalnum() for c in password)
    
    if length < 12:
        recommendations.append(f"Increase length to at least 12 characters (current: {length})")
    
    if not has_upper:
        recommendations.append("Add uppercase letters")
    
    if not has_lower:
        recommendations.append("Add lowercase letters")
    
    if not has_digit:
        recommendations.append("Add numbers")
    
    if not has_symbol:
        recommendations.append("Add special characters (!@#$%^&*)")
    
    # Check for common patterns
    common_patterns = ["123", "password", "qwerty", "abc"]
    if any(pattern in password.lower() for pattern in common_patterns):
        recommendations.append("Avoid common patterns (123, password, qwerty, etc.)")
    
    # Check for repeating characters
    if any(password[i] == password[i+1] == password[i+2] for i in range(len(password) - 2)):
        recommendations.append("Avoid repeating characters (aaa, 111, etc.)")
    
    return recommendations

print("="*60)
print("PASSWORD STRENGTH ANALYSIS")
print("="*60)

for pw in test_passwords:
    features = extract(pw)
    prob = model.predict_proba([features])[0]  # [weak_prob, strong_prob]
    
    score = calculate_strength_score(prob[1])
    label, emoji = get_strength_label(score)
    
    print(f"\nPassword: {pw}")
    print(f"  Strength: {emoji} {label} (Score: {score:.1f}/100)")
    print(f"  Model Confidence: Weak={prob[0]*100:.1f}%, Strong={prob[1]*100:.1f}%")
    
    if score < 70:  # Show recommendations for weak and medium passwords
        recommendations = get_recommendations(pw, score)
        if recommendations:
            print(f"  Recommendations:")
            for rec in recommendations:
                print(f"    • {rec}")
    
    # Show detailed features for debugging (optional)
    # print(f"  Features: {features}")
    print("-"*60)

print("\n" + "="*60)
print("FEATURE LEGEND:")
print("="*60)
print("""
Features extracted from each password:
  [0] Length score (normalized to 0-1.5)
  [1] Uppercase ratio (0-1)
  [2] Lowercase ratio (0-1)
  [3] Digit ratio (0-1)
  [4] Symbol ratio (0-1)
  [5] Entropy per character (0-1)
  [6] Character diversity (unique chars / total)
  [7] Character type diversity (0-1)
  [8] Sequential patterns score
  [9] Repeating patterns score
  [10] Has common pattern (0 or 1)
  [11] Has keyboard pattern (0 or 1)
""")

# Interactive mode
print("\n" + "="*60)
print("INTERACTIVE MODE")
print("="*60)
print("Enter passwords to test (or 'quit' to exit):\n")

while True:
    try:
        user_input = input("Enter password: ").strip()
        if user_input.lower() in ['quit', 'exit', 'q']:
            print("Goodbye!")
            break
        
        if not user_input:
            continue
        
        features = extract(user_input)
        prob = model.predict_proba([features])[0]
        score = calculate_strength_score(prob[1])
        label, emoji = get_strength_label(score)
        
        print(f"\n  Strength: {emoji} {label} (Score: {score:.1f}/100)")
        
        if score < 70:
            recommendations = get_recommendations(user_input, score)
            if recommendations:
                print(f"  Recommendations:")
                for rec in recommendations:
                    print(f"    • {rec}")
        print()
        
    except KeyboardInterrupt:
        print("\n\nGoodbye!")
        break
    except Exception as e:
        print(f"Error: {e}")
