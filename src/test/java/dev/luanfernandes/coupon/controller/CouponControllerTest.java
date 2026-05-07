package dev.luanfernandes.coupon.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.luanfernandes.coupon.dto.request.CouponRequest;
import dev.luanfernandes.coupon.repository.CouponRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class CouponControllerTest {

    @Autowired
    WebApplicationContext context;

    @Autowired
    CouponRepository couponRepository;

    MockMvc mockMvc;

    final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        couponRepository.deleteAll();
    }

    private CouponRequest validRequest() {
        return new CouponRequest(
                "ABC123",
                "Cupom de desconto especial",
                new BigDecimal("1.0"),
                LocalDateTime.now().plusDays(30),
                true);
    }

    private CouponRequest inactiveRequest() {
        return new CouponRequest(
                "ABC123",
                "Cupom inativo",
                new BigDecimal("1.0"),
                LocalDateTime.now().plusDays(30),
                false);
    }

    @Test
    void shouldCreateCouponAndReturn201() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.code", is("ABC123")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.redeemed", is(false)));
    }

    @Test
    void shouldSanitizeCodeWithSpecialCharsOnCreate() throws Exception {
        CouponRequest request = new CouponRequest(
                "AB-C1@23", "Desconto", new BigDecimal("1.0"), LocalDateTime.now().plusDays(30), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("ABC123")));
    }

    @Test
    void shouldCreateCouponAsPublished() throws Exception {
        CouponRequest request = new CouponRequest(
                "ABC123", "Cupom publicado", new BigDecimal("1.0"), LocalDateTime.now().plusDays(30), true);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.published", is(true)));
    }

    @Test
    void shouldReturn400WhenExpirationDateIsInThePast() throws Exception {
        CouponRequest request = new CouponRequest(
                "ABC123", "Cupom expirado", new BigDecimal("1.0"), LocalDateTime.now().minusDays(1), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDiscountValueBelowMinimum() throws Exception {
        CouponRequest request = new CouponRequest(
                "ABC123", "Cupom inválido", new BigDecimal("0.4"), LocalDateTime.now().plusDays(30), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCodeIsBlank() throws Exception {
        CouponRequest request = new CouponRequest(
                "", "Cupom inválido", new BigDecimal("1.0"), LocalDateTime.now().plusDays(30), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCodeHasFewAlphanumericChars() throws Exception {
        CouponRequest request = new CouponRequest(
                "AB-C", "Cupom inválido", new BigDecimal("1.0"), LocalDateTime.now().plusDays(30), false);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetCouponById() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(get("/coupon/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.code", is("ABC123")));
    }

    @Test
    void shouldReturn404WhenCouponNotFound() throws Exception {
        mockMvc.perform(get("/coupon/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListCoupons() throws Exception {
        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest())));

        mockMvc.perform(get("/coupon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.hasNext", is(false)));
    }

    @Test
    void shouldIndicateHasNextWhenMoreItemsExist() throws Exception {
        CouponRequest second = new CouponRequest(
                "DEF456", "Segundo cupom", new BigDecimal("1.0"), LocalDateTime.now().plusDays(30), false);

        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest())));
        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(second)));

        mockMvc.perform(get("/coupon").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.hasNext", is(true)));
    }

    @Test
    void shouldDeleteCouponAndReturn204() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(delete("/coupon/{id}", id)).andExpect(status().isNoContent());

        mockMvc.perform(get("/coupon/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DELETED")));
    }

    @Test
    void shouldReturn409WhenDeletingAlreadyDeletedCoupon() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(delete("/coupon/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/coupon/{id}", id)).andExpect(status().isConflict());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCoupon() throws Exception {
        mockMvc.perform(delete("/coupon/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Malformed JSON request")));
    }

    @Test
    void shouldReturn400WhenIdIsNotValidUUID() throws Exception {
        mockMvc.perform(get("/coupon/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn405WhenMethodNotAllowed() throws Exception {
        mockMvc.perform(patch("/coupon"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldReturn409WhenCreatingCouponWithDuplicateCode() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturn400WhenPageSizeIsZero() throws Exception {
        mockMvc.perform(get("/coupon").param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldExcludeDeletedCouponsFromDefaultListing() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(delete("/coupon/{id}", id));

        mockMvc.perform(get("/coupon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void shouldFilterCouponsByStatusDeleted() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(delete("/coupon/{id}", id));

        mockMvc.perform(get("/coupon").param("status", "DELETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status", is("DELETED")));
    }

    @Test
    void shouldCreateCouponAsInactiveWhenNotPublished() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inactiveRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("INACTIVE")))
                .andExpect(jsonPath("$.published", is(false)));
    }

    @Test
    void shouldPublishCouponAndReturn200() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inactiveRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(patch("/coupon/{id}/publish", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.published", is(true)));
    }

    @Test
    void shouldReturn409WhenPublishingAlreadyActiveCoupon() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(patch("/coupon/{id}/publish", id)).andExpect(status().isConflict());
    }

    @Test
    void shouldReturn404WhenPublishingNonExistentCoupon() throws Exception {
        mockMvc.perform(patch("/coupon/{id}/publish", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAllowCodeReuseAfterSoftDelete() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(delete("/coupon/{id}", id)).andExpect(status().isNoContent());

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldFilterCouponsByStatusActive() throws Exception {
        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest())));
        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CouponRequest(
                        "DEF456", "Inativo", new BigDecimal("1.0"), LocalDateTime.now().plusDays(30), false))));

        mockMvc.perform(get("/coupon").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status", is("ACTIVE")));
    }

    @Test
    void shouldFilterCouponsByStatusInactive() throws Exception {
        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest())));
        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CouponRequest(
                        "DEF456", "Inativo", new BigDecimal("1.0"), LocalDateTime.now().plusDays(30), false))));

        mockMvc.perform(get("/coupon").param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status", is("INACTIVE")));
    }

    @Test
    void shouldReturn409WhenRedeemingInactiveCoupon() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inactiveRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(patch("/coupon/{id}/redeem", id)).andExpect(status().isConflict());
    }

    @Test
    void shouldReturn409WhenPublishingDeletedCoupon() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inactiveRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(delete("/coupon/{id}", id)).andExpect(status().isNoContent());

        mockMvc.perform(patch("/coupon/{id}/publish", id)).andExpect(status().isConflict());
    }

    @Test
    void shouldRedeemCouponAndReturn200() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(patch("/coupon/{id}/redeem", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.redeemed", is(true)));
    }

    @Test
    void shouldReturn409WhenRedeemingAlreadyRedeemedCoupon() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(patch("/coupon/{id}/redeem", id)).andExpect(status().isOk());
        mockMvc.perform(patch("/coupon/{id}/redeem", id)).andExpect(status().isConflict());
    }

    @Test
    void shouldReturn404WhenRedeemingNonExistentCoupon() throws Exception {
        mockMvc.perform(patch("/coupon/{id}/redeem", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenRedeemingExpiredCoupon() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        couponRepository.findById(UUID.fromString(id)).ifPresent(c -> {
            c.setExpirationDate(LocalDateTime.now().minusDays(1));
            couponRepository.save(c);
        });

        mockMvc.perform(patch("/coupon/{id}/redeem", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenRedeemingDeletedCoupon() throws Exception {
        String body = mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();
        mockMvc.perform(delete("/coupon/{id}", id)).andExpect(status().isNoContent());

        mockMvc.perform(patch("/coupon/{id}/redeem", id)).andExpect(status().isConflict());
    }
}
