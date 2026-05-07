package dev.luanfernandes.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Request para criar cupom")
public record CouponRequest(
        @Schema(description = "Código do cupom (alfanumérico, 6 chars; especiais são removidos)", example = "ABC-123")
        @NotBlank(message = "Código é obrigatório")
        String code,

        @Schema(description = "Descrição do cupom", example = "Cupom de 20% de desconto")
        @NotBlank(message = "Descrição é obrigatória")
        String description,

        @Schema(description = "Valor de desconto (mínimo 0.5)", example = "0.8")
        @NotNull(message = "Valor de desconto é obrigatório")
        @DecimalMin(value = "0.5", message = "Valor de desconto mínimo é 0.5")
        BigDecimal discountValue,

        @Schema(description = "Data de expiração (não pode ser no passado)", example = "2026-12-31T23:59:59")
        @NotNull(message = "Data de expiração é obrigatória")
        LocalDateTime expirationDate,

        @Schema(description = "Se o cupom já é publicado ao criar", defaultValue = "false")
        boolean published
) {}
