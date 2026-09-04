# Konark — Local Simulation with Test Data

Run the Konark order-placement application fully offline against a **local SQL
Server 2019 in Docker**, seeded with realistic test data — no remote database
required.

## Quick start

```bash
./local-sim/run.sh          # idempotent: starts DB, loads data, (re)starts app
```

Then open **http://localhost:8080** and log in:

| Username | Password | Access |
|---|---|---|
| `admin` | `admin123` | All screens (Order Placement, Accounts Payable, Store Dashboard, Gift Card, Vendor Management) |
| `user`  | `user123` | Order Placement, Store Dashboard, Gift Card |

## What the run script does

1. Starts `konark-sqlserver` (SQL Server 2019, port **1433**, SA password
   `KonarkTest123!`, volume `konark-sql-data`) if not already running.
2. Loads `schema.sql` (creates the `Central` database + all tables) then
   `seed.sql` (test data) via `sqlcmd`.
3. Kills any running instance of the app and restarts it pointed at
   `jdbc:sqlserver://localhost:1433;databaseName=Central` (runtime overrides
   only — no source files are modified).

## Manual steps

```bash
docker run -d --name konark-sqlserver \
  -e ACCEPT_EULA=Y -e MSSQL_SA_PASSWORD='KonarkTest123!' \
  -e MSSQL_PID=Developer -p 1433:1433 \
  -v konark-sql-data:/var/opt/mssql \
  mcr.microsoft.com/mssql/server:2019-latest

docker cp local-sim/schema.sql konark-sqlserver:/tmp/schema.sql
docker cp local-sim/seed.sql  konark-sqlserver:/tmp/seed.sql

docker exec konark-sqlserver /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P 'KonarkTest123!' -i /tmp/schema.sql
docker exec konark-sqlserver /opt/mssql-tools18/bin/sqlcmd -C -S localhost -U sa -P 'KonarkTest123!' -d Central -i /tmp/seed.sql

# restart the app with local DB overrides (see run.sh for the full command)
```

## Linking restored Central and Aurora backups

Restore the Central backup as `CentralBackup963` and the Aurora backup as
`AuroraSalesSource`. Synchronize the Central master first:

```bash
docker cp local-sim/sync-central-master.sql konark-sqlserver:/tmp/sync-central-master.sql
docker exec konark-sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -C -S localhost -U sa -P 'KonarkTest123!' \
  -v SourceDatabase="CentralBackup963" TargetDatabase="Central" CentralStoreId="9999" \
  -i /tmp/sync-central-master.sql
```

This replaces only store `9999` master rows in the app-facing schema and loads
the Central standardized vendor catalog. Application users, orders, bills, and
other app-owned records remain intact.

Then synchronize Aurora products followed by sales. Restore the Aurora `cresql`
backup into the same local SQL Server instance under the isolated database name
`AuroraSalesSource`, then import its active product identities and vendor
mappings:

```bash
docker cp local-sim/sync-aurora-products.sql konark-sqlserver:/tmp/sync-aurora-products.sql
docker exec konark-sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -C -S localhost -U sa -P 'KonarkTest123!' \
  -i /tmp/sync-aurora-products.sql
```

The local seed item numbers do not match the real Aurora receipts, so product
identity must be synchronized before sales. Then import the invoice lines:

```bash
docker cp local-sim/sync-aurora-sales.sql konark-sqlserver:/tmp/sync-aurora-sales.sql
docker exec konark-sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -C -S localhost -U sa -P 'KonarkTest123!' \
  -v ReplaceAuroraSnapshot="1" \
  -i /tmp/sync-aurora-sales.sql
```

The product script replaces only store `7890` inventory and vendor mappings.
The sales script updates or inserts the store's invoice-line snapshot by
`Invoice_Number + LineNum + Store_ID`. The example enables authoritative
snapshot replacement for Aurora.

Run the scripts in this order after `run.sh`, because `run.sh` recreates the
app-facing schema and seed data:

1. `sync-central-master.sql`
2. `sync-aurora-products.sql`
3. `sync-aurora-sales.sql`

## Test data cheat sheet

- **Stores**: Omaha `1234`, Denver `5678`, Aurora `7890`, … (IDs match the
  hardcoded store map in `StoreController`).
- **Vendors**: `V0001` Fresh Farms Produce … `V0007` Tyson Foods.
- **Items**: 24 SKUs across 8 departments, present in stores `1234` and `5678`
  (inventory, vendor mappings, catalog, SKU tables).
- **Accounts payable**: 8 bills in the last 60 days for locations `Omaha` /
  `Denver` (plus one unassigned), with pending / overdue / paid statuses.
- **Gift cards**: card `GC-100001` for customer mobile `4025550100` (John Doe)
  with 4 transactions; `GC-100002` for `4025550101`.
- **Sales history**: ~8 days × 24 items × 2 stores of invoice lines over the
  last 30 days for the Store Dashboard chart and item-level sales history.
- **Product orders**: two sample orders (`SW_ProductOrders` / `...Items`).

## Notes & known app quirks

- **`spring.jpa.hibernate.dialect` in `application.properties` is ignored** by
  Spring Boot 2.4.x — the run script passes the dialect as a runtime override
  (`--spring.jpa.properties.hibernate.dialect=…`). The proper fix is
  `spring.jpa.database-platform` or `spring.jpa.properties.hibernate.dialect`.
- Placing an order writes a `ProductOrder-<id>.pdf` to the app's working
  directory and attempts an email (SMTP is likely unreachable; failures are
  swallowed).
