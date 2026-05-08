#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import sys

try:
    import face_recognition  # noqa: F401
    import cv2  # noqa: F401
    import numpy  # noqa: F401
except Exception as exc:
    print(f"FAIL: {exc}")
    sys.exit(1)

print("OK: face_recognition, cv2, numpy")
sys.exit(0)

