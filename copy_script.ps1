$srcBase = "c:\Users\sabso\IdeaProjects\pidevproject\wetransfer_workshopa6_2026-02-27_0449\workshopA6"
$destBase = "c:\Users\sabso\IdeaProjects\pidevproject\testunitaire\AdminandAgentdashboard\GestionReservation"

$files = @(
    @{ src="src\main\java\entities\User.java"; dest="src\main\java\tn\esprit\tahwissa\models\User.java" },
    @{ src="src\main\java\services\UserService.java"; dest="src\main\java\tn\esprit\tahwissa\services\UserService.java" },
    @{ src="src\main\java\services\PythonBiometricService.java"; dest="src\main\java\tn\esprit\tahwissa\services\PythonBiometricService.java" },
    @{ src="src\main\java\services\MailService.java"; dest="src\main\java\tn\esprit\tahwissa\services\MailService.java" },
    @{ src="src\main\java\services\PasswordResetService.java"; dest="src\main\java\tn\esprit\tahwissa\services\PasswordResetService.java" },
    @{ src="src\main\java\services\IService.java"; dest="src\main\java\tn\esprit\tahwissa\services\IService.java" },
    @{ src="src\main\java\services\FaceRecognitionService.java"; dest="src\main\java\tn\esprit\tahwissa\services\FaceRecognitionService.java" },
    @{ src="src\main\java\utils\SessionManager.java"; dest="src\main\java\tn\esprit\tahwissa\utils\SessionManager.java" },
    @{ src="src\main\java\utils\EmailConfig.java"; dest="src\main\java\tn\esprit\tahwissa\utils\EmailConfig.java" },
    @{ src="src\main\java\utils\GoogleOAuthConfig.java"; dest="src\main\java\tn\esprit\tahwissa\utils\GoogleOAuthConfig.java" },
    @{ src="src\main\java\controller\LoginController.java"; dest="src\main\java\tn\esprit\tahwissa\controllers\LoginController.java" },
    @{ src="src\main\java\controller\RegisterController.java"; dest="src\main\java\tn\esprit\tahwissa\controllers\RegisterController.java" },
    @{ src="src\main\java\controller\ComingSoonController.java"; dest="src\main\java\tn\esprit\tahwissa\controllers\ComingSoonController.java" }
)

foreach ($file in $files) {
    $srcFile = Join-Path $srcBase $file.src
    $destFile = Join-Path $destBase $file.dest
    if (Test-Path $srcFile) {
        $destDir = Split-Path $destFile -Parent
        if (!(Test-Path $destDir)) { New-Item -ItemType Directory -Force -Path $destDir | Out-Null }
        $content = Get-Content $srcFile -Raw
        $content = $content -replace 'package entities;', 'package tn.esprit.tahwissa.models;'
        $content = $content -replace 'package services;', 'package tn.esprit.tahwissa.services;'
        $content = $content -replace 'package utils;', 'package tn.esprit.tahwissa.utils;'
        $content = $content -replace 'package controller;', 'package tn.esprit.tahwissa.controllers;'
        $content = $content -replace 'import entities\.User;', 'import tn.esprit.tahwissa.models.User;'
        $content = $content -replace 'import services\.', 'import tn.esprit.tahwissa.services.'
        $content = $content -replace 'import utils\.SessionManager;', 'import tn.esprit.tahwissa.utils.SessionManager;'
        $content = $content -replace 'import utils\.EmailConfig;', 'import tn.esprit.tahwissa.utils.EmailConfig;'
        $content = $content -replace 'import utils\.MyDatabase;', 'import tn.esprit.tahwissa.config.Database;'
        $content = $content -replace 'utils\.MyDatabase', 'tn.esprit.tahwissa.config.Database'
        $content = $content -replace 'import controller\.DashboardController;', 'import tn.esprit.tahwissa.controllers.admin.AdminDashboardController;'
        Set-Content -Path $destFile -Value $content -Encoding UTF8
    } else {
        Write-Host "Not found: $srcFile"
    }
}

$fxmlFiles = @(
    @{ src="src\main\resources\view\login.fxml"; dest="src\main\resources\fxml\login.fxml" },
    @{ src="src\main\resources\view\register.fxml"; dest="src\main\resources\fxml\register.fxml" },
    @{ src="src\main\resources\view\coming-soon.fxml"; dest="src\main\resources\fxml\coming-soon.fxml" }
)

foreach ($file in $fxmlFiles) {
    $srcFile = Join-Path $srcBase $file.src
    $destFile = Join-Path $destBase $file.dest
    if (Test-Path $srcFile) {
        $destDir = Split-Path $destFile -Parent
        if (!(Test-Path $destDir)) { New-Item -ItemType Directory -Force -Path $destDir | Out-Null }
        $content = Get-Content $srcFile -Raw
        $content = $content -replace 'controller="controller\.', 'controller="tn.esprit.tahwissa.controllers.'
        $content = $content -replace '"/styles/', '"/css/'
        Set-Content -Path $destFile -Value $content -Encoding UTF8
    }
}

$srcScripts = Join-Path $srcBase "scripts"
$destScripts = Join-Path $destBase "scripts"
if (Test-Path $srcScripts) { Copy-Item -Path $srcScripts -Destination $destScripts -Recurse -Force }

$srcCss = Join-Path $srcBase "src\main\resources\styles"
$destCss = Join-Path $destBase "src\main\resources\css"
if (Test-Path $srcCss) { Copy-Item -Path "$srcCss\*" -Destination $destCss -Recurse -Force }

echo "powershell copy done"
