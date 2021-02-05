package dal.fatih.todoly;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.Date;
import java.util.List;

public class HibernateTaskRepository implements TaskRepository {

    Session session = new
            Configuration().configure().buildSessionFactory().openSession();
    Transaction transaction = session.getTransaction();

    public HibernateTaskRepository() {

    }

    @Override
    public void createTable() {
    }

    @Override
    public boolean create(Task task) {
        transaction = session.getTransaction();
        transaction.begin();
        task.setTaskId(task.getTaskId());
        task.setTitle(task.getTitle());
        task.setDescription(task.getDescription());
        task.setDueDate(task.getDueDate());
        session.saveOrUpdate(task);
        transaction.commit();
        return true;
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