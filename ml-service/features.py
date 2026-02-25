import math
import re

COMMON_PATTERNS = ["123", "password", "qwerty", "abc", "111", "000", "admin", "login", "welcome"]
KEYBOARD_PATTERNS = ["qwerty", "asdf", "zxcv", "qaz", "wsx", "edc"]

def entropy(password):
    """Calculate Shannon entropy per character"""
    if not password:
        return 0
    prob = [password.count(c) / len(password) for c in set(password)]
    return -sum(x * math.log2(x) for x in prob if x > 0)

def count_sequential(password):
    """Count sequential character runs (abc, 123, etc)"""
    count = 0
    for i in range(len(password) - 2):
        if (ord(password[i+1]) == ord(password[i]) + 1 and 
            ord(password[i+2]) == ord(password[i]) + 2):
            count += 1
    return count

def count_repeating(password):
    """Count repeating character patterns (aaa, 111, etc)"""
    count = 0
    for i in range(len(password) - 2):
        if password[i] == password[i+1] == password[i+2]:
            count += 1
    return count

def has_keyboard_pattern(password):
    """Check for keyboard pattern sequences"""
    lower_pw = password.lower()
    return any(pattern in lower_pw for pattern in KEYBOARD_PATTERNS)

def character_diversity(password):
    """Calculate ratio of unique characters to total length"""
    if not password:
        return 0
    return len(set(password)) / len(password)

def extract(password):
    """
    Extract enhanced features from password.
    Returns a feature vector optimized for weak/medium/strong classification.
    """
    length = len(password)
    upper = sum(c.isupper() for c in password)
    lower = sum(c.islower() for c in password)
    digits = sum(c.isdigit() for c in password)
    symbols = sum(not c.isalnum() for c in password)
    
    # Advanced features
    ent = entropy(password)
    diversity = character_diversity(password)
    sequential = count_sequential(password)
    repeating = count_repeating(password)
    
    # Pattern detection (binary)
    has_common = int(any(x in password.lower() for x in COMMON_PATTERNS))
    has_keyboard = int(has_keyboard_pattern(password))
    
    # Character type ratios (better than absolute counts)
    upper_ratio = upper / length if length > 0 else 0
    lower_ratio = lower / length if length > 0 else 0
    digit_ratio = digits / length if length > 0 else 0
    symbol_ratio = symbols / length if length > 0 else 0
    
    # Character type diversity (how many types are used)
    char_types = sum([upper > 0, lower > 0, digits > 0, symbols > 0])
    
    # Length score (normalized)
    length_score = min(length / 16.0, 1.5)  # Sweet spot around 16 chars
    
    return [
        length_score,           # 0: Length score (0-1.5)
        upper_ratio,            # 1: Uppercase ratio (0-1)
        lower_ratio,            # 2: Lowercase ratio (0-1)
        digit_ratio,            # 3: Digit ratio (0-1)
        symbol_ratio,           # 4: Symbol ratio (0-1)
        ent / 5.0,              # 5: Entropy per char (typically 0-1)
        diversity,              # 6: Character diversity (0-1)
        char_types / 4.0,       # 7: Character type diversity (0-1)
        min(sequential / 5.0, 1.0),  # 8: Sequential patterns (normalized)
        min(repeating / 5.0, 1.0),   # 9: Repeating patterns (normalized)
        has_common,             # 10: Has common pattern (0 or 1)
        has_keyboard,           # 11: Has keyboard pattern (0 or 1)
    ]
