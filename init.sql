SELECT 'CREATE DATABASE yet_another_e_shop_auth'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'yet_another_e_shop_auth')\gexec

SELECT 'CREATE DATABASE yet_another_e_shop_shop'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'yet_another_e_shop_shop')\gexec

SELECT 'CREATE DATABASE yet_another_e_shop_product'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'yet_another_e_shop_product')\gexec