@echo off
echo ====================================
echo COMPILATION ET EXECUTION TAHWISSA
echo ====================================
echo.

cd /d "%~dp0"

echo [1/3] Recherche de Maven...
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Maven n'est pas dans le PATH. Recherche dans les emplacements communs...

    if exist "C:\Program Files\Apache Maven\bin\mvn.cmd" (
        set "MAVEN_CMD=C:\Program Files\Apache Maven\bin\mvn.cmd"
    ) else if exist "C:\maven\bin\mvn.cmd" (
        set "MAVEN_CMD=C:\maven\bin\mvn.cmd"
    ) else if exist "%USERPROFILE%\maven\bin\mvn.cmd" (
        set "MAVEN_CMD=%USERPROFILE%\maven\bin\mvn.cmd"
    ) else (
        echo.
        echo ERREUR: Maven introuvable!
        echo Veuillez installer Maven ou l'ajouter au PATH.
        pause
        exit /b 1
    )
) else (
    set "MAVEN_CMD=mvn"
)

echo Maven trouve: %MAVEN_CMD%
echo.

echo [2/3] Compilation du projet...
call "%MAVEN_CMD%" clean compile
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERREUR lors de la compilation!
    pause
    exit /b 1
)

echo.
echo [3/3] Lancement de l'application...
call "%MAVEN_CMD%" javafx:run

pause

