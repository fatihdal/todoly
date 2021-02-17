package dal.fatih.todoly;


import javax.persistence.*;
import java.sql.Date;
import java.util.*;

public class HibernateTaskRepository implements TaskRepository {

    private final EntityManagerFactory entityManagerFactory
            = Persistence.createEntityManagerFactory("Todoly");
    private final EntityManager entityManager
            = entityManagerFactory.createEntityManager();

    @Override
    public boolean create(Task task) {
        entityManager.getTransaction().begin();
        entityManager.persist(task);
        entityManager.getTransaction().commit();

        return true;
    }

    @Override
    public List<Task> list() {
        final TypedQuery<Task> listQuery
                = entityManager.createQuery("select a from Task a ", Task.class);
        return listQuery.getResultList();
    }

    @Override
    public Task get(String taskId) {
        final TypedQuery<Task> getQuery
                = entityManager.createQuery("select a from Task a " +
                "where a.taskId ='" + taskId + "'", Task.class);
        return getQuery.getSingleResult();
    }

    @Override
    public boolean delete(String taskId) {
        final Query deleteQuery
                = entityManager.createQuery("delete from Task " +
                "where taskId ='" + taskId + "'");
        int rows;
        try {
            entityManager.getTransaction().begin();
            rows = deleteQuery.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            entityManager.getTransaction().commit();
        }
        return rows > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Task> filterByDueDate(Date lastDate) {
        final Query filterQuery
                = entityManager.createNativeQuery("select * from Task where dueDate  " +
                "between now() and '" + lastDate + "'", Task.class);
        return filterQuery.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Task> filterByTitleOrDescription(String keyword) {
        final Query getByTitleOrDesQuery
                = entityManager.createNativeQuery("select * from Task " +
                "where title like '" + "%" + keyword.toLowerCase() + "%" + "'" +
                "or description like '" + "%" + keyword.toLowerCase() + "%" + "' ", Task.class);
        return getByTitleOrDesQuery.getResultList();
    }

    @Override
    public void close() {
        entityManagerFactory.close();
    }
}