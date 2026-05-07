package dev.luanfernandes.coupon.controller;

import static dev.luanfernandes.coupon.constants.PathConstants.COUPON;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dev.luanfernandes.coupon.domain.enums.CouponStatus;
import dev.luanfernandes.coupon.dto.request.CouponRequest;
import dev.luanfernandes.coupon.dto.response.CouponResponse;
import dev.luanfernandes.coupon.dto.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@Tag(name = "Coupon", description = "Endpoints para gerenciamento de cupons de desconto")
@RequestMapping(value = COUPON, produces = APPLICATION_JSON_VALUE)
public interface CouponController {

    @Operation(summary = "Criar cupom")
    @ApiResponse(responseCode = "201")
    @PostMapping
    ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request);

    @Operation(summary = "Buscar cupom por ID")
    @GetMapping("/{id}")
    ResponseEntity<CouponResponse> getCoupon(@PathVariable UUID id);

    @Operation(summary = "Listar cupons")
    @GetMapping
    ResponseEntity<PageResponse<CouponResponse>> listCoupons(
            @Parameter(description = "Número da página (0-indexed)", example = "0")
                    @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
                    @Min(1) @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Ordenação", example = "createdAt")
                    @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "Filtrar por status (omitir exclui DELETED)")
                    @RequestParam(required = false) CouponStatus status);

    @Operation(summary = "Deletar cupom (soft delete)")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCoupon(@PathVariable UUID id);

    @Operation(summary = "Publicar cupom")
    @PatchMapping("/{id}/publish")
    ResponseEntity<CouponResponse> publishCoupon(@PathVariable UUID id);

    @Operation(summary = "Resgatar cupom")
    @PatchMapping("/{id}/redeem")
    ResponseEntity<CouponResponse> redeemCoupon(@PathVariable UUID id);
}
