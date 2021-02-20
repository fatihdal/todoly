package dal.fatih.todoly;


import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.util.*;

public class HibernateTaskRepository implements TaskRepository {
    
    private final EntityManagerFactory emf
            = Persistence.createEntityManagerFactory("Todoly");
    private final EntityManager em = emf.createEntityManager();
    private final CriteriaBuilder cb = em.getCriteriaBuilder();
    private final CriteriaQuery<Task> cq = cb.createQuery(Task.class);
    private final Root<Task> taskRoot = cq.from(Task.class);

    @Override
    public boolean create(Task task) {
        EntityTransaction et = null;
        try {
            et = em.getTransaction();
            et.begin();
            em.persist(task);
            et.commit();

            return true;
        } catch (Exception e) {
            if (et != null) {
                et.rollback();
                return false;
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Task> list() {
        TypedQuery<Task> query = em.createQuery(cq);
        cq.select(taskRoot);

        return query.getResultList();
    }

    @Override
    public Task get(String taskId) {
        final TypedQuery<Task> getQuery
                = em.createQuery("select a from Task a " +
                "where a.taskId ='" + taskId + "'", Task.class);
        return getQuery.getSingleResult();
    }

    @Override
    public boolean delete(String taskId) {
        final Query deleteQuery
                = em.createQuery("delete from Task " +
                "where taskId ='" + taskId + "'");
        int rows;
        try {
            em.getTransaction().begin();
            rows = deleteQuery.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            em.getTransaction().commit();
        }
        return rows > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Task> filterByDueDate(Date lastDate) {
        final Query filterQuery
                = em.createNativeQuery("select * from Task where dueDate  " +
                "between now() and '" + lastDate + "'", Task.class);
        return filterQuery.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Task> filterByTitleOrDescription(String keyword) {
        final Query getByTitleOrDesQuery
                = em.createNativeQuery("select * from Task " +
                "where title like '" + "%" + keyword.toLowerCase() + "%" + "'" +
                "or description like '" + "%" + keyword.toLowerCase() + "%" + "' ", Task.class);
        return getByTitleOrDesQuery.getResultList();
    }

    @Override
    public void close() {
        emf.close();
    }
}