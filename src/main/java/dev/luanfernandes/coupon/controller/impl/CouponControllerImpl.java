package dev.luanfernandes.coupon.controller.impl;

import static org.springframework.http.HttpStatus.CREATED;

import dev.luanfernandes.coupon.controller.CouponController;
import dev.luanfernandes.coupon.domain.enums.CouponStatus;
import dev.luanfernandes.coupon.dto.request.CouponRequest;
import dev.luanfernandes.coupon.dto.response.CouponResponse;
import dev.luanfernandes.coupon.dto.response.PageResponse;
import dev.luanfernandes.coupon.service.CouponService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponControllerImpl implements CouponController {

    private final CouponService couponService;

    @Override
    public ResponseEntity<CouponResponse> createCoupon(CouponRequest request) {
        return ResponseEntity.status(CREATED).body(couponService.createCoupon(request));
    }

    @Override
    public ResponseEntity<CouponResponse> getCoupon(UUID id) {
        return ResponseEntity.ok(couponService.getCoupon(id));
    }

    @Override
    public ResponseEntity<PageResponse<CouponResponse>> listCoupons(int page, int size, String sort, CouponStatus status) {
        Page<CouponResponse> result = couponService.listCoupons(PageRequest.of(page, size, Sort.by(sort)), status);
        return ResponseEntity.ok(PageResponse.of(
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getContent()));
    }

    @Override
    public ResponseEntity<Void> deleteCoupon(UUID id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CouponResponse> publishCoupon(UUID id) {
        return ResponseEntity.ok(couponService.publishCoupon(id));
    }

    @Override
    public ResponseEntity<CouponResponse> redeemCoupon(UUID id) {
        return ResponseEntity.ok(couponService.redeemCoupon(id));
    }
}
