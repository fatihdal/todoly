package dal.fatih.todoly;


import javax.persistence.*;
import java.sql.Date;
import java.util.*;

public class HibernateTaskRepository implements TaskRepository {

    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Tasks");
    private final EntityManager entityManager = entityManagerFactory.createEntityManager();
    private final TypedQuery<Task> listQuery = entityManager.createQuery("select a from Task a ", Task.class);
    private final TypedQuery<Task> getQuery = entityManager.createQuery("select a from Task a where a.taskId = ?1", Task.class);
    private final Query deleteQuery = entityManager.createQuery("delete from Task where taskId = ?1");
    private final Query filterQuery = entityManager.createNativeQuery("select * from Tasks where duedate  between now() and ?1", Task.class);
    
    public HibernateTaskRepository() {
    }

    @Override
    public void createTable() {
    }

    @Override
    public boolean create(Task task) {
        entityManager.getTransaction().begin();
        entityManager.persist(task);
        entityManager.getTransaction().commit();

        return true;
    }

    @Override
    public List<Task> list() {
        return listQuery.getResultList();
    }

    @Override
    public Task get(String taskId) {
        Task task = getQuery.setParameter(1, UUID.fromString(taskId)).getSingleResult();
        return task;
    }

    @Override
    public boolean delete(String taskId) {
        int rows = 0;
        try {
            entityManager.getTransaction().begin();
            deleteQuery.setParameter(1, UUID.fromString(taskId));
            rows = deleteQuery.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            entityManager.getTransaction().commit();
            return rows > 0;
        }
    }

    @Override
    public List<Task> filter(Date lastDate) {
        filterQuery.setParameter(1, lastDate);
        return filterQuery.getResultList();
    }

    @Override
    public List<Task> filterByTitleOrDescription(String keyword) {
        return null;
    }

    @Override
    public void close() {
        entityManagerFactory.close();
    }
}