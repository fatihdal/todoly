package dal.fatih.todoly;

import java.io.Closeable;
import java.sql.Date;
import java.util.List;

public interface TaskRepository extends Closeable {

    Task create(Task task);

    List<Task> list();

    Task get(Long taskId);

    boolean delete(String taskId);

    List<Task> filterByDueDate(Date lastDate);

    List<Task> filterByTitleOrDescription(String keyword);
}