package dev.luanfernandes.coupon.service;

import dev.luanfernandes.coupon.domain.enums.CouponStatus;
import dev.luanfernandes.coupon.dto.request.CouponRequest;
import dev.luanfernandes.coupon.dto.response.CouponResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {

    CouponResponse createCoupon(CouponRequest request);

    CouponResponse getCoupon(UUID id);

    Page<CouponResponse> listCoupons(Pageable pageable, CouponStatus status);

    void deleteCoupon(UUID id);

    CouponResponse publishCoupon(UUID id);

    CouponResponse redeemCoupon(UUID id);
}
