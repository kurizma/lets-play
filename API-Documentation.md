# LetsPlay User & Product Management API

### Base URL: `https://localhost:8443`

***

## AUTHENTICATION

### 1. Register User
- **Endpoint**: `POST /api/auth/register`
- **Description**: Register a new user (role defaults to USER; admin promotion via a separate endpoint).
- **Request Body**:
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```
- **Example**:
```json
{
  "name": "John Doe",
  "email": "johndoe@example.com",
  "password": "john123"
}
```

### 2. Login User
- **Endpoint**: `POST /api/auth/login`
- **Description**: Log in with email and password. Returns JWT auth token.
- **Request Body**:
```json
{
  "email": "string",
  "password": "string"
}
```
- **Example**:
```json
{
  "email": "johndoe@example.com",
  "password": "john123"
}
```
- **Add the auth token in Postman > Authorization > Bearer Token:**  
  Paste the token from login response to perform authorized API calls.

***

## USERS

### 3. Get All Users
- **Endpoint**: `GET /api/users`
- **Description**: List all users. Requires ADMIN role.

### 4. Get User by ID
- **Endpoint**: `GET /api/users/{id}`
- **Description**: Get a user by user ID.

### 5. Get Current User
- **Endpoint**: `GET /api/users/me`
- **Description**: Gets the currently authenticated user's profile.

### 6. Create User (Admin action)
- **Endpoint**: `POST /api/users/create`
- **Description**: Create a user as ADMIN (can set role).
- **Request Body**:
```json
{
  "name": "string",
  "email": "string",
  "password": "string",
  "role": "USER/ADMIN"
}
```
- **Example**:
```json
{
  "name": "Jane Doe",
  "email": "janedoe@example.com",
  "password": "pass456",
  "role": "USER"
}
```

### 7. Update User
- **Endpoint**: `PUT /api/users/update/{id}`
- **Description**: Update user fields. Admins can update any user; non-admins can only update themselves.
- **Request Body**:
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```
- **Example**:
```json
{
  "name": "John Smith",
  "email": "jsmith@example.com",
  "password": "newpass"
}
```

### 8. Promote User to ADMIN
- **Endpoint**: `PUT /api/users/promote/{id}`
- **Description**: Promote a user to ADMIN. Requires ADMIN role.

- **Example**:
```json
{
  "role": "USER" or "ADMIN"
}
```
### 9. Delete User
- **Endpoint**: `DELETE /api/users/{id}`
- **Description**: Delete a user. Requires ADMIN role.

***

## PRODUCTS

### 10. List All Products
- **Endpoint**: `GET /api/products`
- **Description**: List all products. Accessible without authentication.

### 11. Get Product by ID
- **Endpoint**: `GET /api/products/{id}`
- **Description**: Get details of a product by product ID.

### 12. List All Products by User
- **Endpoint**: `GET /api/products/user/{userId}`
- **Description**: Get all products created by a specific user.

### 13. Create Product
- **Endpoint**: `POST /api/products`
- **Description**: Create a new product. Authenticated users only.  
  *Include JWT token as Authorization header:*
- **Request Body**:
```json
{
  "name": "string",
  "description": "string",
  "price": 0.0
}
```
- **Example**:
```json
{
  "name": "iPhone 14",
  "description": "Brand new iPhone 14",
  "price": 999.99
}
```

### 14. Update Product
- **Endpoint**: `PUT /api/products/{id}`
- **Description**: Update a product. Authenticated user (owner/admin) only.
- **Request Body**:
```json
{
  "name": "string",
  "description": "string",
  "price": 0.0
}
```

### 15. Delete Product
- **Endpoint**: `DELETE /api/products/{id}`
- **Description**: Delete a product by ID. Authenticated user (owner/admin) only.

***

## Notes

- **All secured endpoints require JWT token in the `Authorization` header (Bearer token).**
- **Role-based access is enforced as described.**
- **Product list and individual product endpoints are *accessible without authentication*.**
- **Standard error codes: 400 for validation errors, 401 for unauthorized, 403 for forbidden, 404 for not found.**
- For full details on expected response payloads, see your controller source and model/response classes.

***
