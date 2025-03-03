SELECT 'CREATE DATABASE yet_another_e_shop_auth'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'yet_another_e_shop_auth')\gexec

SELECT 'CREATE DATABASE yet_another_e_shop_shop'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'yet_another_e_shop_shop')\gexec

SELECT 'CREATE DATABASE yet_another_e_shop_product'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'yet_another_e_shop_product')\gexec

SELECT 'CREATE DATABASE yet_another_e_shop_order'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'yet_another_e_shop_order')\gexec

SELECT 'CREATE DATABASE yet_another_e_shop_notification'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'yet_another_e_shop_notification')\gexec

SELECT 'CREATE DATABASE yet_another_e_shop_payment'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'yet_another_e_shop_payment')\gexec
