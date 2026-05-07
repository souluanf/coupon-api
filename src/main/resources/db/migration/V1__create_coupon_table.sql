CREATE TABLE coupon (
    id UUID PRIMARY KEY,
    code VARCHAR(6) NOT NULL,
    description VARCHAR(500) NOT NULL,
    discount_value DECIMAL(15, 2) NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    published BOOLEAN NOT NULL DEFAULT FALSE,
    redeemed BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
