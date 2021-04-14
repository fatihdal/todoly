package dal.fatih.todoly.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecordNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -9079454849611061074L;
    private final String id;

    public RecordNotFoundException(String id) {
        super(String.format("Task not found with %s ", id));
        this.id = id;
    }

    public RecordNotFoundException(Throwable cause, String id) {
        super(cause);
        this.id = id;
    }
}