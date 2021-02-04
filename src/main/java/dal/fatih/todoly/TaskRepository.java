package dal.fatih.todoly;

import java.util.List;

public interface TaskRepository {
    void createTable();

    boolean create(Task t);

    List<Task> list();

    Task get(String t);

    boolean delete(String t);


}
