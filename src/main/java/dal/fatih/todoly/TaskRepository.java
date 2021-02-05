package dal.fatih.todoly;

import java.sql.Date;
import java.util.List;

public interface TaskRepository {
    void createTable();

    boolean create(Task task);

    List<Task> list();

    Task get(String taskId);

    boolean delete(String taskId);

    List<Task> filter(Date lastDate);

    List<Task> filterByTitleOrDescription(String keyword);

    void close();
}