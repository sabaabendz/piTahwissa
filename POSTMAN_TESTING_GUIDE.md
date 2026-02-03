# Postman Testing Guide

This guide explains how to test all API endpoints using Postman.

## Base URL
```
http://localhost:8000
```
(Adjust if your Symfony server runs on a different port)

---

## 1. Register a Manager

**Endpoint:** `POST /api/manager/new`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "name": "John Doe",
  "email": "manager@example.com",
  "password": "password123",
  "level": "Senior",
  "department": "IT"
}
```

**Expected Response (201 Created):**
```json
{
  "message": "Manager created successfully",
  "manager": {
    "id": 1,
    "name": "John Doe",
    "email": "manager@example.com",
    "level": "Senior",
    "department": "IT",
    "enterprise_code": "ABC1234"
  }
}
```

**Important:** Save the `enterprise_code` from the response - you'll need it to register collaborators!

---

## 2. Register a Collaborator

**Endpoint:** `POST /api/collaborator/new`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "name": "Jane Smith",
  "email": "collaborator@example.com",
  "password": "password123",
  "post": "Developer",
  "team": "Backend",
  "enterpriseCode": "ABC1234"
}
```

**Note:** Replace `"ABC1234"` with the actual enterprise code from step 1.

**Expected Response (201 Created):**
```json
{
  "message": "Collaborator created successfully",
  "collaborator": {
    "id": 2,
    "name": "Jane Smith",
    "email": "collaborator@example.com",
    "post": "Developer",
    "team": "Backend",
    "enterprise_code": "ABC1234"
  }
}
```

**Error Response (400 Bad Request) - Invalid Enterprise Code:**
```json
{
  "error": "Invalid enterprise code"
}
```

---

## 3. Login (Get JWT Token)

**Endpoint:** `POST /api/login`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "email": "manager@example.com",
  "password": "password123"
}
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwMDAvYXBpIiwiaWF0IjoxNzM1MjM0NTY3LCJleHAiOjE3MzUyMzgxNjcsInVzZXJuYW1lIjoibWFuYWdlckBleGFtcGxlLmNvbSJ9...",
  "role": "ROLE_MANAGER",
  "enterprise_code": "ABC1234",
  "user": {
    "id": 1,
    "email": "manager@example.com",
    "name": "John Doe"
  }
}
```

**Important:** Copy the `token` value - you'll need it for authenticated requests!

**Error Response (401 Unauthorized):**
```json
{
  "error": "Invalid credentials"
}
```

---

## 4. Access Protected Endpoints

All protected endpoints require the JWT token in the Authorization header.

### 4.1. Get All Managers (Manager Only)

**Endpoint:** `GET /api/manager`

**Headers:**
```
Authorization: Bearer {YOUR_JWT_TOKEN}
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "manager@example.com",
    "level": "Senior",
    "department": "IT",
    "enterprise_code": "ABC1234"
  }
]
```

**Error Response (403 Forbidden) - If accessed by Collaborator:**
```json
{
  "code": 403,
  "message": "Access Denied."
}
```

### 4.2. Get Single Manager

**Endpoint:** `GET /api/manager/{idUser}`

**Example:** `GET /api/manager/1`

**Headers:**
```
Authorization: Bearer {YOUR_JWT_TOKEN}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "manager@example.com",
  "level": "Senior",
  "department": "IT",
  "enterprise_code": "ABC1234"
}
```

### 4.3. Get All Collaborators (Collaborator Only)

**Endpoint:** `GET /api/collaborator`

**Headers:**
```
Authorization: Bearer {YOUR_JWT_TOKEN}
```

**Note:** Use a token from a collaborator login.

**Expected Response (200 OK):**
```json
[
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "collaborator@example.com",
    "post": "Developer",
    "team": "Backend",
    "enterprise_code": "ABC1234"
  }
]
```

### 4.4. Get Single Collaborator

**Endpoint:** `GET /api/collaborator/{idUser}`

**Example:** `GET /api/collaborator/2`

**Headers:**
```
Authorization: Bearer {YOUR_JWT_TOKEN}
```

**Expected Response (200 OK):**
```json
{
  "id": 2,
  "name": "Jane Smith",
  "email": "collaborator@example.com",
  "post": "Developer",
  "team": "Backend",
  "enterprise_code": "ABC1234"
}
```

---

## Postman Collection Setup Tips

### 1. Create Environment Variables

Create a Postman environment with these variables:
- `base_url`: `http://localhost:8000`
- `jwt_token`: (will be set after login)
- `enterprise_code`: (will be set after manager registration)

### 2. Automate Token Extraction

For the login request, add this to the **Tests** tab:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("jwt_token", jsonData.token);
    pm.environment.set("enterprise_code", jsonData.enterprise_code);
}
```

### 3. Use Variables in Requests

- Use `{{base_url}}/api/login` instead of hardcoding the URL
- Use `{{jwt_token}}` in Authorization header: `Bearer {{jwt_token}}`
- Use `{{enterprise_code}}` in collaborator registration body

---

## Complete Testing Flow

1. **Start Symfony Server:**
   ```bash
   php bin/console server:start
   # or
   symfony server:start
   ```

2. **Register Manager:**
   - POST `/api/manager/new`
   - Save `enterprise_code` from response

3. **Register Collaborator:**
   - POST `/api/collaborator/new`
   - Use the `enterprise_code` from step 2

4. **Login as Manager:**
   - POST `/api/login` with manager credentials
   - Save `token` from response

5. **Login as Collaborator:**
   - POST `/api/login` with collaborator credentials
   - Save `token` from response

6. **Test Protected Endpoints:**
   - GET `/api/manager` (with manager token)
   - GET `/api/collaborator` (with collaborator token)
   - Try accessing manager endpoints with collaborator token (should fail)

---

## Common Error Responses

### 400 Bad Request
```json
{
  "error": "Enterprise code is required"
}
```
or
```json
{
  "errors": {
    "email": "This value is not a valid email address."
  }
}
```

### 401 Unauthorized
```json
{
  "error": "Invalid credentials"
}
```

### 403 Forbidden
```json
{
  "code": 403,
  "message": "Access Denied."
}
```

### 409 Conflict
```json
{
  "error": "Email already exists"
}
```

---

## Quick Test Script

You can also test using `curl`:

```bash
# Register Manager
curl -X POST http://localhost:8000/api/manager/new \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"manager@example.com","password":"password123","level":"Senior","department":"IT"}'

# Login
curl -X POST http://localhost:8000/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"manager@example.com","password":"password123"}'

# Access Protected Endpoint (replace TOKEN with actual token)
curl -X GET http://localhost:8000/api/manager \
  -H "Authorization: Bearer TOKEN"
```
