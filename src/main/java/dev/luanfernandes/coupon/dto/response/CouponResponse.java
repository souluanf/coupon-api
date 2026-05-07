package dev.luanfernandes.coupon.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.luanfernandes.coupon.domain.enums.CouponStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Response com dados do cupom")
public record CouponResponse(
        @JsonProperty("id") String id,
        @JsonProperty("code") String code,
        @JsonProperty("description") String description,
        @JsonProperty("discountValue") BigDecimal discountValue,
        @JsonProperty("expirationDate") LocalDateTime expirationDate,
        @JsonProperty("status") CouponStatus status,
        @JsonProperty("published") boolean published,
        @JsonProperty("redeemed") boolean redeemed
) {}
