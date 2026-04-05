# Controller/Template Integration

## Current core routes

- /dashboard -> app_dashboard_index, app_dashboard
- /admin -> app_admin_dashboard
- /admin/users -> app_admin_users_index
- /profile/edit -> app_front_profile_edit
- /login -> app_login
- /register -> app_register

## Current user-management flow

- User listing and role changes are handled by Admin User controller/actions.
- Templates used:
  - templates/admin/dashboard.html.twig
  - templates/admin/users/index.html.twig
  - templates/admin/users/_table.html.twig

## Deprecated modules removed

The legacy manager/collaborator modules are removed from runtime code.
Routes, controllers, repositories, forms, entities, and Twig pages for these modules are no longer part of the active application.
