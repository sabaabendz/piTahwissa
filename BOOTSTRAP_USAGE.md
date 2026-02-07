# Bootstrap Usage Guide

Bootstrap 5.3.8 has been successfully installed and configured in your Symfony project using Asset Mapper.

## ✅ Installation Complete

Bootstrap is now available in all your Twig templates. The following have been installed:
- **Bootstrap CSS** (v5.3.8)
- **Bootstrap JavaScript** (v5.3.8)
- **Popper.js** (v2.11.8) - Required for Bootstrap JS components

## 🚀 How to Use Bootstrap

### 1. In Twig Templates

Bootstrap is automatically loaded via the `base.html.twig` template. Simply use Bootstrap classes in your templates:

```twig
{% extends 'base.html.twig' %}

{% block body %}
    <div class="container mt-5">
        <div class="row">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title">Bootstrap Card</h5>
                    </div>
                    <div class="card-body">
                        <p class="card-text">Bootstrap is working!</p>
                        <button class="btn btn-primary">Click Me</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
{% endblock %}
```

### 2. Available Bootstrap Components

All Bootstrap 5 components are available:
- **Layout**: Container, Grid System, Flexbox
- **Components**: Buttons, Cards, Forms, Modals, Navbar, Dropdowns, Alerts, etc.
- **Utilities**: Spacing, Colors, Typography, Borders, Shadows, etc.

### 3. Example: Bootstrap Form

```twig
{% extends 'base.html.twig' %}

{% block body %}
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow">
                    <div class="card-body">
                        <h2 class="card-title mb-4">Login Form</h2>
                        <form>
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" class="form-control" id="email" required>
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label">Password</label>
                                <input type="password" class="form-control" id="password" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Login</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
{% endblock %}
```

### 4. Example: Bootstrap Alert

```twig
<div class="alert alert-success alert-dismissible fade show" role="alert">
    <strong>Success!</strong> Your action was completed successfully.
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
```

### 5. Example: Bootstrap Modal

```twig
<!-- Button trigger modal -->
<button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#exampleModal">
    Open Modal
</button>

<!-- Modal -->
<div class="modal fade" id="exampleModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Modal Title</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                Modal content goes here.
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary">Save changes</button>
            </div>
        </div>
    </div>
</div>
```

### 6. Custom Styles

You can add custom CSS in `assets/styles/app.css`:

```css
/* Your custom styles */
.custom-class {
    /* Your styles here */
}
```

Bootstrap classes will work alongside your custom styles.

## 📚 Bootstrap Documentation

For complete Bootstrap documentation, visit:
- **Official Docs**: https://getbootstrap.com/docs/5.3/
- **Components**: https://getbootstrap.com/docs/5.3/components/
- **Utilities**: https://getbootstrap.com/docs/5.3/utilities/

## 🔧 Files Modified

1. **`importmap.php`** - Added Bootstrap and dependencies
2. **`assets/app.js`** - Imports Bootstrap CSS and JS
3. **`assets/styles/app.css`** - Ready for your custom styles

## ✨ Quick Test

To verify Bootstrap is working, create a test template:

```twig
{% extends 'base.html.twig' %}

{% block body %}
    <div class="container mt-5">
        <h1 class="text-primary">Bootstrap is Working! 🎉</h1>
        <button class="btn btn-success">Success Button</button>
        <button class="btn btn-danger">Danger Button</button>
    </div>
{% endblock %}
```

If you see styled buttons and text, Bootstrap is correctly installed!

## 🎨 Bootstrap Icons (Optional)

If you need icons, you can install Bootstrap Icons:

```bash
php bin/console importmap:require bootstrap-icons
```

Then use in templates:
```twig
<i class="bi bi-check-circle"></i>
```
