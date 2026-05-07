package dev.luanfernandes.coupon.domain.exception;

import dev.luanfernandes.coupon.domain.enums.ErrorCode;
import java.util.UUID;

public class CouponAlreadyDeletedException extends BusinessException {

    public CouponAlreadyDeletedException(UUID id) {
        super(ErrorCode.COUPON_ALREADY_DELETED, "Cupom já foi deletado: " + id);
    }
}
