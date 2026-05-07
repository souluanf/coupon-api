package dev.luanfernandes.coupon.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.luanfernandes.coupon.domain.exception.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class CouponDomainTest {

    private static final LocalDateTime FUTURE = LocalDateTime.now().plusDays(30);

    @Test
    void shouldSanitizeSpecialCharsFromCode() {
        CouponDomain domain = CouponDomain.of("ABC-123", "desc", BigDecimal.ONE, FUTURE, false);
        assertThat(domain.getCode()).isEqualTo("ABC123");
    }

    @Test
    void shouldTruncateCodeToSixChars() {
        CouponDomain domain = CouponDomain.of("ABCDEFGH", "desc", BigDecimal.ONE, FUTURE, false);
        assertThat(domain.getCode()).hasSize(6).isEqualTo("ABCDEF");
    }

    @Test
    void shouldUppercaseCode() {
        CouponDomain domain = CouponDomain.of("abc123", "desc", BigDecimal.ONE, FUTURE, false);
        assertThat(domain.getCode()).isEqualTo("ABC123");
    }

    @Test
    void shouldThrowWhenCodeHasLessThanSixAlphanumericChars() {
        assertThatThrownBy(() -> CouponDomain.of("AB-1", "desc", BigDecimal.ONE, FUTURE, false))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("6 caracteres alfanuméricos");
    }

    @Test
    void shouldThrowWhenCodeIsNull() {
        assertThatThrownBy(() -> CouponDomain.of(null, "desc", BigDecimal.ONE, FUTURE, false))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("obrigatório");
    }

    @Test
    void shouldThrowWhenDiscountValueBelowMinimum() {
        BigDecimal belowMinimum = new BigDecimal("0.4");
        assertThatThrownBy(() -> CouponDomain.of("ABC123", "desc", belowMinimum, FUTURE, false))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("0.5");
    }

    @Test
    void shouldThrowWhenDiscountValueIsNull() {
        assertThatThrownBy(() -> CouponDomain.of("ABC123", "desc", null, FUTURE, false))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("0.5");
    }

    @Test
    void shouldAcceptMinimumDiscountValue() {
        CouponDomain domain = CouponDomain.of("ABC123", "desc", new BigDecimal("0.5"), FUTURE, false);
        assertThat(domain.getDiscountValue()).isEqualByComparingTo("0.5");
    }

    @Test
    void shouldThrowWhenExpirationDateIsInThePast() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        assertThatThrownBy(() -> CouponDomain.of("ABC123", "desc", BigDecimal.ONE, pastDate, false))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("passado");
    }

    @Test
    void shouldThrowWhenExpirationDateIsNull() {
        assertThatThrownBy(() -> CouponDomain.of("ABC123", "desc", BigDecimal.ONE, null, false))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("passado");
    }

    @Test
    void shouldCreateDomainAsPublished() {
        CouponDomain domain = CouponDomain.of("ABC123", "desc", BigDecimal.ONE, FUTURE, true);
        assertThat(domain.isPublished()).isTrue();
    }

    @Test
    void shouldCreateDomainWithAllValidFields() {
        CouponDomain domain = CouponDomain.of("ABC-123", "Desconto especial", new BigDecimal("1.5"), FUTURE, false);
        assertThat(domain.getCode()).isEqualTo("ABC123");
        assertThat(domain.getDescription()).isEqualTo("Desconto especial");
        assertThat(domain.getDiscountValue()).isEqualByComparingTo("1.5");
        assertThat(domain.getExpirationDate()).isEqualTo(FUTURE);
        assertThat(domain.isPublished()).isFalse();
    }
}
