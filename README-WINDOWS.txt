========================================================================
 ProductOrder Webserver - Windows Deployment Guide
========================================================================

Prerequisites:
- Windows 10/11 or Windows Server with Docker Desktop installed.
- Access to your external SQL Server database.

Quick Start Instructions:

Option 1: Using run-webserver.bat (Recommended)
-----------------------------------------------
1. Double-click `run-webserver.bat` (or right-click -> Run as administrator).
2. If no `.env` file exists, the script will prompt you for:
   - External Database Host / IP
   - Database Port (default: 1433)
   - Database Name (default: Central)
   - Database Username & Password
3. The script will automatically build the Docker image and start the webserver container on port 8080.
4. Access the web application at http://localhost:8080

Option 2: Using .env File & Docker Compose
------------------------------------------
1. Copy `.env.example` to `.env` in the same directory.
2. Edit `.env` to set your database host, username, and password:
   SPRING_DATASOURCE_URL=jdbc:sqlserver://YOUR_DB_HOST:1433;databaseName=Central
   SPRING_DATASOURCE_USERNAME=sa
   SPRING_DATASOURCE_PASSWORD=YOUR_PASSWORD
3. Run in Command Prompt or PowerShell:
   docker-compose up -d

Stopping the Webserver:
-----------------------
- Double-click `stop-webserver.bat`
- Or run `docker stop productorder-webserver`
