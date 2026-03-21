@echo off
setlocal

:: ── Naruto Tower Defense — build and run (Windows) ───────────────────────
:: Usage:  run.bat
:: Builds a self-contained fat jar via Maven Shade, then starts the server.
:: The game will be available at http://localhost:8080

:: Locate Java 17 (Temurin or any JDK 17 in Program Files)
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [WARN] JAVA_HOME not found at expected path.
    echo        Trying PATH java...
    set "JAVA_HOME="
)

set "MVN=mvn"

echo [1/2] Building fat jar...
%MVN% package -q
if errorlevel 1 (
    echo [ERROR] Build failed. Fix compile errors and try again.
    pause
    exit /b 1
)

echo [2/2] Starting server...
echo       Open http://localhost:8080 in your browser.
echo       Press Ctrl+C to stop.
echo.

if defined JAVA_HOME (
    "%JAVA_HOME%\bin\java.exe" -jar target\naruto-tower-defense-1.0-SNAPSHOT.jar
) else (
    java -jar target\naruto-tower-defense-1.0-SNAPSHOT.jar
)

endlocal
