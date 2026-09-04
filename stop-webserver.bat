@echo off
echo [INFO] Stopping and removing ProductOrder Webserver container...
docker stop productorder-webserver >nul 2>&1
docker rm productorder-webserver >nul 2>&1
echo [SUCCESS] Container stopped.
pause
