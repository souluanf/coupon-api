package dev.luanfernandes.coupon.domain.exception;

import dev.luanfernandes.coupon.domain.enums.ErrorCode;
import java.util.UUID;

public class CouponAlreadyActiveException extends BusinessException {

    public CouponAlreadyActiveException(UUID id) {
        super(ErrorCode.COUPON_ALREADY_ACTIVE, "Cupom já está ativo: " + id);
    }
}
