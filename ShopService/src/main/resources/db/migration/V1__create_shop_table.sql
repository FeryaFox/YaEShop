CREATE TABLE shops
(
    id               UUID         NOT NULL,
    user_owner       UUID         NOT NULL,
    shop_name        VARCHAR(255) NOT NULL,
    shop_description TEXT,
    shop_image       TEXT,
    CONSTRAINT pk_shop PRIMARY KEY (id)
);