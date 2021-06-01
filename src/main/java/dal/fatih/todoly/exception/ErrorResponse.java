package dal.fatih.todoly.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ErrorResponse {

    private final HttpStatus status;
    private final String message;
    private final List<String> details;

    public ErrorResponse(HttpStatus status, String message, List<String> details) {
        super();
        this.status = status;
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }

    public HttpStatus getStatus() {
        return status;
    }
}