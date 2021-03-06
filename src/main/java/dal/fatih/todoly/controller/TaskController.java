package dal.fatih.todoly.controller;

import dal.fatih.todoly.dto.CreateTaskResponse;
import dal.fatih.todoly.dto.TaskDTO;
import dal.fatih.todoly.exception.RecordNotFoundException;
import dal.fatih.todoly.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/todoly")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CreateTaskResponse createTaskResponse;

    @RequestMapping(method = RequestMethod.POST, value = "/task")
    public ResponseEntity<CreateTaskResponse> create(@Valid @RequestBody TaskDTO taskDTO) {

        createTaskResponse.setId(taskService.create(taskDTO).getId());

        return new ResponseEntity<CreateTaskResponse>(createTaskResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.listAll();
        return ResponseEntity.ok(tasks);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/task/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable("id") Long id) throws RecordNotFoundException {
        try {
            return ResponseEntity.ok().body(taskService.get(id));
        } catch (EmptyResultDataAccessException ex) {
            throw new RecordNotFoundException("id = " + id);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/task/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable("id") Long id) {
        try {
            taskService.delete(id);
            return ResponseEntity.ok("Deleted task with id : "+id);
        } catch (EmptyResultDataAccessException ex) {
            throw new RecordNotFoundException("id = " + id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/tasks/duedate")
    public ResponseEntity<List<TaskDTO>> filterByDueDate(@RequestParam("duedate")
                                                                 String dueDate) {

        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateTime = dateTimeFormat.parse(dueDate, LocalDateTime::from);

        List<TaskDTO> tasks = taskService.filterByDueDate(dateTime);
        return ResponseEntity.ok().body(tasks);
    }

    @RequestMapping(method = RequestMethod.GET, value = "tasks/titleordesc")
    public ResponseEntity<List<TaskDTO>> filterByTitleOrDescription(@RequestParam("keyword") String keyword) {
        List<TaskDTO> tasks = taskService.filterByTitleOrDescription(keyword);

        return ResponseEntity.ok().body(tasks);
    }
}