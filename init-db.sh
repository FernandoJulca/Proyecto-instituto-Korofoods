#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE "DB_USER_SERVICE";
    CREATE DATABASE "DB_TABLE_SERVICE";
    CREATE DATABASE "DB_RESERVATION_SERVICE";
    CREATE DATABASE "DB_PAYMENT_SERVICE";
    CREATE DATABASE "DB_ORDER_SERVICE";
    CREATE DATABASE "DB_MENU_SERVICE";
    CREATE DATABASE "DB_EVENT_SERVICE";
    CREATE DATABASE "DB_QUALIFICATION_SERVICE";
EOSQL

psql --username "$POSTGRES_USER" --dbname="DB_USER_SERVICE" -f /docker-entrypoint-initdb.d/scripts/user-tablas.sql
psql --username "$POSTGRES_USER" --dbname="DB_USER_SERVICE" -f /docker-entrypoint-initdb.d/scripts/user-inserts.sql

psql --username "$POSTGRES_USER" --dbname="DB_TABLE_SERVICE" -f /docker-entrypoint-initdb.d/scripts/table-tablas.sql
psql --username "$POSTGRES_USER" --dbname="DB_TABLE_SERVICE" -f /docker-entrypoint-initdb.d/scripts/table-inserts.sql

psql --username "$POSTGRES_USER" --dbname="DB_RESERVATION_SERVICE" -f /docker-entrypoint-initdb.d/scripts/reservation-tablas.sql
psql --username "$POSTGRES_USER" --dbname="DB_RESERVATION_SERVICE" -f /docker-entrypoint-initdb.d/scripts/reservation-inserts.sql

psql --username "$POSTGRES_USER" --dbname="DB_QUALIFICATION_SERVICE" -f /docker-entrypoint-initdb.d/scripts/qualification-tablas.sql
psql --username "$POSTGRES_USER" --dbname="DB_QUALIFICATION_SERVICE" -f /docker-entrypoint-initdb.d/scripts/qualification-inserts.sql

psql --username "$POSTGRES_USER" --dbname="DB_PAYMENT_SERVICE" -f /docker-entrypoint-initdb.d/scripts/payment-tablas.sql
psql --username "$POSTGRES_USER" --dbname="DB_PAYMENT_SERVICE" -f /docker-entrypoint-initdb.d/scripts/payment-inserts.sql

psql --username "$POSTGRES_USER" --dbname="DB_ORDER_SERVICE" -f /docker-entrypoint-initdb.d/scripts/order-tablas.sql
psql --username "$POSTGRES_USER" --dbname="DB_ORDER_SERVICE" -f /docker-entrypoint-initdb.d/scripts/order-inserts.sql

psql --username "$POSTGRES_USER" --dbname="DB_MENU_SERVICE" -f /docker-entrypoint-initdb.d/scripts/menu-tablas.sql
psql --username "$POSTGRES_USER" --dbname="DB_MENU_SERVICE" -f /docker-entrypoint-initdb.d/scripts/menu-inserts.sql

psql --username "$POSTGRES_USER" --dbname="DB_EVENT_SERVICE" -f /docker-entrypoint-initdb.d/scripts/event-tablas.sql
psql --username "$POSTGRES_USER" --dbname="DB_EVENT_SERVICE" -f /docker-entrypoint-initdb.d/scripts/event-inserts.sql