package dev.luanfernandes.coupon.domain.exception;

import dev.luanfernandes.coupon.domain.enums.ErrorCode;
import java.util.UUID;

public class CouponNotActiveException extends BusinessException {

    public CouponNotActiveException(UUID id) {
        super(ErrorCode.COUPON_NOT_ACTIVE, "Cupom não está ativo: " + id);
    }
}
