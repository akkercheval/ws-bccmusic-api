package cc.kercheval.bccmusic.ws_bccmusic_api.Exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String error,
        String message,
        List<String> details,
        String path,
        LocalDateTime timestamp
) {

    public ErrorResponse(int status, String error, String message, String path, LocalDateTime timestamp) {
        this(status, error, message, null, path, timestamp);
    }

    public ErrorResponse(int status, String error, List<String> details, String path, LocalDateTime timestamp) {
        this(status, error, null, details, path, timestamp);
    }
}
