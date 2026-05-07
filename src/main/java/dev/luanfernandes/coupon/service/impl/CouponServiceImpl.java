package dev.luanfernandes.coupon.service.impl;

import dev.luanfernandes.coupon.domain.CouponDomain;
import dev.luanfernandes.coupon.domain.entity.Coupon;
import dev.luanfernandes.coupon.domain.enums.CouponStatus;
import dev.luanfernandes.coupon.domain.exception.CouponAlreadyActiveException;
import dev.luanfernandes.coupon.domain.exception.CouponAlreadyDeletedException;
import dev.luanfernandes.coupon.domain.exception.CouponAlreadyExistsException;
import dev.luanfernandes.coupon.domain.exception.CouponAlreadyRedeemedException;
import dev.luanfernandes.coupon.domain.exception.CouponExpiredException;
import dev.luanfernandes.coupon.domain.exception.CouponNotActiveException;
import dev.luanfernandes.coupon.domain.exception.CouponNotFoundException;
import dev.luanfernandes.coupon.dto.mapper.CouponMapper;
import dev.luanfernandes.coupon.dto.request.CouponRequest;
import dev.luanfernandes.coupon.dto.response.CouponResponse;
import dev.luanfernandes.coupon.repository.CouponRepository;
import dev.luanfernandes.coupon.service.CouponService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository repository;
    private final CouponMapper mapper;

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        CouponDomain domain = CouponDomain.of(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published());
        if (repository.existsByCodeAndStatusNot(domain.getCode(), CouponStatus.DELETED)) {
            throw new CouponAlreadyExistsException(domain.getCode());
        }
        Coupon coupon = mapper.toEntity(domain);
        coupon.setStatus(domain.isPublished() ? CouponStatus.ACTIVE : CouponStatus.INACTIVE);
        coupon.setRedeemed(false);
        return mapper.toResponse(repository.save(coupon));
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCoupon(UUID id) {
        return mapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponResponse> listCoupons(Pageable pageable, CouponStatus status) {
        Page<Coupon> page = status != null
                ? repository.findByStatus(status, pageable)
                : repository.findByStatusNot(CouponStatus.DELETED, pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteCoupon(UUID id) {
        Coupon coupon = findById(id);
        if (CouponStatus.DELETED.equals(coupon.getStatus())) {
            throw new CouponAlreadyDeletedException(id);
        }
        coupon.setStatus(CouponStatus.DELETED);
        coupon.setDeletedAt(LocalDateTime.now());
        repository.save(coupon);
    }

    @Override
    @Transactional
    public CouponResponse publishCoupon(UUID id) {
        Coupon coupon = findById(id);
        if (CouponStatus.DELETED.equals(coupon.getStatus())) {
            throw new CouponAlreadyDeletedException(id);
        }
        if (CouponStatus.ACTIVE.equals(coupon.getStatus())) {
            throw new CouponAlreadyActiveException(id);
        }
        coupon.setStatus(CouponStatus.ACTIVE);
        coupon.setPublished(true);
        return mapper.toResponse(repository.save(coupon));
    }

    @Override
    @Transactional
    public CouponResponse redeemCoupon(UUID id) {
        Coupon coupon = findById(id);
        if (!CouponStatus.ACTIVE.equals(coupon.getStatus())) {
            throw new CouponNotActiveException(id);
        }
        if (coupon.isRedeemed()) {
            throw new CouponAlreadyRedeemedException(id);
        }
        if (coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new CouponExpiredException(id);
        }
        coupon.setRedeemed(true);
        return mapper.toResponse(repository.save(coupon));
    }

    private Coupon findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new CouponNotFoundException(id));
    }
}
