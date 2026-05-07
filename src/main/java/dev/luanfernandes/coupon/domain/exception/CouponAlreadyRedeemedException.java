package dev.luanfernandes.coupon.domain.exception;

import dev.luanfernandes.coupon.domain.enums.ErrorCode;
import java.util.UUID;

public class CouponAlreadyRedeemedException extends BusinessException {

    public CouponAlreadyRedeemedException(UUID id) {
        super(ErrorCode.COUPON_ALREADY_REDEEMED, "Cupom já foi resgatado: " + id);
    }
}
