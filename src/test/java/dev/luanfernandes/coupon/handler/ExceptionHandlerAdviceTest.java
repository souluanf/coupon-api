package dev.luanfernandes.coupon.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.luanfernandes.coupon.config.web.ExceptionHandlerAdvice;
import dev.luanfernandes.coupon.domain.enums.ErrorCode;
import dev.luanfernandes.coupon.domain.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

class ExceptionHandlerAdviceTest {

    private final ExceptionHandlerAdvice advice = new ExceptionHandlerAdvice();

    @Test
    void shouldReturn500WithFallbackMessageWhenExceptionHasNoMessage() {
        ProblemDetail result = advice.handleGenericException(new NullPointerException());
        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getDetail()).isEqualTo("Unexpected error");
    }

    @Test
    void shouldReturn500WithExceptionMessage() {
        ProblemDetail result = advice.handleGenericException(new RuntimeException("something failed"));
        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getDetail()).isEqualTo("something failed");
    }

    @Test
    void shouldReturnStatusAndReasonFromResponseStatusException() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.CONFLICT, "already exists");
        ResponseEntity<ProblemDetail> response = advice.handleResponseStatusException(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).isEqualTo("already exists");
    }

    @Test
    void shouldFallbackToStatusCodeWhenReasonIsNull() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        ResponseEntity<ProblemDetail> response = advice.handleResponseStatusException(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).isEqualTo("503 SERVICE_UNAVAILABLE");
    }

    @Test
    void shouldReturn409WhenDataIntegrityViolationOccurs() {
        ResponseEntity<ProblemDetail> response = advice.handleDataIntegrityViolationException(
                new DataIntegrityViolationException("unique constraint violated"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    void shouldReturn404WhenBusinessExceptionOccurs() {
        BusinessException exception = new BusinessException(ErrorCode.COUPON_NOT_FOUND, "Cupom não encontrado");
        ResponseEntity<ProblemDetail> response = advice.handleBusinessException(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).isEqualTo("Cupom não encontrado");
    }

    @Test
    void shouldReturn400WhenMethodArgumentNotValid() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("obj", "code", "must not be blank")));
        ProblemDetail result = advice.handleMethodArgumentNotValidException(exception);
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getDetail()).isEqualTo("Validation failed for fields");
    }

    @Test
    void shouldReturn400WhenHttpMessageNotReadable() {
        ProblemDetail result = advice.handleHttpMessageNotReadableException(mock(HttpMessageNotReadableException.class));
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getDetail()).isEqualTo("Malformed JSON request");
    }

    @Test
    void shouldReturn400WhenMethodArgumentTypeMismatch() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getValue()).thenReturn("not-a-uuid");
        when(exception.getName()).thenReturn("id");
        when(exception.getRequiredType()).thenReturn((Class) UUID.class);
        ProblemDetail result = advice.handleMethodArgumentTypeMismatchException(exception);
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getDetail()).contains("not-a-uuid", "id", "UUID");
    }

    @Test
    void shouldReturn400WhenMethodArgumentTypeMismatchWithNullType() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getValue()).thenReturn("bad-value");
        when(exception.getName()).thenReturn("param");
        when(exception.getRequiredType()).thenReturn(null);
        ProblemDetail result = advice.handleMethodArgumentTypeMismatchException(exception);
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getDetail()).contains("unknown");
    }

    @Test
    void shouldReturn405WhenMethodNotAllowed() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("PATCH");
        ResponseEntity<ProblemDetail> response = advice.handleHttpRequestMethodNotSupportedException(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldReturn400WhenHandlerMethodValidationFails() {
        HandlerMethodValidationException exception = mock(HandlerMethodValidationException.class);
        ParameterValidationResult validationResult = mock(ParameterValidationResult.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        MessageSourceResolvable error = mock(MessageSourceResolvable.class);

        when(exception.getParameterValidationResults()).thenReturn(List.of(validationResult));
        when(validationResult.getResolvableErrors()).thenReturn(List.of(error));
        when(validationResult.getMethodParameter()).thenReturn(methodParameter);
        when(methodParameter.getParameterName()).thenReturn("size");
        when(error.getDefaultMessage()).thenReturn("must be greater than or equal to 1");

        ProblemDetail result = advice.handleHandlerMethodValidationException(exception);
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getDetail()).isEqualTo("Validation failed for request parameters");
    }

    @Test
    void shouldReturn400WhenConstraintViolationOccurs() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("size");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be greater than or equal to 1");

        ProblemDetail result = advice.handleConstraintViolationException(new ConstraintViolationException(Set.of(violation)));
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getDetail()).isEqualTo("Validation failed for request parameters");
    }
}
