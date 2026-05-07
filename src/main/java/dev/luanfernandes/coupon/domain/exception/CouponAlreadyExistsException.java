package dev.luanfernandes.coupon.domain.exception;

import dev.luanfernandes.coupon.domain.enums.ErrorCode;

public class CouponAlreadyExistsException extends BusinessException {

    public CouponAlreadyExistsException(String code) {
        super(ErrorCode.COUPON_ALREADY_EXISTS, "Já existe um cupom ativo com o código: " + code);
    }
}
