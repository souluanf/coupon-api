CREATE UNIQUE INDEX uq_coupon_code_active ON coupon(code) WHERE status <> 'DELETED';
