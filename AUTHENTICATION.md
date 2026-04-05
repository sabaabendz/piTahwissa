# Authentication and Authorization

This project now uses a role-table model only.

## Current role model

- Users are stored in the user table.
- Each user is linked to the role table via user.role_id.
- Supported application roles are:
  - USER -> ROLE_USER
  - AGENT -> ROLE_AGENT
  - ADMIN -> ROLE_ADMIN

## Login and registration

- Route /login handles form authentication.
- Route /register creates a user with default role USER (role id 1).
- OAuth login (Google/GitHub) creates or reuses users by email.

## Access control

- Public routes: login, register, forgot/reset password.
- Authenticated users can access dashboard and profile.
- Admin routes are under /admin and require ROLE_ADMIN.

## Migration note

Legacy manager/collaborator entities, repositories, forms, controllers, routes, and templates were removed.
Roles must be managed through the role table and admin user management screens.
