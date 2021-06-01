package dal.fatih.todoly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dal.fatih.todoly.dto.TaskDTO;
import dal.fatih.todoly.model.Task;
import dal.fatih.todoly.repo.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = {"spring.profiles.active=test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TodolyTest {

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final ObjectMapper jsonMapper = new ObjectMapper().findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void shouldCreateTask() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().plusDays(10).format(dateTimeFormat));
        TaskDTO taskDto = new TaskDTO("Created-title-of-task", "Created-description-of-task", dueDate);
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<TaskDTO> createTaskResponse = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, TaskDTO.class);

        assertThat(createTaskResponse.getStatusCode(), is(HttpStatus.CREATED));

        Long createdTaskId = Objects.requireNonNull(createTaskResponse.getBody()).getId();
        Task actual = taskRepository.get(createdTaskId);

        assertThat(actual, is(notNullValue()));
        assertThat(
                actual, is(
                        new Task(1L, "Created-title-of-task", "Created-description-of-task", dueDate)
                )
        );
    }

    @Test
    public void shouldAllowEmptyDescription() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().plusDays(10).format(dateTimeFormat));
        TaskDTO taskDto = new TaskDTO("Title of task with empty description", null, dueDate);
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<TaskDTO> createTaskResponse = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, TaskDTO.class);

        assertThat(createTaskResponse.getStatusCode(), is(HttpStatus.CREATED));

        Long createdTaskId = Objects.requireNonNull(createTaskResponse.getBody()).getId();
        Task actual = taskRepository.get(createdTaskId);

        assertThat(actual, is(notNullValue()));
        assertThat(
                actual, is(
                        new Task(1L, "Title of task with empty description", null, dueDate)
                )
        );
    }

    @Test
    public void shouldNotAllowEmptyTitle() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        TaskDTO taskDto = new TaskDTO(null, "Description-of-task", LocalDateTime.now().plusDays(10));
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> createTaskResponse = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        assertThat(createTaskResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        String actual = createTaskResponse.getBody();
        String expected = "Title must not be empty";

        assertThat(taskRepository.list(), is(hasSize(0)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowTitleLessThan5Characters() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        TaskDTO taskDto = new TaskDTO("Titl", "Description-of-task", LocalDateTime.now().plusDays(10));
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> createTaskResponse = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        assertThat(createTaskResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        String actual = createTaskResponse.getBody();
        String expected = "Title length must be between 5 and 120";

        assertThat(taskRepository.list(), is(hasSize(0)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowEmptyDueDate() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        TaskDTO taskDto = new TaskDTO("Title-of-task", "Description-of-task", null);
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> createTaskResponse = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        assertThat(createTaskResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        String actual = createTaskResponse.getBody();
        String expected = "Due Date must not be empty";

        assertThat(taskRepository.list(), is(hasSize(0)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowDueDateOlderThanNow() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        TaskDTO taskDto = new TaskDTO("Title-of-task", "Description-of-task", LocalDateTime.now().plusMinutes(-1));
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> createTaskResponse = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        assertThat(createTaskResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        String actual = createTaskResponse.getBody();
        String expected = "Due date must be a future date";

        assertThat(taskRepository.list(), is(hasSize(0)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowDueDateToBeEqualToNow() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        TaskDTO taskDto = new TaskDTO("Title-of-task", "Description-of-task", LocalDateTime.now());
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> createTaskResponse = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        assertThat(createTaskResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        String actual = createTaskResponse.getBody();
        String expected = "Due date must be a future date";

        assertThat(taskRepository.list(), is(hasSize(0)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldListAllTasks() throws URISyntaxException, JsonProcessingException {
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);
        String title = "Listed-title-of-task", description = "Listed-description-of-task";

        createTask(title + 1, description + 1, dueDate);
        createTask(title + 2, description + 2, dueDate);
        createTask(title + 3, description + 3, dueDate);

        URI urlOfGetAll = new URI(createURLWithPort("/tasks"));
        ResponseEntity<String> listAllTaskResponse =
                this.testRestTemplate.getForEntity(urlOfGetAll, String.class);

        assertThat(listAllTaskResponse.getStatusCode(), is(HttpStatus.OK));

        String response = listAllTaskResponse.getBody();
        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(response, TaskDTO[].class));

        assertThat(actual, is(hasSize(3)));
        assertThat(actual, hasItem(
                new TaskDTO(1L, title + 1, description + 1, dueDate)
        ));
        assertThat(actual, hasItem(
                new TaskDTO(2L, title + 2, description + 2, dueDate)
        ));
        assertThat(actual, hasItem(
                new TaskDTO(1L, title + 3, description + 3, dueDate)
        ));
    }

    @Test
    public void shouldNotFindTaskToList() throws URISyntaxException, JsonProcessingException {
        URI urlOfGetAll = new URI(createURLWithPort("/tasks"));
        ResponseEntity<String> listAllTaskResponse =
                this.testRestTemplate.getForEntity(urlOfGetAll, String.class);

        assertThat(listAllTaskResponse.getStatusCode(), is(HttpStatus.OK));

        String response = listAllTaskResponse.getBody();

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(response, TaskDTO[].class));

        assertThat(actual, is(hasSize(0)));
    }

    @Test
    public void shouldGetTaskById() throws URISyntaxException, JsonProcessingException {
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);
        String title = "Get-by-id-task-title", description = "Get-by-id-task-description";

        createTask(title + 1, description + 1, dueDate);
        createTask(title + 2, description + 2, dueDate);
        createTask(title + 3, description + 3, dueDate);

        URI urlOfGetById = new URI(createURLWithPort("/task/" + 2L));

        ResponseEntity<String> getByIdResponse =
                this.testRestTemplate.getForEntity(urlOfGetById, String.class);

        assertThat(getByIdResponse.getStatusCode(), is(HttpStatus.OK));

        String response = getByIdResponse.getBody();

        TaskDTO taskDto = jsonMapper.readValue(response, TaskDTO.class);

        assertThat(
                taskDto, is(
                        new TaskDTO(2L, title + 2, description + 3, dueDate)
                )
        );
    }

    @Test
    public void shouldNotFindTaskById() throws URISyntaxException {
        URI urlOfGetById = new URI(createURLWithPort("/task/446"));
        ResponseEntity<String> getByIdResponse =
                this.testRestTemplate.getForEntity(urlOfGetById, String.class);

        assertThat(getByIdResponse.getStatusCode(), is(HttpStatus.NOT_FOUND));

        String actual = getByIdResponse.getBody();
        String expected = "Task not found with id = 446";

        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowValueOtherThanNumberWhenGetTaskById() throws URISyntaxException {
        URI urlOfGetById = new URI(createURLWithPort("/task/A446"));
        ResponseEntity<String> getByIdResponse =
                this.testRestTemplate.getForEntity(urlOfGetById, String.class);

        assertThat(getByIdResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        String actual = getByIdResponse.getBody();
        String expected = "Failed to convert value of type of id";

        assertThat(taskRepository.list(), is(hasSize(0)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldDeleteTaskById() throws URISyntaxException {
        String title = "Delete-by-id-task-title", description = "Delete-by-id-task-description";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);

        createTask(title + 1, description + 1, dueDate);
        createTask(title + 2, description + 2, dueDate);
        createTask(title + 3, description + 3, dueDate);

        URI urlOfDelete = new URI(createURLWithPort("/task/2"));
        ResponseEntity<String> deleteResponse = this.testRestTemplate.exchange(
                urlOfDelete, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertThat(deleteResponse.getStatusCode(), is(HttpStatus.OK));

        List<Task> afterRemoveTask = taskRepository.list();

        String expected = "Deleted task with id : 2";

        assertThat(afterRemoveTask, is(hasSize(2)));
        assertThat(deleteResponse.getBody(), is(expected));
    }

    @Test
    public void shouldFindNoTaskToDelete() throws URISyntaxException {
        URI urlOfDelete = new URI(createURLWithPort("/task/446"));
        ResponseEntity<String> deleteResponse = this.testRestTemplate.exchange(
                urlOfDelete, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertThat(deleteResponse.getStatusCode(), is(HttpStatus.NOT_FOUND));

        String actual = deleteResponse.getBody();
        String expected = "Task not found with id = 446";

        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowValueOtherThanNumberWhenDeleteTaskById() throws URISyntaxException {
        URI urlOfDelete = new URI(createURLWithPort("/task/A446"));
        ResponseEntity<String> deleteResponse = this.testRestTemplate.exchange(
                urlOfDelete, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        assertThat(deleteResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        String actual = deleteResponse.getBody();
        String expected = "Failed to convert value of type of id";

        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldFilterTasksByTitle() throws URISyntaxException, JsonProcessingException {
        String title = "Filter-by-title", noFilterTitle = "No-fil-ter-by-title";
        String description = "Description-of-task", noFilterDesc = "No-fil-ter-by-desc";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10), noFilterDueDate = LocalDateTime.now().plusMonths(5);

        for (int i = 1; i <= 3; i++) {
            createTask(title, description, dueDate);
            createTask(noFilterTitle, noFilterDesc, noFilterDueDate);
        }

        URI urlOfFilterByTitle = new URI(createURLWithPort("/tasks/titleordesc?keyword=Filt"));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByTitle, String.class);

        assertThat(filterResponse.getStatusCode(), is(HttpStatus.OK));

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(filterResponse.getBody(), TaskDTO[].class));

        assertThat(actual, is(hasSize(3)));
        assertThat(actual, hasItem(
                new TaskDTO(1L, title, description, dueDate)
        ));
        assertThat(actual, hasItem(
                new TaskDTO(3L, title, description, dueDate)
        ));
        assertThat(actual, is(not(
                hasItem(
                        new TaskDTO(4L, noFilterTitle, noFilterDesc, noFilterDueDate)
                )
        )));
    }

    @Test
    public void shouldIgnoreCaseWhenFilteringTasksByTitle() throws URISyntaxException, JsonProcessingException {
        String title = "FiLTER-t-i-T-l-E-ignore-CASE", noFilterTitle = "No-fil-ter-by-title";
        String description = "Description-of-task ", noFilterDesc = "No-fil-ter-by-desc";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10), noFilterDueDate = LocalDateTime.now().plusMonths(5);

        for (int i = 1; i <= 3; i++) {
            createTask(title, description, dueDate);
            createTask(noFilterTitle, noFilterDesc, noFilterDueDate);
        }

        URI urlOfFilterByTitle = new URI(createURLWithPort("/tasks/titleordesc?keyword=" + title.toLowerCase(Locale.ENGLISH)));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByTitle, String.class);

        assertThat(filterResponse.getStatusCode(), is(HttpStatus.OK));

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(filterResponse.getBody(), TaskDTO[].class));

        assertThat(actual, is(hasSize(3)));
        assertThat(actual, hasItem(
                new TaskDTO(1L, title, description, dueDate)
        ));
        assertThat(actual, hasItem(
                new TaskDTO(3L, title, description, dueDate)
        ));
        assertThat(actual, is(not(
                hasItem(
                        new TaskDTO(4L, noFilterTitle, noFilterDesc, noFilterDueDate)
                )
        )));
    }

    @Test
    public void shouldFilterTasksByDescription() throws URISyntaxException, JsonProcessingException {
        String title = "Title-of-Task", noFilterTitle = "No-fil-ter-by-title";
        String description = "Filter-by-description", noFilterDesc = "No-fil-ter-by-desc";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10), noFilterDueDate = LocalDateTime.now().plusMonths(5);

        for (int i = 1; i <= 3; i++) {
            createTask(title, description, dueDate);
            createTask(noFilterTitle, noFilterDesc, noFilterDueDate);
        }

        URI urlOfFilterByDesc = new URI(createURLWithPort("/tasks/titleordesc?keyword=Filt"));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDesc, String.class);

        assertThat(filterResponse.getStatusCode(), is(HttpStatus.OK));

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(filterResponse.getBody(), TaskDTO[].class));

        assertThat(actual, is(hasSize(3)));
        assertThat(actual, hasItem(
                new TaskDTO(1L, title, description, dueDate)
        ));
        assertThat(actual, hasItem(
                new TaskDTO(3L, title, description, dueDate)
        ));
        assertThat(actual, is(not(
                hasItem(
                        new TaskDTO(4L, noFilterTitle, noFilterDesc, noFilterDueDate)
                )
        )));
    }

    @Test
    public void shouldIgnoreCaseWhenFilteringTasksByDescription() throws URISyntaxException, JsonProcessingException {
        String title = "Title-of-Task", noFilterTitle = "No-fil-ter-by-title";
        String description = "FiLTER-d-e-scriPTioN-ignore-CASE", noFilterDesc = "No-fil-ter-by-desc";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10), noFilterDueDate = LocalDateTime.now().plusMonths(5);

        for (int i = 1; i <= 3; i++) {
            createTask(title, description, dueDate);
            createTask(noFilterTitle, noFilterDesc, noFilterDueDate);
        }

        URI urlOfFilterByDesc = new URI(createURLWithPort("/tasks/titleordesc?keyword=" + description.toLowerCase(Locale.ENGLISH)));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDesc, String.class);

        assertThat(filterResponse.getStatusCode(), is(HttpStatus.OK));

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(filterResponse.getBody(), TaskDTO[].class));

        assertThat(actual, is(hasSize(3)));
        assertThat(actual, hasItem(
                new TaskDTO(1L, title, description, dueDate)
        ));
        assertThat(actual, hasItem(
                new TaskDTO(3L, title, description, dueDate)
        ));
        assertThat(actual, is(not(
                hasItem(
                        new TaskDTO(4L, noFilterTitle, noFilterDesc, noFilterDueDate)
                )
        )));
    }

    @Test
    public void shouldFindNoTaskWhenFilterByTitleOrDescription() throws URISyntaxException, JsonProcessingException {
        URI urlOfFilterByTitle = new URI(createURLWithPort("/tasks/titleordesc?keyword=Unavailable-title-or-description"));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByTitle, String.class);

        assertThat(filterResponse.getStatusCode(), is(HttpStatus.OK));

        String response = filterResponse.getBody();

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(response, TaskDTO[].class));

        assertThat(actual, is(hasSize(0)));
    }

    @Test
    public void shouldFilterByDueDate() throws URISyntaxException, JsonProcessingException {
        String title = "Filter-by-due-date-title", noFilterTitle = "No-filter-by-title";
        String description = "Filter-by-due-date-description", noFilterDesc = "No-filter-by-description";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10), noFilterDueDate = dueDate.plusMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        for (int i = 1; i <= 3; i++) {
            createTask(title, description, dueDate);
            createTask(noFilterTitle, noFilterDesc, noFilterDueDate);
        }

        URI urlOfFilterByDueDate = new URI(createURLWithPort("/tasks/duedate?duedate=" + dueDate.plusMinutes(4).format(formatter)));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDueDate, String.class);

        assertThat(filterResponse.getStatusCode(), is(HttpStatus.OK));

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(filterResponse.getBody(), TaskDTO[].class));

        assertThat(actual, is(hasSize(3)));
        assertThat(actual, hasItem(
                new TaskDTO(1L, title, description, dueDate)
        ));
        assertThat(actual, hasItem(
                new TaskDTO(3L, title, description, dueDate)
        ));
        assertThat(actual, is(not(
                hasItem(
                        new TaskDTO(4L, noFilterTitle, noFilterDesc, noFilterDueDate)
                )
        )));
    }

    @Test
    public void shouldFindNoTaskToFilterByDueDate() throws URISyntaxException, JsonProcessingException {
        LocalDateTime dueDate = LocalDateTime.now().plusSeconds(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        URI urlOfFilterByDueDate = new URI(createURLWithPort("/tasks/duedate?duedate=" + dueDate.plusMinutes(4).format(formatter)));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDueDate, String.class);

        assertThat(filterResponse.getStatusCode(), is(HttpStatus.OK));

        String response = filterResponse.getBody();

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(response, TaskDTO[].class));

        assertThat(actual, is(hasSize(0)));
    }

    @Test
    public void shouldNotAllowIncorrectDateFormatWhenFilterByDueDate() throws URISyntaxException {
        URI urlOfFilterByDueDate = new URI(createURLWithPort("/tasks/duedate?duedate=2090-05-05"));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDueDate, String.class);

        String actual = filterResponse.getBody();
        String expected = "Check date format";

        assertThat(filterResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowEmptyDueDateWhenFilterByDueDate() throws URISyntaxException {
        URI urlOfFilterByDueDate = new URI(createURLWithPort("/tasks/duedate?duedate="));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDueDate, String.class);

        String actual = filterResponse.getBody();
        String expected = "Check date format";

        assertThat(filterResponse.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    public String createTask(String title, String description, LocalDateTime dueDate) throws URISyntaxException {
        TaskDTO taskDto = new TaskDTO(title, description, dueDate);
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);
        ResponseEntity<String> responseEntity = this.testRestTemplate.postForEntity(taskCreateUrl, request, String.class);

        return responseEntity.getBody();
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + randomServerPort + "/todoly" + uri;
    }
}