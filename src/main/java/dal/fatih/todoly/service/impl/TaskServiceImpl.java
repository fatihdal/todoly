package dal.fatih.todoly.service.impl;

import dal.fatih.todoly.dto.TaskDTO;
import dal.fatih.todoly.exception.RecordNotFoundException;
import dal.fatih.todoly.model.Task;
import dal.fatih.todoly.repo.TaskRepository;
import dal.fatih.todoly.service.TaskService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TaskRepository taskRepository;

    @Autowired
    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task handleCreateTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());

        final Task taskDb = taskRepository.create(task);

        taskDTO.setId(taskDb.getId());
        logger.info(taskDTO.toString());

        return taskDb;
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        final ModelMapper modelMapper = new ModelMapper();
        List<Task> allTasks = taskRepository.list();
        List<TaskDTO> taskDTOs = new ArrayList<>();
        allTasks.forEach(task ->
                taskDTOs.add(modelMapper.map(task, TaskDTO.class))
        );

        return taskDTOs;
    }

    @Override
    public TaskDTO get(Long id) throws RecordNotFoundException {

        Task foundTask = taskRepository.get(id);
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(foundTask.getId());
        taskDTO.setTitle(foundTask.getTitle());
        taskDTO.setDescription(foundTask.getDescription());
        taskDTO.setDueDate(foundTask.getDueDate());
        return taskDTO;
    }

    @Override
    public void delete(Long id) {
        taskRepository.delete(id);
    }

    @Override
    public List<TaskDTO> filterByDueDate(LocalDateTime dueDate) {
        return null;
    }

    @Override
    public List<TaskDTO> filterByTitleOrDescription(String keyword) {
        return null;
    }
}