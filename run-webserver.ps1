# ProductOrder Webserver Launcher for PowerShell
$ErrorActionPreference = "Stop"

Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  ProductOrder Webserver - Standalone Runner (PS)" -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Locate Java executable
$javaExe = "java"
if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $javaExe = "$env:JAVA_HOME\bin\java.exe"
}

# Verify Java
try {
    & $javaExe -version 2>&1 | Out-Null
} catch {
    Write-Host "[ERROR] Java is not installed or not found in PATH / JAVA_HOME." -ForegroundColor Red
    Read-Host "Press Enter to exit..."
    exit 1
}

# 2. Locate JAR file
$jarFile = ""
$possiblePaths = @(
    "com.konark\target\com.konark-0.0.1-SNAPSHOT.jar",
    "target\com.konark-0.0.1-SNAPSHOT.jar",
    "com.konark-0.0.1-SNAPSHOT.jar"
)

foreach ($path in $possiblePaths) {
    if (Test-Path $path) {
        $jarFile = $path
        break
    }
}

if (-not $jarFile) {
    Write-Host "[INFO] JAR file not found. Building project with Maven..." -ForegroundColor Yellow
    if (Test-Path "com.konark\mvnw.cmd") {
        Set-Location com.konark
        .\mvnw.cmd package -DskipTests
        Set-Location ..
        $jarFile = "com.konark\target\com.konark-0.0.1-SNAPSHOT.jar"
    } elseif (Test-Path "mvnw.cmd") {
        .\mvnw.cmd package -DskipTests
        $jarFile = "target\com.konark-0.0.1-SNAPSHOT.jar"
    }
}

if (-not (Test-Path $jarFile)) {
    Write-Host "[ERROR] Could not locate or build $jarFile" -ForegroundColor Red
    Read-Host "Press Enter to exit..."
    exit 1
}

# 3. Locate properties file
$configArg = ""
if (Test-Path "C:\ProductOrder\config\application-prod.properties") {
    $configArg = "--spring.config.location=file:C:/ProductOrder/config/application-prod.properties"
    Write-Host "[INFO] Using configuration: C:\ProductOrder\config\application-prod.properties" -ForegroundColor Green
} elseif (Test-Path "application-prod.properties") {
    $configArg = "--spring.config.location=file:application-prod.properties"
    Write-Host "[INFO] Using configuration: application-prod.properties" -ForegroundColor Green
} else {
    Write-Host "[WARNING] External application-prod.properties not found. Running with default configuration." -ForegroundColor Yellow
}

Write-Host "[INFO] Starting ProductOrder Webserver..." -ForegroundColor Green
Write-Host "Executing: & '$javaExe' -jar '$jarFile' $configArg" -ForegroundColor Gray
Write-Host ""

if ($configArg) {
    & $javaExe -jar $jarFile $configArg
} else {
    & $javaExe -jar $jarFile
}
