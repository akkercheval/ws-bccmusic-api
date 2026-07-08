package cc.kercheval.bccmusic.ws_bccmusic_api.Exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access denied", ex.getMessage(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", details, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not found", ex.getMessage(), request);
    }

    @ExceptionHandler(AccountValidationException.class)
    public ResponseEntity<ErrorResponse> handleAccountValidation(AccountValidationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", ex.getErrors(), request);
    }

    @ExceptionHandler(CollaborationAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCollaborationAlreadyExists(CollaborationAlreadyExistsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "Collaboration already exists", ex.getMessage(), request);
    }

    @ExceptionHandler(CollaborationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCollaborationNotFound(CollaborationNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Collaboration not found", ex.getMessage(), request);
    }

    @ExceptionHandler(CollaborationValidationException.class)
    public ResponseEntity<ErrorResponse> handleCollaborationValidation(CollaborationValidationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed", ex.getMessage(), request);
    }

    @ExceptionHandler(ScoreValidationException.class)
    public ResponseEntity<ErrorResponse> handleScoreValidation(ScoreValidationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed", ex.getMessage(), request);
    }

    @ExceptionHandler(VendorNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVendorNotFound(VendorNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Vendor not found", ex.getMessage(), request);
    }

    @ExceptionHandler(VendorValidationException.class)
    public ResponseEntity<ErrorResponse> handleVendorValidation(VendorValidationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed", ex.getMessage(), request);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Account not found", ex.getMessage(), request);
    }

    // --- Helper methods ---

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                status.value(), error, message, request.getRequestURI(), LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, List<String> details, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                status.value(), error, details, request.getRequestURI(), LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}
