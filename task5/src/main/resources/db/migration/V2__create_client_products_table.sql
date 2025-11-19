CREATE TABLE IF NOT EXISTS products (
    id           BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    balance      NUMERIC(19, 2) NOT NULL DEFAULT 0,
    product_type VARCHAR(20) NOT NULL CHECK (product_type IN ('ACCOUNT', 'CARD')),
    user_id      BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE
);
