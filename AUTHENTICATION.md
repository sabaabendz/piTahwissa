# Authentication System Documentation

This document explains the authentication and authorization system implemented in the SmartTask Manager project.

## 1. Login & Register Flow

### Registration
The registration flow is handled by `App\Controller\AuthController::register`. 
1.  **Role Selection**: Users first select a role (Manager or Collaborator) on the `/register` page. This selection is handled via JavaScript in `templates/auth/register.html.twig`, which re-submits the form to the same route with a `role` parameter.
2.  **Form Submission**:
    *   **Manager**: Uses `ManagerType`. A unique **Enterprise Code** is automatically generated upon successful registration using the `EnterpriseCodeGenerator` service.
    *   **Collaborator**: Uses `CollaboratorType`. Collaborators must provide an existing **Enterprise Code** to join an organization.
3.  **Validation**: Server-side validation is performed using Symfony Constraints (`#[Assert\...]`) defined in `App\Entity\User`, `App\Entity\Manager`, and `App\Entity\Collaborator`.
4.  **Persistence**: Passwords are hashed using `UserPasswordHasherInterface`.

### Login
The login flow uses Symfony's built-in `form_login` authenticator.
1.  **Route**: `/login` (`app_login`).
2.  **Process**: Symfony intercepts the POST request, validates credentials against the `app_user_provider` (which uses the `email` field), and starts a session.
3.  **Redirect**: After a successful login, users are redirected to the dashboard (`app_dashboard_index`). Authenticated users trying to access `/login` or `/register` are also redirected there.

---

## 2. User Roles & Logic

### Entities & Hierarchy
The project uses Doctrine's **Joined Table Inheritance** for the `User` entity.
-   **User**: Base class containing common fields (`email`, `name`, `password`, `googleId`).
-   **Manager**: Extends `User`. Has `level`, `department`, and a unique `enterpriseCode`. Role: `ROLE_MANAGER`.
-   **Collaborator**: Extends `User`. Has `post`, `team`, and an `enterpriseCode` matching their manager's. Role: `ROLE_COLLABORATOR`.

### Tenant Isolation (Enterprise Code)
The `enterpriseCode` is the core of the multi-tenant logic:
-   A **Manager** creates a new enterprise and gets a unique code (e.g., `ABC1234`).
-   **Collaborators** use this code during registration to join that specific enterprise.
-   **Visibility Rule**: Managers and Collaborators can only see other collaborators who share the same `enterpriseCode`. This is enforced in the `CollaboratorController` using the `SecurityHelper` service.

---

## 3. Controllers

### AuthController
-   **Purpose**: Manages standard web authentication actions.
-   **Routes**:
    -   `GET/POST /login`: Login page.
    -   `GET/POST /register`: Registration page with role-specific logic.
    -   `GET /forgot-password`: Password recovery entry.
    -   `GET /logout`: Intercepted by Symfony Security.

### ConnectGoogleController
-   **Purpose**: Entry point for Google OAuth2.
-   **Routes**:
    -   `GET /connect/google`: Initiates the Google login process (stores `role` in session if registering).
    -   `GET /connect/google/check`: The callback route (intercepted by `GoogleAuthenticator`).

---

## 4. Google Authentication Service

### Implementation
Google Authentication is implemented using the `knpu/oauth2-client-bundle`.
-   **Authenticator**: `App\Security\GoogleAuthenticator` handles the OAuth flow.
-   **Flow**:
    1.  User clicks "Continue with Google".
    2.  Redirected to Google for approval.
    3.  Redirected back to `/connect/google/check`.
    4.  The authenticator fetches the Google user.
    5.  **If User exists**: Logs them in.
    6.  **If New User**: Creates a `Manager` or `Collaborator` entity based on the `role` stored in the session. Default values are assigned to required fields (e.g., Level = 'Owner' for Managers).

### Configuration
Managed in `config/packages/knpu_oauth2_client.yaml` using:
-   `OAUTH_GOOGLE_CLIENT_ID`
-   `OAUTH_GOOGLE_CLIENT_SECRET`

---

## 5. Other Services

### SecurityHelper
-   **Responsibility**: Centralizes security and tenant isolation logic.
-   **Key Methods**:
    -   `getEnterpriseCode(UserInterface $user)`: Retrieves the enterprise code regardless of user type.
    -   `canAccessCollaborator(UserInterface $user, Collaborator $target)`: Checks if a user has permission to view/edit a specific collaborator based on their enterprise code.

### EnterpriseCodeGenerator
-   **Responsibility**: Generates human-readable, unique enterprise codes.
-   **Format**: 3 uppercase letters followed by 4 digits (e.g., `XYZ9876`).

---

## 6. Important Twig Files

### auth/login.html.twig
-   **Purpose**: The main login interface.
-   **Key Variables**: `error` (last auth error), `last_username`.
-   **Includes**: CSRF token field, "Remember Me" checkbox, and Google Login button.

### auth/register.html.twig
-   **Purpose**: Role-based registration form.
-   **Key Logic**: Contains a JavaScript block that handles role switching and dynamic form rendering by re-submitting the role to `AuthController`.
-   **Alerts**: Displays flash messages (e.g., successful manager creation with the enterprise code).

---

## 7. Security Configuration (`security.yaml`)

-   **Password Hashing**: Uses `auto` hasher for `PasswordAuthenticatedUserInterface`.
-   **Firewalls**:
    -   `dev`: Disables security for profiler and assets.
    -   `main`: Configures `form_login`, `logout`, and `App\Security\GoogleAuthenticator`.
-   **Access Control**:
    -   `/login`, `/register`, `/connect/google`: Public access.
    -   `/dashboard`, `/projet`, `/tache`: Restricted to `ROLE_USER`.
    -   `/manager`: Restricted to `ROLE_MANAGER`.
