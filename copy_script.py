import os
import shutil

src_base = r"c:\Users\sabso\IdeaProjects\pidevproject\wetransfer_workshopa6_2026-02-27_0449\workshopA6"
dest_base = r"c:\Users\sabso\IdeaProjects\pidevproject\testunitaire\AdminandAgentdashboard\GestionReservation"

# Files to copy (src relative path, dest relative path, package name)
files = [
    (r"src\main\java\entities\User.java", r"src\main\java\tn\esprit\tahwissa\models\User.java", "tn.esprit.tahwissa.models"),
    (r"src\main\java\services\UserService.java", r"src\main\java\tn\esprit\tahwissa\services\UserService.java", "tn.esprit.tahwissa.services"),
    (r"src\main\java\services\PythonBiometricService.java", r"src\main\java\tn\esprit\tahwissa\services\PythonBiometricService.java", "tn.esprit.tahwissa.services"),
    (r"src\main\java\services\MailService.java", r"src\main\java\tn\esprit\tahwissa\services\MailService.java", "tn.esprit.tahwissa.services"),
    (r"src\main\java\services\PasswordResetService.java", r"src\main\java\tn\esprit\tahwissa\services\PasswordResetService.java", "tn.esprit.tahwissa.services"),
    (r"src\main\java\services\IService.java", r"src\main\java\tn\esprit\tahwissa\services\IService.java", "tn.esprit.tahwissa.services"),
    (r"src\main\java\services\FaceRecognitionService.java", r"src\main\java\tn\esprit\tahwissa\services\FaceRecognitionService.java", "tn.esprit.tahwissa.services"),
    (r"src\main\java\utils\SessionManager.java", r"src\main\java\tn\esprit\tahwissa\utils\SessionManager.java", "tn.esprit.tahwissa.utils"),
    (r"src\main\java\utils\EmailConfig.java", r"src\main\java\tn\esprit\tahwissa\utils\EmailConfig.java", "tn.esprit.tahwissa.utils"),
    (r"src\main\java\utils\GoogleOAuthConfig.java", r"src\main\java\tn\esprit\tahwissa\utils\GoogleOAuthConfig.java", "tn.esprit.tahwissa.utils"),
    (r"src\main\java\controller\LoginController.java", r"src\main\java\tn\esprit\tahwissa\controllers\LoginController.java", "tn.esprit.tahwissa.controllers"),
    (r"src\main\java\controller\RegisterController.java", r"src\main\java\tn\esprit\tahwissa\controllers\RegisterController.java", "tn.esprit.tahwissa.controllers"),
    (r"src\main\java\controller\ComingSoonController.java", r"src\main\java\tn\esprit\tahwissa\controllers\ComingSoonController.java", "tn.esprit.tahwissa.controllers"),
]

for src_rel, dest_rel, pkg in files:
    src = os.path.join(src_base, src_rel)
    dest = os.path.join(dest_base, dest_rel)
    if not os.path.exists(src):
        print(f"File not found: {src}")
        continue
    os.makedirs(os.path.dirname(dest), exist_ok=True)
    with open(src, 'r', encoding='utf-8') as f:
        content = f.read()

    # Replace packages
    content = content.replace("package entities;", "package tn.esprit.tahwissa.models;")
    content = content.replace("package services;", "package tn.esprit.tahwissa.services;")
    content = content.replace("package utils;", "package tn.esprit.tahwissa.utils;")
    content = content.replace("package controller;", "package tn.esprit.tahwissa.controllers;")
    
    # Replace imports
    content = content.replace("import entities.User;", "import tn.esprit.tahwissa.models.User;")
    content = content.replace("import services.", "import tn.esprit.tahwissa.services.")
    content = content.replace("import utils.SessionManager;", "import tn.esprit.tahwissa.utils.SessionManager;")
    content = content.replace("import utils.EmailConfig;", "import tn.esprit.tahwissa.utils.EmailConfig;")
    content = content.replace("import utils.MyDatabase;", "import tn.esprit.tahwissa.config.Database;")
    content = content.replace("utils.MyDatabase", "tn.esprit.tahwissa.config.Database")
    
    # Specific controller references fixing
    content = content.replace("import controller.DashboardController;", "import tn.esprit.tahwissa.controllers.admin.AdminDashboardController;")
    
    with open(dest, 'w', encoding='utf-8') as f:
        f.write(content)

# FXML files
fxml_files = [
    (r"src\main\resources\view\login.fxml", r"src\main\resources\fxml\login.fxml"),
    (r"src\main\resources\view\register.fxml", r"src\main\resources\fxml\register.fxml"),
    (r"src\main\resources\view\coming-soon.fxml", r"src\main\resources\fxml\coming-soon.fxml"),
]

for src_rel, dest_rel in fxml_files:
    src = os.path.join(src_base, src_rel)
    dest = os.path.join(dest_base, dest_rel)
    if not os.path.exists(src):
        print(f"File not found: {src}")
        continue
    os.makedirs(os.path.dirname(dest), exist_ok=True)
    with open(src, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Replace FXML controller path
    content = content.replace('controller="controller.', 'controller="tn.esprit.tahwissa.controllers.')
    
    # Correct stylesheet/image paths if necessary
    content = content.replace('"/styles/', '"/css/')
    
    with open(dest, 'w', encoding='utf-8') as f:
        f.write(content)

# Resources (CSS, Images)
source_css = os.path.join(src_base, "src", "main", "resources", "styles")
dest_css = os.path.join(dest_base, "src", "main", "resources", "css")
if os.path.exists(source_css):
    shutil.copytree(source_css, dest_css, dirs_exist_ok=True)

# Copy python scripts folder
source_scripts = os.path.join(src_base, "scripts")
dest_scripts = os.path.join(dest_base, "scripts")
if os.path.exists(source_scripts):
    shutil.copytree(source_scripts, dest_scripts, dirs_exist_ok=True)
print("done")
