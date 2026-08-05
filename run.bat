@echo off
echo =========================================================
echo   Compiling and Launching Nexus3D Location Portal (Java)
echo =========================================================

if not exist "bin" mkdir bin

set "JAVAC_PATH=C:\Program Files\Java\jdk-21.0.12\bin\javac.exe"
set "JAVA_PATH=C:\Program Files\Java\jdk-21.0.12\bin\java.exe"

if not exist "%JAVAC_PATH%" (
    set "JAVAC_PATH=javac"
    set "JAVA_PATH=java"
)

echo Compiling Java source files...
"%JAVAC_PATH%" -d bin src/main/java/com/locationapp/*.java

if %ERRORLEVEL% NEQ 0 (
    echo Error: Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Starting Nexus3D Server on http://localhost:8080...
"%JAVA_PATH%" -cp bin com.locationapp.LocationLoginServer

pause
