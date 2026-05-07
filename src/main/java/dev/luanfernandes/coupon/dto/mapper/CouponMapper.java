package dev.luanfernandes.coupon.dto.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import dev.luanfernandes.coupon.domain.CouponDomain;
import dev.luanfernandes.coupon.domain.entity.Coupon;
import dev.luanfernandes.coupon.dto.response.CouponResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface CouponMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "redeemed", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Coupon toEntity(CouponDomain domain);

    @Mapping(target = "id", expression = "java(coupon.getId().toString())")
    CouponResponse toResponse(Coupon coupon);
}
