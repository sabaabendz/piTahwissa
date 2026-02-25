from __future__ import annotations

from typing import List


def is_admin_role(role: str | None) -> bool:
    if not role:
        return False
    normalized = role.strip().upper()
    return normalized in {"ROLE_ADMIN", "ADMIN"}


def validate_embedding_values(values: List[float]) -> bool:
    return isinstance(values, list) and len(values) > 0
