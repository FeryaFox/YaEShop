CREATE TABLE refresh_token (
                               id SERIAL PRIMARY KEY,
                               token TEXT NOT NULL,
                               user_id uuid UNIQUE
);

CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255) NOT NULL,
                       surname VARCHAR(255) NOT NULL,
                       middle_name VARCHAR(255) NOT NULL,
                       roles VARCHAR(255) DEFAULT 'ROLE_USER',
                       refresh_token_id BIGINT,
                       is_enabled BOOLEAN DEFAULT TRUE,
                       is_account_non_expired BOOLEAN DEFAULT TRUE,
                       is_account_non_locked BOOLEAN DEFAULT TRUE,
                       is_credentials_non_expired BOOLEAN DEFAULT TRUE,
                       FOREIGN KEY (refresh_token_id) REFERENCES refresh_token (id)
);

ALTER TABLE refresh_token
    ADD CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users (id);
