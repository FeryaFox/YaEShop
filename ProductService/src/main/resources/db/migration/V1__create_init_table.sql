CREATE TABLE images
(
    id            UUID         NOT NULL,
    image_url     TEXT         NOT NULL,
    position      INTEGER      NOT NULL,
    uploaded_user UUID,
    product_id    UUID,
    CONSTRAINT pk_images PRIMARY KEY (id)
);

CREATE TABLE products
(
    id          UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    price       DECIMAL,
    shop_id     UUID,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE shops
(
    id         UUID         NOT NULL,
    user_owner UUID         NOT NULL,
    shop_name  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_shops PRIMARY KEY (id)
);

ALTER TABLE images
    ADD CONSTRAINT FK_IMAGES_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_SHOP FOREIGN KEY (shop_id) REFERENCES shops (id);