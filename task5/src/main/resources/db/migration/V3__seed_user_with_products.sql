WITH new_user AS (
    INSERT INTO users (username)
    VALUES ('client_demo')
    RETURNING id
), target_user AS (
    SELECT id FROM new_user
)
INSERT INTO products (account_number, balance, product_type, user_id)
SELECT p.account_number,
       p.balance,
       p.product_type,
       tu.id
FROM target_user tu
CROSS JOIN (VALUES
    ('ACC-1001', 1500.00, 'ACCOUNT'),
    ('ACC-1002', 2500.50, 'ACCOUNT'),
    ('CARD-2001', 500.00, 'CARD'),
    ('CARD-2002', 750.25, 'CARD'),
    ('ACC-1003', 3200.75, 'ACCOUNT')
) AS p(account_number, balance, product_type);
