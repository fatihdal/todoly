package dal.fatih.todoly.controller;

import dal.fatih.todoly.dto.TaskDTO;
import dal.fatih.todoly.exception.RecordNotFoundException;
import dal.fatih.todoly.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/todoly")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @RequestMapping(method = RequestMethod.POST, value = "/task")
    public ResponseEntity<URI> create(@Valid @RequestBody TaskDTO taskDTO) {
        taskService.handleCreateTask(taskDTO);
        Long id = taskDTO.getId();
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(id).toUri();
        return new ResponseEntity<URI>(location, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.listAllTasks();
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
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException ex) {
            throw new RecordNotFoundException("id = " + id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/filter")
    public ResponseEntity<List<TaskDTO>> filterByTitleOrDescription(@RequestParam("keyword") String keyword) {
        List<TaskDTO> tasks = taskService.filterByTitleOrDescription(keyword.toLowerCase(Locale.ROOT));

        return ResponseEntity.ok().body(tasks);
    }
}
