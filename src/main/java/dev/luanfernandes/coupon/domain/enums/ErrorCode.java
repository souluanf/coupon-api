package dev.luanfernandes.coupon.domain.enums;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    COUPON_NOT_FOUND("COUPON_NOT_FOUND", NOT_FOUND),
    COUPON_ALREADY_DELETED("COUPON_ALREADY_DELETED", CONFLICT),
    COUPON_ALREADY_EXISTS("COUPON_ALREADY_EXISTS", CONFLICT),
    COUPON_ALREADY_REDEEMED("COUPON_ALREADY_REDEEMED", CONFLICT),
    COUPON_EXPIRED("COUPON_EXPIRED", BAD_REQUEST),
    COUPON_NOT_ACTIVE("COUPON_NOT_ACTIVE", CONFLICT),
    COUPON_ALREADY_ACTIVE("COUPON_ALREADY_ACTIVE", CONFLICT),
    INVALID_COUPON_CODE("INVALID_COUPON_CODE", BAD_REQUEST),
    INVALID_DISCOUNT_VALUE("INVALID_DISCOUNT_VALUE", BAD_REQUEST),
    INVALID_EXPIRATION_DATE("INVALID_EXPIRATION_DATE", BAD_REQUEST),
    VALIDATION_ERROR("VALIDATION_ERROR", BAD_REQUEST);

    private final String code;
    private final HttpStatus httpStatus;

    ErrorCode(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

}
