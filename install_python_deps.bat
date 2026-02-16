@echo off
echo ========================================
echo    TAHWISSA - Installation Python
echo ========================================
echo.

:: Vérifier si Python est installé
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERREUR] Python n'est pas installe ou pas dans le PATH
    echo.
    echo Telechargez Python depuis : https://www.python.org/downloads/
    echo IMPORTANT: Cochez "Add Python to PATH" lors de l'installation
    echo.
    pause
    exit /b 1
)

echo [OK] Python est installe
python --version
echo.

:: Installer les dépendances
echo Installation des dependances Python...
echo.
python -m pip install --upgrade pip
pip install -r requirements.txt

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   Installation terminee avec succes!
    echo ========================================
    echo.
    echo Vous pouvez maintenant lancer l'application JavaFX
    echo La verification biometrique est prete!
    echo.
) else (
    echo.
    echo [ERREUR] Echec de l'installation
    echo.
    pause
    exit /b 1
)

:: Tester le script
echo Test du script de verification...
cd scripts
python human_verification.py webcam 5 >nul 2>&1
cd ..

echo.
echo Appuyez sur une touche pour fermer...
pause >nul

