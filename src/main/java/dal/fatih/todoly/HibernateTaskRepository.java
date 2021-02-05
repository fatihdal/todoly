package dal.fatih.todoly;


import java.sql.Date;
import java.util.List;

public class HibernateTaskRepository implements TaskRepository {
    
    @Override
    public void createTable() {
    }

    @Override
    public boolean create(Task task) {
     return false;
    }

    @Override
    public List<Task> list() {
        return null;
    }

    @Override
    public Task get(String t) {
        return null;
    }

    @Override
    public boolean delete(String t) {
        return false;
    }

    @Override
    public List<Task> filter(Date lastDate) {
        return null;
    }


    @Override
    public List<Task> filterByTitleOrDescription(String keyword) {
        return null;
    }

    @Override
    public void close() {

    }


}
