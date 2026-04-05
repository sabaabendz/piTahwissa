# Template Integration Summary

## Active user-management UI

- Admin dashboard: templates/admin/dashboard.html.twig
- Admin users index: templates/admin/users/index.html.twig
- Admin users table partial: templates/admin/users/_table.html.twig
- Shared navigation and shell:
  - templates/base.html.twig
  - templates/partials/_sidebar.html.twig
  - templates/partials/_member_topnav.html.twig

## Authentication UI

- templates/auth/login.html.twig
- templates/security/register.html.twig
- templates/auth/forgot_password.html.twig
- templates/auth/reset_password.html.twig

## Profile UI

- templates/frontoffice/profile/edit.html.twig

## Removed legacy UI

Legacy manager/collaborator dedicated templates were removed.
User roles are now represented only by role table values (USER, AGENT, ADMIN).
