package dev.luanfernandes.coupon.domain;

import dev.luanfernandes.coupon.domain.enums.ErrorCode;
import dev.luanfernandes.coupon.domain.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CouponDomain {

    private final String code;
    private final String description;
    private final BigDecimal discountValue;
    private final LocalDateTime expirationDate;
    private final boolean published;

    private CouponDomain(String code, String description, BigDecimal discountValue,
                         LocalDateTime expirationDate, boolean published) {
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = published;
    }

    public static CouponDomain of(String rawCode, String description, BigDecimal discountValue,
                                  LocalDateTime expirationDate, boolean published) {
        String code = sanitizeCode(rawCode);
        validateDiscountValue(discountValue);
        validateExpirationDate(expirationDate);
        return new CouponDomain(code, description, discountValue, expirationDate, published);
    }

    private static String sanitizeCode(String rawCode) {
        if (rawCode == null) {
            throw new BusinessException(ErrorCode.INVALID_COUPON_CODE, "Código do cupom é obrigatório");
        }
        String alphanumeric = rawCode.replaceAll("[^a-zA-Z0-9]", "");
        if (alphanumeric.length() < 6) {
            throw new BusinessException(ErrorCode.INVALID_COUPON_CODE,
                    "Código deve ter ao menos 6 caracteres alfanuméricos após remoção de caracteres especiais");
        }
        return alphanumeric.substring(0, 6).toUpperCase();
    }

    private static void validateDiscountValue(BigDecimal value) {
        if (value == null || value.compareTo(new BigDecimal("0.5")) < 0) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE, "Valor de desconto mínimo é 0.5");
        }
    }

    private static void validateExpirationDate(LocalDateTime date) {
        if (date == null || !date.isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_EXPIRATION_DATE,
                    "Data de expiração não pode estar no passado");
        }
    }
}
