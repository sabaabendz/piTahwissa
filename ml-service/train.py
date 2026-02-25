import random
import string
from features import extract, COMMON_PATTERNS
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, confusion_matrix
from joblib import dump
import os

# -------------------------------
# Step 1: Load weak passwords
# -------------------------------
weak_file = "data/weak.txt"
MAX_WEAK = 5000  # limit for memory

try:
    with open(weak_file, "r", encoding="utf-8", errors="ignore") as f:
        weak_passwords_all = [line.strip() for line in f if line.strip()]
except FileNotFoundError:
    print(f"Warning: {weak_file} not found. Creating sample weak passwords.")
    # Sample weak passwords for testing
    weak_passwords_all = [
        "password", "123456", "12345678", "qwerty", "abc123",
        "password123", "admin", "letmein", "welcome", "monkey",
        "1234567890", "password1", "qwertyuiop", "123123", "000000"
    ] * 100

random.shuffle(weak_passwords_all)
weak_passwords = weak_passwords_all[:MAX_WEAK]
print(f"Weak passwords loaded: {len(weak_passwords)}")

# -------------------------------
# Step 2: Generate strong passwords
# -------------------------------
def generate_strong_password(length=None):
    """Generate truly strong passwords with high entropy"""
    if length is None:
        length = random.randint(12, 20)  # Adjusted range
    
    max_attempts = 100
    for _ in range(max_attempts):
        # Ensure at least 2 of each type for stronger passwords
        password = [
            random.choice(string.ascii_uppercase),
            random.choice(string.ascii_uppercase),
            random.choice(string.ascii_lowercase),
            random.choice(string.ascii_lowercase),
            random.choice(string.digits),
            random.choice(string.digits),
            random.choice(string.punctuation),
            random.choice(string.punctuation)
        ]
        
        # Fill remaining with random chars
        chars = string.ascii_letters + string.digits + string.punctuation
        remaining = length - len(password)
        password += [random.choice(chars) for _ in range(remaining)]
        
        # Shuffle to avoid predictable patterns
        random.shuffle(password)
        password_str = ''.join(password)
        
        # Ensure no weak patterns
        password_lower = password_str.lower()
        has_weak_pattern = any(pat in password_lower for pat in COMMON_PATTERNS)
        
        # Check for sequential or repeating patterns
        has_sequential = any(
            password_str[i:i+3] in '0123456789abcdefghijklmnopqrstuvwxyz'
            for i in range(len(password_str) - 2)
        )
        has_repeating = any(
            password_str[i] == password_str[i+1] == password_str[i+2]
            for i in range(len(password_str) - 2)
        )
        
        if not (has_weak_pattern or has_sequential or has_repeating):
            return password_str
    
    # Fallback: return even if not perfect
    return password_str

def generate_medium_password(length=None):
    """Generate medium-strength passwords (missing some characteristics)"""
    if length is None:
        length = random.randint(8, 14)
    
    password_type = random.choice(['no_symbol', 'short_good', 'long_simple'])
    
    if password_type == 'no_symbol':
        # Good length but no symbols
        password = [
            random.choice(string.ascii_uppercase),
            random.choice(string.ascii_lowercase),
            random.choice(string.digits)
        ]
        chars = string.ascii_letters + string.digits
        password += [random.choice(chars) for _ in range(length - 3)]
        
    elif password_type == 'short_good':
        # Short but has all types
        length = random.randint(8, 10)
        password = [
            random.choice(string.ascii_uppercase),
            random.choice(string.ascii_lowercase),
            random.choice(string.digits),
            random.choice(string.punctuation)
        ]
        chars = string.ascii_letters + string.digits + string.punctuation
        password += [random.choice(chars) for _ in range(length - 4)]
        
    else:  # long_simple
        # Longer but only letters and digits
        length = random.randint(10, 14)
        password = [
            random.choice(string.ascii_uppercase),
            random.choice(string.ascii_lowercase),
            random.choice(string.digits)
        ]
        chars = string.ascii_letters + string.digits
        password += [random.choice(chars) for _ in range(length - 3)]
    
    random.shuffle(password)
    return ''.join(password)

# Generate passwords in 3 categories
num_per_category = len(weak_passwords)
strong_passwords = [generate_strong_password() for _ in range(num_per_category)]
medium_passwords = [generate_medium_password() for _ in range(num_per_category // 2)]

print(f"Strong passwords generated: {len(strong_passwords)}")
print(f"Medium passwords generated: {len(medium_passwords)}")

# -------------------------------
# Step 3: Combine dataset
# -------------------------------
# For binary classification: weak=0, strong=1, medium passwords split between both
all_passwords = weak_passwords + medium_passwords + strong_passwords
labels = (
    [0] * len(weak_passwords) +           # weak
    [0] * (len(medium_passwords) // 2) +  # half medium -> weak
    [1] * (len(medium_passwords) - len(medium_passwords) // 2) +  # half medium -> strong
    [1] * len(strong_passwords)           # strong
)

print(f"Total passwords: {len(all_passwords)}")
print(f"Label distribution - Weak (0): {labels.count(0)}, Strong (1): {labels.count(1)}")

# -------------------------------
# Step 4: Extract features
# -------------------------------
print("\nExtracting features...")
X = [extract(pw) for pw in all_passwords]
y = labels

# Print sample features for debugging
print("\nSample features:")
print("Weak password:", weak_passwords[0], "->", X[0])
print("Strong password:", strong_passwords[0], "->", X[len(weak_passwords) + len(medium_passwords)])

# -------------------------------
# Step 5: Train model
# -------------------------------
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42, stratify=y
)

model = make_pipeline(
    StandardScaler(),
    LogisticRegression(
        class_weight='balanced',
        max_iter=2000,
        random_state=42,
        C=1.0  # Regularization parameter
    )
)

print("\nTraining Logistic Regression model...")
model.fit(X_train, y_train)

accuracy = model.score(X_test, y_test)
print(f"Model trained. Test accuracy: {accuracy*100:.2f}%")

# Detailed evaluation
y_pred = model.predict(X_test)
print("\nClassification Report:")
print(classification_report(y_test, y_pred, target_names=['Weak', 'Strong']))

print("\nConfusion Matrix:")
print(confusion_matrix(y_test, y_pred))

# -------------------------------
# Step 6: Save model
# -------------------------------
os.makedirs("model", exist_ok=True)
dump(model, "model/password_model.joblib")
print("\nModel saved to model/password_model.joblib")

# Test with sample passwords
print("\n" + "="*50)
print("Quick test with sample passwords:")
print("="*50)

test_samples = [
    ("password123", "Expected: Weak"),
    ("P@ssw0rd", "Expected: Weak/Medium"),
    ("MyS3cur3P@ss!", "Expected: Medium/Strong"),
    ("aB3$xK9#mL2&vN5@qR", "Expected: Strong")
]

for pw, expected in test_samples:
    features = extract(pw)
    prob = model.predict_proba([features])[0]
    prediction = "Strong" if prob[1] > 0.5 else "Weak"
    print(f"\n{pw}")
    print(f"  {expected}")
    print(f"  Prediction: {prediction} (confidence: {max(prob)*100:.1f}%)")
