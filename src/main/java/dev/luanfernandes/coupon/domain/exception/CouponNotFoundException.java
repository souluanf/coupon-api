package dev.luanfernandes.coupon.domain.exception;

import dev.luanfernandes.coupon.domain.enums.ErrorCode;
import java.util.UUID;

public class CouponNotFoundException extends BusinessException {

    public CouponNotFoundException(UUID id) {
        super(ErrorCode.COUPON_NOT_FOUND, "Cupom não encontrado: " + id);
    }
}
