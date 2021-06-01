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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    @PersistenceContext
    private EntityManager entityManager;

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
    public Task delete(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cq = getBaseQuery(cb);
        Root<Task> taskRoot = cq.from(Task.class);

        cq.where(cb.equal(taskRoot.get("id"), id));

        Task task = entityManager.createQuery(cq).getSingleResult();
        entityManager.remove(task);

        return task;
    }

    @Override
    public List<Task> filterByDueDate(LocalDateTime dueDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> cq = getBaseQuery(cb);
        Root<Task> taskRoot = cq.from(Task.class);
        cq.where(cb.between(taskRoot.get("dueDate"),
                LocalDateTime.now(), dueDate));

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
                '%' + keyword.toLowerCase(Locale.ENGLISH) + '%')

                , cb.like(cb.lower(taskRoot.get(type.getDeclaredSingularAttribute
                                ("description", String.class))),
                        '%' + keyword.toLowerCase(Locale.ENGLISH) + '%')));

        cq.select(taskRoot);

        return entityManager.createQuery(cq).getResultList();
    }
}