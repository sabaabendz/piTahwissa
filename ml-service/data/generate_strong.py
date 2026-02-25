import random
import string

def gen():
    chars = string.ascii_letters + string.digits + "!@#$%^&*"
    return ''.join(random.choice(chars) for _ in range(random.randint(12,20)))

with open("strong.txt","w") as f:
    for _ in range(100000):
        f.write(gen()+"\n")
