# Testing Login & Register Guide

## ⚡ Quick Reference

| Action | URL | Method | Auth Required |
|--------|-----|--------|---------------|
| **Login Page** | `/login` | GET | ❌ No |
| **Login Submit** | `/login` | POST | ❌ No (auto-handled) |
| **Register Page** | `/register` | GET | ❌ No |
| **Create Manager** | `/manager/new` | GET/POST | ❌ No |
| **Create Collaborator** | `/collaborator/new` | GET/POST | ❌ No |
| **Dashboard** | `/dashboard` | GET | ✅ Yes |
| **API Login** | `/api/login` | POST | ❌ No |

---

## 🚀 Quick Start

### 1. Start the Symfony Server

```bash
cd /home/mohsen-nabli/smarttask-manager
symfony server:start
# or
php -S localhost:8000 -t public
```

The application will be available at: **http://localhost:8000**

### 2. Quick Test Flow

1. **Create Manager** → `http://localhost:8000/manager/new`
2. **Note Enterprise Code** from success message
3. **Create Collaborator** → `http://localhost:8000/collaborator/new` (use enterprise code)
4. **Login** → `http://localhost:8000/login`
5. **Access Dashboard** → `http://localhost:8000/dashboard`

---

## 📝 Step-by-Step Testing

### Step 1: Create Your First Manager (Registration)

Since the `/register` page is currently just a display page, you need to create users via the manager/collaborator creation pages.

**Option A: Via Web Interface**

1. Visit: **http://localhost:8000/manager/new**
   - This page is **PUBLIC** (no login required)

2. Fill in the form:
   - **Name**: `John Doe`
   - **Email**: `manager@example.com`
   - **Password**: `password123` (min 8 characters)
   - **Level**: `Senior`
   - **Department**: `IT`

3. Click **"Save"**
   - ✅ You'll be redirected to `/manager` (manager list)
   - ✅ A success message will appear
   - ✅ **Note the Enterprise Code** (e.g., `ABC1234`) - you'll need this for collaborators!

**Option B: Via API (Postman)**

```
POST http://localhost:8000/api/manager/new
Content-Type: application/json

{
  "name": "John Doe",
  "email": "manager@example.com",
  "password": "password123",
  "level": "Senior",
  "department": "IT"
}
```

Response will include the `enterprise_code`.

---

### Step 2: Create a Collaborator

**Option A: Via Web Interface**

1. Visit: **http://localhost:8000/collaborator/new**
   - This page is **PUBLIC** (no login required)

2. Fill in the form:
   - **Name**: `Jane Smith`
   - **Email**: `collaborator@example.com`
   - **Password**: `password123`
   - **Post**: `Developer`
   - **Team**: `Backend`
   - **Enterprise Code**: `ABC1234` (use the code from Step 1)

3. Click **"Save"**
   - ✅ You'll be redirected to `/collaborator` (collaborator list)

**Option B: Via API (Postman)**

```
POST http://localhost:8000/api/collaborator/new
Content-Type: application/json

{
  "name": "Jane Smith",
  "email": "collaborator@example.com",
  "password": "password123",
  "post": "Developer",
  "team": "Backend",
  "enterpriseCode": "ABC1234"
}
```

---

## 🔐 Testing Login

### Step 3: Visit Login Page

Open your browser and go to:
```
http://localhost:8000/login
```

**What you should see:**
- ✅ Beautiful split-screen design
- ✅ Left side: Welcome message with avatars
- ✅ Right side: Login form with:
  - Email input field (with envelope icon)
  - Password input field (with lock icon)
  - "Remember me" checkbox
  - "Forgot password?" link
  - "Login" button (blue, primary color)
  - "Don't have an account? Signup here" link at bottom

### Step 4: Login with Manager Credentials

**Enter:**
- **Email:** `manager@example.com`  
- **Password:** `password123`
- (Optional) Check "Remember me"

**Click "Login" button.**

**Expected Result:**
- ✅ Form submits (you'll see a brief loading state)
- ✅ Redirected to `/dashboard`
- ✅ Dashboard loads with:
  - Statistics cards at the top
  - Sidebar navigation on the left
  - Top bar with search and profile dropdown
  - No error messages

### Step 5: Test Logout

1. Click on your **profile avatar** in the top-right corner
2. Click **"Sign Out"** from the dropdown menu

**Expected Result:**
- ✅ Redirected back to `/login` page
- ✅ You are logged out
- ✅ If you try to access `/dashboard` directly, you'll be redirected to login

### Step 6: Login with Collaborator Credentials

**Enter:**
- **Email:** `collaborator@example.com`  
- **Password:** `password123`

**Email:** `collaborator@example.com`  
**Password:** `password123`

**Expected Result:**
- ✅ Redirected to `/dashboard`
- ✅ You can access collaborator pages
- ⚠️ You may not access manager pages (if access control is strict)

---

## 🧪 Testing Scenarios

### Scenario 1: Invalid Credentials

1. Go to `/login`
2. Enter wrong email: `wrong@example.com`
3. Enter any password
4. Click "Login"

**Expected:** Error message displayed: "Invalid credentials."

### Scenario 2: Empty Fields

1. Go to `/login`
2. Leave fields empty
3. Click "Login"

**Expected:** Browser validation prevents submission (required fields)

### Scenario 3: Remember Me

1. Go to `/login`
2. Enter credentials
3. Check "Remember me" checkbox
4. Click "Login"

**Expected:** Session persists after browser close

### Scenario 4: Forgot Password Link

1. Go to `/login`
2. Click "Forgot password?" link

**Expected:** Redirected to `/forgot-password` page

---

## 🔍 Testing via Browser Developer Tools

### Check Network Requests

1. Open browser DevTools (F12)
2. Go to Network tab
3. Submit login form
4. Look for POST request to `/login`
5. Check:
   - Request payload includes `_username`, `_password`, `_csrf_token`
   - Response is a redirect (302) to `/dashboard`
   - Cookies are set (session)

### Check Console for Errors

1. Open Console tab
2. Look for any JavaScript errors
3. Check if Bootstrap/theme scripts load correctly

---

## 📱 Testing via Postman (API Login)

### API Login Endpoint

**URL:** `POST http://localhost:8000/api/login`

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
  "token": "eyJ0eXAiOiJKV1QiLCJhbGc...",
  "role": "ROLE_MANAGER",
  "enterprise_code": "ABC1234",
  "user": {
    "id": 1,
    "email": "manager@example.com",
    "name": "John Doe"
  }
}
```

---

## 🎯 Complete Testing Flow

### Full User Journey Test

1. **Create a Manager:**
   ```
   GET http://localhost:8000/manager/new
   ```
   - Fill form and submit
   - Note the **Enterprise Code** from success message or list page

2. **Create a Collaborator:**
   ```
   GET http://localhost:8000/collaborator/new
   ```
   - Use the enterprise code from step 1
   - Fill form and submit

3. **Test Manager Login:**
   ```
   GET http://localhost:8000/login
   ```
   - Login with manager credentials
   - Should redirect to dashboard
   - Check sidebar navigation works

4. **Test Collaborator Login:**
   - Logout first
   - Login with collaborator credentials
   - Should redirect to dashboard
   - Check access to collaborator pages

5. **Test Protected Routes:**
   - Try accessing `/manager` as collaborator (should be denied or redirect)
   - Try accessing `/dashboard` without login (should redirect to login)

---

## 🐛 Troubleshooting

### Issue: "Invalid credentials" even with correct password

**Solution:**
- Check if user exists in database
- Verify password was hashed correctly during registration
- Check database directly: `SELECT * FROM user WHERE email = 'manager@example.com';`

### Issue: Redirect loop on login

**Solution:**
- Check security.yaml access_control rules
- Verify `default_target_path` in form_login config
- Clear cache: `php bin/console cache:clear`

### Issue: "Access Denied" after login

**Solution:**
- Check user roles in database
- Verify access_control rules in security.yaml
- Check if user has ROLE_MANAGER or ROLE_COLLABORATOR

### Issue: Form not submitting

**Solution:**
- Check browser console for JavaScript errors
- Verify CSRF token is present
- Check form action attribute

### Issue: Assets not loading

**Solution:**
- Verify assets are in `public/assets/` directory
- Check file permissions
- Clear cache: `php bin/console cache:clear`

---

## ✅ Success Indicators

### Login Success:
- ✅ Redirect to `/dashboard`
- ✅ Sidebar visible with navigation
- ✅ Top bar shows user profile
- ✅ Flash message (if any)
- ✅ Can access protected pages

### Registration Success:
- ✅ Redirect to list page
- ✅ Success flash message
- ✅ User appears in list
- ✅ Can login with new credentials

---

## 📋 Quick Test Checklist

- [ ] Server starts without errors
- [ ] `/login` page loads correctly
- [ ] `/register` page loads correctly
- [ ] Can create manager via `/manager/new`
- [ ] Can create collaborator via `/collaborator/new`
- [ ] Can login with manager credentials
- [ ] Can login with collaborator credentials
- [ ] Dashboard loads after login
- [ ] Logout works correctly
- [ ] Protected routes require authentication
- [ ] Flash messages display correctly
- [ ] Forms validate correctly
- [ ] Error messages show for invalid credentials

---

## 🎨 Visual Testing

### What to Look For:

1. **Login Page:**
   - Beautiful split-screen design
   - Left: Welcome message with avatars
   - Right: Clean login form
   - Icons in input fields
   - "Remember me" checkbox
   - "Forgot password" link
   - Social login buttons (non-functional yet)

2. **Dashboard:**
   - Statistics cards with icons
   - Quick action buttons
   - Sidebar navigation
   - Top bar with search and profile

3. **Forms:**
   - Bootstrap-styled inputs
   - Proper labels
   - Validation messages
   - Submit buttons with icons

---

## 🔗 Direct URLs for Testing

```
Login:           http://localhost:8000/login
Register:        http://localhost:8000/register
Forgot Password:  http://localhost:8000/forgot-password
Dashboard:       http://localhost:8000/dashboard (requires login)
Manager List:    http://localhost:8000/manager (requires ROLE_MANAGER)
Create Manager:  http://localhost:8000/manager/new (requires ROLE_MANAGER)
Collaborator List: http://localhost:8000/collaborator (requires login)
Create Collaborator: http://localhost:8000/collaborator/new (requires login)
```

---

## 💡 Pro Tips

1. **Use Browser DevTools** to inspect network requests and see what's happening
2. **Check Symfony Profiler** (if enabled) at `/_profiler` to debug requests
3. **Use `php bin/console debug:router`** to see all available routes
4. **Check logs** in `var/log/dev.log` for any errors
5. **Test with different browsers** to ensure compatibility

---

## 🚨 Important Notes

1. **Registration Flow:** Currently, the `/register` page is just a display page. To actually create users:
   - Managers: Use `/manager/new` (requires ROLE_MANAGER or make it public)
   - Collaborators: Use `/collaborator/new` (requires login or make it public)

2. **First User:** You may need to create the first manager via API or directly in the database, then use that to access the web interface.

3. **Enterprise Code:** Collaborators need a valid enterprise code from an existing manager.

4. **Password:** Minimum 8 characters (enforced by form validation).

---

## 🎬 Ready to Test!

Start your server and begin testing:

```bash
symfony server:start
```

Then open: **http://localhost:8000/login**

Happy testing! 🎉
