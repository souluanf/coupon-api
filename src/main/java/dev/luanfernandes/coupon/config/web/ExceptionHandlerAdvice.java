package dev.luanfernandes.coupon.config.web;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ProblemDetail.forStatusAndDetail;
import static org.springframework.http.ResponseEntity.status;

import dev.luanfernandes.coupon.domain.enums.ErrorCode;
import dev.luanfernandes.coupon.domain.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private static final String TIMESTAMP_PROPERTY = "timestamp";
    private static final String ERROR_CODE_PROPERTY = "errorCode";
    private static final String FIELD_ERRORS_PROPERTY = "fieldErrors";
    private static final String FIELD_ERROR_FORMAT = "%s: %s";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException exception) {
        HttpStatus httpStatus = exception.getHttpStatus();
        ProblemDetail problemDetail = forStatusAndDetail(httpStatus, exception.getMessage());
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        problemDetail.setProperty(ERROR_CODE_PROPERTY, exception.getErrorCode());
        return status(httpStatus).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> format(FIELD_ERROR_FORMAT, error.getField(), error.getDefaultMessage()))
                .toList();
        ProblemDetail problemDetail = forStatusAndDetail(BAD_REQUEST, "Validation failed for fields");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        problemDetail.setProperty(FIELD_ERRORS_PROPERTY, fieldErrors);
        problemDetail.setProperty(ERROR_CODE_PROPERTY, ErrorCode.VALIDATION_ERROR.getCode());
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ignored) {
        return problemDetailFor(BAD_REQUEST, "Malformed JSON request");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String expectedType = Optional.ofNullable(exception.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("unknown");
        String message = format("Invalid value '%s' for parameter '%s': expected %s", exception.getValue(), exception.getName(), expectedType);
        return problemDetailFor(BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception) {
        return status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(problemDetailFor(HttpStatus.METHOD_NOT_ALLOWED, exception.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatusException(ResponseStatusException exception) {
        String reason = Optional.ofNullable(exception.getReason())
                .orElse(exception.getStatusCode().toString());
        ProblemDetail problemDetail = problemDetailFor(exception.getStatusCode(), reason);
        return status(exception.getStatusCode()).body(problemDetail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException ignored) {
        return status(HttpStatus.CONFLICT)
                .body(problemDetailFor(HttpStatus.CONFLICT, "Operação viola restrição de integridade dos dados"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations().stream()
                .map(v -> format(FIELD_ERROR_FORMAT, v.getPropertyPath(), v.getMessage()))
                .toList();
        ProblemDetail problemDetail = forStatusAndDetail(BAD_REQUEST, "Validation failed for request parameters");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        problemDetail.setProperty(FIELD_ERRORS_PROPERTY, errors);
        problemDetail.setProperty(ERROR_CODE_PROPERTY, ErrorCode.VALIDATION_ERROR.getCode());
        return problemDetail;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleHandlerMethodValidationException(HandlerMethodValidationException exception) {
        List<String> errors = exception.getParameterValidationResults().stream()
                .flatMap(r -> r.getResolvableErrors().stream()
                        .map(e -> format(FIELD_ERROR_FORMAT, r.getMethodParameter().getParameterName(), e.getDefaultMessage())))
                .toList();
        ProblemDetail problemDetail = forStatusAndDetail(BAD_REQUEST, "Validation failed for request parameters");
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        problemDetail.setProperty(FIELD_ERRORS_PROPERTY, errors);
        problemDetail.setProperty(ERROR_CODE_PROPERTY, ErrorCode.VALIDATION_ERROR.getCode());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception exception) {
        String message = Optional.ofNullable(exception.getMessage()).orElse("Unexpected error");
        return problemDetailFor(INTERNAL_SERVER_ERROR, message);
    }

    private ProblemDetail problemDetailFor(HttpStatusCode status, String detail) {
        ProblemDetail problemDetail = forStatusAndDetail(status, detail);
        problemDetail.setProperty(TIMESTAMP_PROPERTY, Instant.now());
        return problemDetail;
    }
}
