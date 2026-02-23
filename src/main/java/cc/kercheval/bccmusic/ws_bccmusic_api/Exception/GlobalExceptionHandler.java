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
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body("Validation failed: " + ex.getMessage());
    }    

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
    @ExceptionHandler(AccountValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            AccountValidationException ex,
            HttpServletRequest request) {

        ValidationErrorResponse response = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            ex.getErrors(),
            request.getRequestURI(),
            LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(response);
    }
	
	@ExceptionHandler(CollaborationAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCollaborationAlreadyExists(
            CollaborationAlreadyExistsException ex, 
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Collaboration already exists",
            ex.getMessage(),
            request.getRequestURI(),
            LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
	
	@ExceptionHandler(CollaborationNotFoundException.class)
	public ResponseEntity<String> handleCollaborationNotFoundException(CollaborationNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	@ExceptionHandler(CollaborationValidationException.class)
	public ResponseEntity<String> handleCollaborationValidationException(CollaborationValidationException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(ScoreValidationException.class)
	public ResponseEntity<String> handleScoreValidationException(CollaborationValidationException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(VendorNotFoundException.class)
	public ResponseEntity<String> handleVendorNotFoundException(CollaborationNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
	
	@ExceptionHandler(VendorValidationException.class)
	public ResponseEntity<String> handleVendorValidationException(CollaborationValidationException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	public record ErrorResponse(
	        int status,
	        String error,
	        String message,
	        String path,
	        LocalDateTime timestamp
	    ) {}
	
    public record ValidationErrorResponse(
        int status,
        String error,
        List<String> details,
        String path,
        LocalDateTime timestamp
    ) {}
}