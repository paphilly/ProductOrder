#!/usr/bin/env bash
# ============================================================================
# Konark local simulation launcher
#  1. Ensures SQL Server 2019 is running in Docker on port 1433
#  2. Loads schema.sql + seed.sql into the 'Central' database
#  3. (Re)starts the Spring Boot app pointed at the local database
#
# Usage:  ./local-sim/run.sh
# The app is then available at http://localhost:8080
#   Login: admin / admin123   (or user / user123)
# ============================================================================
set -euo pipefail

CONTAINER=konark-sqlserver
SA_PASS='KonarkTest123!'
DB_NAME=Central
APP_DIR=/home/surya/ProductOrder/com.konark
APP_JAR="$APP_DIR/target/com.konark-0.0.1-SNAPSHOT.jar"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQLCMD="docker exec $CONTAINER /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P $SA_PASS"

# --- 1. SQL Server container ------------------------------------------------
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
  echo ">> Starting SQL Server container..."
  docker rm -f "$CONTAINER" >/dev/null 2>&1 || true
  docker run -d --name "$CONTAINER" \
    -e ACCEPT_EULA=Y \
    -e MSSQL_SA_PASSWORD="$SA_PASS" \
    -e MSSQL_PID=Developer \
    -p 1433:1433 \
    -v konark-sql-data:/var/opt/mssql \
    mcr.microsoft.com/mssql/server:2019-latest >/dev/null

  echo ">> Waiting for SQL Server to become ready..."
  ready=0
  for i in $(seq 1 60); do
    if $SQLCMD -Q "SELECT 1" -h -1 >/dev/null 2>&1; then ready=1; break; fi
    sleep 5
  done
  if [ "$ready" != "1" ]; then
    echo "!! SQL Server did not become ready in time. Check: docker logs $CONTAINER"
    exit 1
  fi
fi

# --- 2. Schema + seed --------------------------------------------------------
echo ">> Loading schema ($SCRIPT_DIR/schema.sql)..."
docker cp "$SCRIPT_DIR/schema.sql" "$CONTAINER":/tmp/schema.sql
$SQLCMD -i /tmp/schema.sql

echo ">> Loading seed data ($SCRIPT_DIR/seed.sql)..."
docker cp "$SCRIPT_DIR/seed.sql" "$CONTAINER":/tmp/seed.sql
$SQLCMD -d "$DB_NAME" -i /tmp/seed.sql

# --- 3. Restart the app ------------------------------------------------------
echo ">> Restarting app against local SQL Server..."
# '^java -jar' only matches the actual java process, never this shell
pkill -f '^java -jar target/com.konark' 2>/dev/null || true
sleep 2

cd "$APP_DIR"
setsid nohup java -jar "$APP_JAR" \
  --spring.datasource.url="jdbc:sqlserver://localhost:1433;databaseName=$DB_NAME" \
  --spring.datasource.username=sa \
  --spring.datasource.password="$SA_PASS" \
  --spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect \
  > /tmp/konark.log 2>&1 < /dev/null &
disown

echo ">> App starting... log: /tmp/konark.log"
echo ">> Open http://localhost:8080  (admin / admin123)"
