package dal.fatih.todoly.service.impl;

import dal.fatih.todoly.Task;
import dal.fatih.todoly.dto.TaskDTO;
import dal.fatih.todoly.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Override
    public TaskDTO handleCreateTask(TaskDTO taskDto) {
        return null;
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        return null;
    }

    @Override
    public TaskDTO get(Long id) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public List<Task> filterByDueDate(LocalDateTime dueDate) {
        return null;
    }

    @Override
    public List<Task> filterByTitleOrDescription(String keyword) {
        return null;
    }
}