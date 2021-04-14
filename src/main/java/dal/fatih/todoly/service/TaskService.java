package dal.fatih.todoly.service;

import dal.fatih.todoly.dto.TaskDTO;
import dal.fatih.todoly.exception.RecordNotFoundException;
import dal.fatih.todoly.model.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {

    Task handleCreateTask(TaskDTO taskDTO);

    List<TaskDTO> listAllTasks();

    TaskDTO get(Long id) throws RecordNotFoundException;

    boolean delete(Long id);

    List<Task> filterByDueDate(LocalDateTime dueDate);

    List<Task> filterByTitleOrDescription(String keyword);
}
