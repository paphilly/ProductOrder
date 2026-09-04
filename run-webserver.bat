@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo   ProductOrder Webserver - Standalone Runner
echo ===================================================
echo.

:: 1. Locate Java executable
set "JAVA_CMD=java"
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    )
)

:: Verify Java works
"%JAVA_CMD%" -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not installed or not in PATH / JAVA_HOME.
    echo Please ensure Java 17 or higher is installed and JAVA_HOME is set.
    echo.
    pause
    exit /b 1
)

:: 2. Locate JAR file
set "JAR_FILE="
if exist "com.konark\target\com.konark-0.0.1-SNAPSHOT.jar" (
    set "JAR_FILE=com.konark\target\com.konark-0.0.1-SNAPSHOT.jar"
) else if exist "target\com.konark-0.0.1-SNAPSHOT.jar" (
    set "JAR_FILE=target\com.konark-0.0.1-SNAPSHOT.jar"
) else if exist "com.konark-0.0.1-SNAPSHOT.jar" (
    set "JAR_FILE=com.konark-0.0.1-SNAPSHOT.jar"
)

if "!JAR_FILE!"=="" (
    echo [INFO] JAR file not found. Building project with Maven...
    if exist "com.konark\mvnw.cmd" (
        cd com.konark
        call mvnw.cmd package -DskipTests
        cd ..
        set "JAR_FILE=com.konark\target\com.konark-0.0.1-SNAPSHOT.jar"
    ) else if exist "mvnw.cmd" (
        call mvnw.cmd package -DskipTests
        set "JAR_FILE=target\com.konark-0.0.1-SNAPSHOT.jar"
    )
)

if not exist "!JAR_FILE!" (
    echo [ERROR] Could not find or build com.konark-0.0.1-SNAPSHOT.jar.
    pause
    exit /b 1
)

:: 3. Locate properties file
set "CONFIG_ARG="
if exist "C:\ProductOrder\config\application-prod.properties" (
    set "CONFIG_ARG=--spring.config.location=file:C:/ProductOrder/config/application-prod.properties"
    echo [INFO] Using configuration: C:\ProductOrder\config\application-prod.properties
) else if exist "application-prod.properties" (
    set "CONFIG_ARG=--spring.config.location=file:application-prod.properties"
    echo [INFO] Using configuration: application-prod.properties
) else (
    echo [WARNING] External application-prod.properties not found. Running with default configuration.
)

echo.
echo [INFO] Starting ProductOrder Webserver...
echo Executing: "%JAVA_CMD%" -jar "!JAR_FILE!" !CONFIG_ARG!
echo.

"%JAVA_CMD%" -jar "!JAR_FILE!" !CONFIG_ARG!

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Webserver exited with error code %ERRORLEVEL%.
)

echo.
pause
