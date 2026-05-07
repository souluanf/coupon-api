package dev.luanfernandes.coupon.domain.exception;

import dev.luanfernandes.coupon.domain.enums.ErrorCode;
import java.util.UUID;

public class CouponExpiredException extends BusinessException {

    public CouponExpiredException(UUID id) {
        super(ErrorCode.COUPON_EXPIRED, "Cupom expirado: " + id);
    }
}
