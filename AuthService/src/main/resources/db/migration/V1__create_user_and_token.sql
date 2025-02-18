CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       phone_number VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255) NOT NULL,
                       surname VARCHAR(255) NOT NULL,
                       middle_name VARCHAR(255) NOT NULL,
                       create_at timestamp default CURRENT_TIMESTAMP,
                       refresh_token_id BIGINT,
                       is_enabled BOOLEAN DEFAULT TRUE,
                       is_account_non_expired BOOLEAN DEFAULT TRUE,
                       is_account_non_locked BOOLEAN DEFAULT TRUE,
                       is_credentials_non_expired BOOLEAN DEFAULT TRUE
);

CREATE TABLE refresh_token (
                               id SERIAL PRIMARY KEY,
                               token TEXT NOT NULL UNIQUE,
                               user_id UUID NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL CHECK (name IN ('BUYER', 'SELLER', 'ADMIN', 'DISTRIBUTION_POINT_EMPLOYEE'))
);

CREATE TABLE user_roles (
                            user_id UUID NOT NULL,
                            role_id INT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE buyers (
    id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    address TEXT,
    date_of_birth DATE
);

CREATE TABLE sellers (
    id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    shop_name VARCHAR(255) NOT NULL,
    business_license VARCHAR(255)
);

ALTER TABLE refresh_token
    ADD CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users (id);

INSERT INTO roles (name) VALUES
                             ('BUYER'),
                             ('SELLER'),
                             ('ADMIN'),
                             ('DISTRIBUTION_POINT_EMPLOYEE');
