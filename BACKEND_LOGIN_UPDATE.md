# Backend Login Endpoint - Updated Response Structure

## ✅ Changes Implemented

### 1. Created UserDTO Class
**File:** `src/main/java/com/e_commerce/backend/dto/UserDTO.java`

A safe DTO that contains only non-sensitive user information:
- `id` - User's unique identifier
- `username` - User's username
- `email` - User's email address
- `role` - User's role (ROLE_USER or ROLE_ADMIN) for admin checks
- `enabled` - Account status
- `accountNonExpired` - Account expiration status
- `accountNonLocked` - Account lock status
- `credentialsNonExpired` - Credentials validity
- `createdAt` - Account creation timestamp

**Note:** Password is NEVER included in this DTO for security.

### 2. Updated AuthResponse Class
**File:** `src/main/java/com/e_commerce/backend/dto/AuthResponse.java`

Now returns:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@sovannarithshop.com",
    "role": "ROLE_ADMIN",
    "enabled": true,
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "createdAt": "2025-10-16T10:30:00"
  }
}
```

### 3. Updated AuthService
**File:** `src/main/java/com/e_commerce/backend/services/AuthService.java`

Both `register()` and `login()` methods now return complete user information:

```java
// Register endpoint
public AuthResponse register(RegisterRequest registerRequest) {
    // ... validation and user creation ...
    User savedUser = userRepository.save(user);
    String jwt = jwtUtil.generateToken(savedUser);
    
    // Return token with complete user data
    return new AuthResponse(jwt, new UserDTO(savedUser));
}

// Login endpoint
public AuthResponse login(LoginRequest loginRequest) {
    // ... authentication ...
    String jwt = jwtUtil.generateToken(userDetails);
    User freshUser = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
    
    // Return token with complete user data
    return new AuthResponse(jwt, new UserDTO(freshUser));
}
```

---

## 📡 API Endpoints

### Login
**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYzMTIzNDU2NH0...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@sovannarithshop.com",
    "role": "ROLE_ADMIN",
    "enabled": true,
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "createdAt": "2025-10-15T08:30:00"
  },
  "message": null
}
```

**Response (Error):**
```json
{
  "token": null,
  "type": "Bearer",
  "user": null,
  "message": "Invalid username or password"
}
```

### Register
**Endpoint:** `POST /api/auth/register`

**Request:**
```json
{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "securepass123"
}
```

**Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": 3,
    "username": "newuser",
    "email": "newuser@example.com",
    "role": "ROLE_USER",
    "enabled": true,
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "createdAt": "2025-10-16T14:22:00"
  },
  "message": null
}
```

---

## 🔒 Security Features

### What's Included in UserDTO:
✅ User ID (for tracking)  
✅ Username  
✅ Email  
✅ Role (for admin checks)  
✅ Account status flags  
✅ Creation timestamp  

### What's NOT Included (Security):
❌ Password (hashed or plain)  
❌ Failed login attempts  
❌ Lock timestamps  
❌ Last failed login time  

---

## 💻 Frontend Usage Example

```javascript
// Login request
const response = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'admin',
    password: 'password123'
  })
});

const data = await response.json();

if (data.token) {
  // Store token
  localStorage.setItem('token', data.token);
  
  // Store user info
  localStorage.setItem('user', JSON.stringify(data.user));
  
  // Check if admin
  const isAdmin = data.user.role === 'ROLE_ADMIN';
  
  // Access user details
  console.log('Welcome,', data.user.username);
  console.log('User ID:', data.user.id);
  console.log('Email:', data.user.email);
}
```

---

## ✨ Benefits

1. **Complete User Context** - Frontend has all necessary user information without additional API calls
2. **Admin Role Check** - Can immediately determine if user is admin
3. **Single Source of Truth** - User data comes directly from authentication
4. **Security** - Only safe, non-sensitive data is exposed
5. **Consistent Structure** - Both login and register return the same format

---

## 🧪 Testing

### Test Login with Admin User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### Test Login with Regular User
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### Expected Response Structure
You should receive a JSON object with:
- `token` (JWT string)
- `type` ("Bearer")
- `user` (UserDTO object with all safe user fields)

---

## 📋 Summary of Changes

| File | Change | Purpose |
|------|--------|---------|
| `UserDTO.java` | Created new DTO | Safe user data transfer object |
| `AuthResponse.java` | Updated to include `UserDTO` | Return complete user info |
| `AuthService.java` | Modified `login()` and `register()` | Return `UserDTO` in response |
| `AuthController.java` | No changes needed | Already uses `AuthService` |

✅ All changes are backward compatible  
✅ No breaking changes to existing endpoints  
✅ Security best practices maintained

