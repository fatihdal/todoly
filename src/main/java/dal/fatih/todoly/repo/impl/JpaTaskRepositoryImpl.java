package dal.fatih.todoly.repo.impl;


import dal.fatih.todoly.model.Task;
import dal.fatih.todoly.repo.TaskRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Repository("taskRepository")
public class JpaTaskRepositoryImpl implements TaskRepository {

    @PersistenceContext
    EntityManager entityManager;

    private CriteriaQuery<Task> getBaseQuery(CriteriaBuilder cb) {
        return cb.createQuery(Task.class);
    }

    @Override
    public Task create(Task task) {
        entityManager.persist(task);

        return task;
    }

    @Override
    public List<Task> list() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cq = getBaseQuery(cb);
        Root<Task> taskRoot = cq.from(Task.class);
        cq.select(taskRoot);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public Task get(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cq = getBaseQuery(cb);
        Root<Task> taskRoot = cq.from(Task.class);
        cq.where(cb.equal(taskRoot.get("id"), id));

        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public boolean delete(String taskId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cq = getBaseQuery(cb);
        Root<Task> taskRoot = cq.from(Task.class);
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cq = getBaseQuery(cb);
        Root<Task> taskRoot = cq.from(Task.class);
        cq.where(cb.between(taskRoot.get("dueDate"),
                java.util.Date.from(Instant.now()), lastDate));
        cq.select(taskRoot);
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Task> filterByTitleOrDescription(String keyword) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cq = getBaseQuery(cb);
        Root<Task> taskRoot = cq.from(Task.class);
        EntityType<Task> type = entityManager.getMetamodel().entity(Task.class);
        cq.where(cb.or(cb.like(cb.lower(taskRoot.get(type.getDeclaredSingularAttribute
                        ("title", String.class))),
                '%' + keyword.toLowerCase(Locale.ROOT) + '%')

                , cb.like(cb.lower(taskRoot.get(type.getDeclaredSingularAttribute
                                ("description", String.class))),
                        '%' + keyword.toLowerCase(Locale.ROOT) + '%')));

        cq.select(taskRoot);
        return entityManager.createQuery(cq).getResultList();
    }
}