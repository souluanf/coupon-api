package dev.luanfernandes.coupon.repository;

import dev.luanfernandes.coupon.domain.entity.Coupon;
import dev.luanfernandes.coupon.domain.enums.CouponStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    boolean existsByCodeAndStatusNot(String code, CouponStatus status);

    Page<Coupon> findByStatus(CouponStatus status, Pageable pageable);

    Page<Coupon> findByStatusNot(CouponStatus status, Pageable pageable);
}
