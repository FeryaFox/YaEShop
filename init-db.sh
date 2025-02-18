#!/bin/bash
set -e

# Создаём дополнительные базы данных
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE yet_another_e_shop;
    CREATE DATABASE yet_another_e_shop_auth;
#    CREATE DATABASE third_database;
EOSQL
