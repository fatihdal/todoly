package dal.fatih.todoly.dto;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

public class CreateTaskResponse {
    Long id;

    public CreateTaskResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CreateTaskResponse{" +
                "id='" + id + '\'' +
                '}';
    }
}
