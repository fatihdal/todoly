package dal.fatih.todoly.repo;

import dal.fatih.todoly.model.Task;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository {

    Task create(Task task);

    List<Task> list();

    Task get(Long taskId);

    void delete(Long taskId);

    List<Task> filterByDueDate(LocalDateTime lastDate);

    List<Task> filterByTitleOrDescription(String keyword);
}