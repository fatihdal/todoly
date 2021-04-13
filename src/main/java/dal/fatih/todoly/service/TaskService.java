package dal.fatih.todoly.service;

import dal.fatih.todoly.Task;
import dal.fatih.todoly.dto.TaskDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {

    Task handleCreateTask(TaskDTO taskDTO);

    List<TaskDTO> listAllTasks();

    TaskDTO get(Long id);

    boolean delete(Long id);

    List<Task> filterByDueDate(LocalDateTime dueDate);

    List<Task> filterByTitleOrDescription(String keyword);
}
