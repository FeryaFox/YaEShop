CREATE TABLE payments
(
    id             UUID         NOT NULL,
    order_id       VARCHAR(255) NOT NULL,
    user_id        UUID         NOT NULL,
    total_price    DECIMAL      NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);