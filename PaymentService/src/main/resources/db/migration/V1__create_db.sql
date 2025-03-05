CREATE TABLE notifications
(
    id      UUID NOT NULL,
    message VARCHAR(255),
    user_id UUID,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE TABLE users
(
    id           UUID         NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    first_name   VARCHAR(255) NOT NULL,
    surname      VARCHAR(255) NOT NULL,
    middle_name  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phone_number UNIQUE (phone_number);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);