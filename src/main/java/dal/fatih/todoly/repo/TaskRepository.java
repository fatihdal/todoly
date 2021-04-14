package dal.fatih.todoly.repo;

import dal.fatih.todoly.model.Task;

import java.sql.Date;
import java.util.List;

public interface TaskRepository {

    Task create(Task task);

    List<Task> list();

    Task get(Long taskId);

    boolean delete(String taskId);

    List<Task> filterByDueDate(Date lastDate);

    List<Task> filterByTitleOrDescription(String keyword);
}