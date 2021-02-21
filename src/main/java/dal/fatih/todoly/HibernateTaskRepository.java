package dal.fatih.todoly;


import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import java.sql.Date;
import java.time.*;
import java.util.*;

public class HibernateTaskRepository implements TaskRepository {

    private final EntityManagerFactory entityManagerFactory
            = Persistence.createEntityManagerFactory("Todoly");
    private final EntityManager entityManager = entityManagerFactory.createEntityManager();
    private final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    private final CriteriaQuery<Task> cq = cb.createQuery(Task.class);
    private final Root<Task> taskRoot = cq.from(Task.class);

    @Override
    public boolean create(Task task) {
        EntityTransaction et = null;
        try {
            et = entityManager.getTransaction();
            et.begin();
            entityManager.persist(task);
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
        TypedQuery<Task> query = entityManager.createQuery(cq);
        cq.select(taskRoot);

        return query.getResultList();
    }

    @Override
    public Task get(String taskId) {
        cq.where(cb.equal(taskRoot.get("taskId"), taskId));

        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public boolean delete(String taskId) {
        try {
            cq.where(cb.equal(taskRoot.get("taskId"), taskId));
            Task task = entityManager.createQuery(cq).getSingleResult();

            entityManager.getTransaction().begin();
            entityManager.remove(task);
            entityManager.getTransaction().commit();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Task> filterByDueDate(Date lastDate) {
        cq.where(cb.between(taskRoot.get("dueDate"),
                java.util.Date.from(Instant.now()), lastDate));
        cq.select(taskRoot);
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Task> filterByTitleOrDescription(String keyword) {
        final EntityType<Task> type = entityManager.getMetamodel().entity(Task.class);
        cq.where(cb.or(cb.like(cb.lower(taskRoot.get(type.getDeclaredSingularAttribute
                        ("title", String.class))),
                '%' + keyword.toLowerCase(Locale.ROOT) + '%')

                , cb.like(cb.lower(taskRoot.get(type.getDeclaredSingularAttribute
                                ("description", String.class))),
                        '%' + keyword.toLowerCase(Locale.ROOT) + '%')));

        cq.select(taskRoot);
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public void close() {
        entityManagerFactory.close();
    }
}