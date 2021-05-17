package dal.fatih.todoly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.*;

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
    public void shouldCreateTask() throws URISyntaxException, JsonProcessingException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().plusDays(10).format(dateTimeFormat));
        TaskDTO taskDto = new TaskDTO("Created-title-of-task", "Created-description-of-task", dueDate);
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<TaskDTO> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, TaskDTO.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.CREATED));

        Long createdTaskId = Objects.requireNonNull(responseEntity.getBody()).getId();
        Task actual = taskRepository.get(createdTaskId);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(1L));
        assertThat(actual.getTitle(), is("Created-title-of-task"));
        assertThat(actual.getDescription(), is("Created-description-of-task"));
        assertThat(actual.getDueDate(), equalTo(taskDto.getDueDate()));
    }

    @Test
    public void shouldAllowEmptyDescription() throws URISyntaxException, JsonProcessingException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().plusDays(10).format(dateTimeFormat));
        TaskDTO taskDto = new TaskDTO("Title of task with empty description", null, dueDate);
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<TaskDTO> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, TaskDTO.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.CREATED));

        Long createdTaskId = Objects.requireNonNull(responseEntity.getBody()).getId();
        Task actual = taskRepository.get(createdTaskId);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getId(), is(1L));
        assertThat(actual.getTitle(), is("Title of task with empty description"));
        assertThat(actual.getDescription(), is(nullValue()));
        assertThat(actual.getDueDate(), equalTo(taskDto.getDueDate()));
    }

    @Test
    public void shouldNotAllowEmptyTitle() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        TaskDTO taskDto = new TaskDTO(null, "Description-of-task", LocalDateTime.now().plusDays(10));
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));

        String actual = responseEntity.getBody();
        String expected = "Title must not be empty";

        assertThat(taskRepository.list(), hasSize(0));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowTitleLessThan5Characters() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        TaskDTO taskDto = new TaskDTO("Titl", "Description-of-task", LocalDateTime.now().plusDays(10));
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));

        String actual = responseEntity.getBody();
        String expected = "Title length must be between 5 and 120";

        assertThat(taskRepository.list(), hasSize(0));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowEmptyDueDate() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        TaskDTO taskDto = new TaskDTO("Title-of-task", "Description-of-task", null);
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));

        String actual = responseEntity.getBody();
        String expected = "Due Date must not be empty";

        assertThat(taskRepository.list(), hasSize(0));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowDueDateOlderThanNow() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        TaskDTO taskDto = new TaskDTO("Title-of-task", "Description-of-task", LocalDateTime.now().plusMinutes(-1));
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));

        String actual = responseEntity.getBody();
        String expected = "Due date must be a future date";

        assertThat(taskRepository.list(), hasSize(0));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowDueDateToBeEqualToNow() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        TaskDTO taskDto = new TaskDTO("Title-of-task", "Description-of-task", LocalDateTime.now());
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));

        String actual = responseEntity.getBody();
        String expected = "Due date must be a future date";

        assertThat(taskRepository.list(), hasSize(0));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
   // @Ignore
    public void shouldListAllTasks() throws URISyntaxException, JsonProcessingException {
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);
        String title = "Listed-title-of-task", description = "Listed-description-of-task";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        for (int i = 1; i <= 3; i++) {
            createTask(title + i, description + i, dueDate);
        }
        URI urlOfGetAll = new URI(createURLWithPort("/tasks"));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(urlOfGetAll, String.class);

        String response = responseEntity.getBody();

        List<Map<String, String>> actual = jsonMapper.readValue(response, new TypeReference<List<Map<String, String>>>() {
        });

        List<Map<String, String>> expected = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("id", String.valueOf(i));
            map.put("title", title + i);
            map.put("description", description + i);
            map.put("dueDate", dueDate.format(formatter));
            expected.add(i - 1, map);
        }

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldNotFindTaskToList() throws URISyntaxException, JsonProcessingException {
        URI urlOfGetAll = new URI(createURLWithPort("/tasks"));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(urlOfGetAll, String.class);

        String response = responseEntity.getBody();

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(response, TaskDTO[].class));

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(hasSize(0)));
    }

    @Test
    public void shouldGetTaskById() throws URISyntaxException, JsonProcessingException {
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);
        String title = "Get-by-id-task-title", description = "Get-by-id-task-description";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String createdTaskResponse = "";
        for (int i = 1; i <= 3; i++) {
            createdTaskResponse = createTask(title + i, description + i, dueDate);
        }


        Map<String, Long> createResponseMap = jsonMapper.readValue(createdTaskResponse, new TypeReference<Map<String, Long>>() {
        });
        long idOfTaskToGet = createResponseMap.get("id");

        URI urlOfGetById = new URI(createURLWithPort("/task/" + idOfTaskToGet));

        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(urlOfGetById, String.class);

        String response = responseEntity.getBody();

        Map<String, String> actual = jsonMapper.readValue(response, new TypeReference<Map<String, String>>() {
        });

        Map<String, String> expected = new HashMap<>();
        expected.put("id", String.valueOf(idOfTaskToGet));
        expected.put("title", title + "3");
        expected.put("description", description + "3");
        expected.put("dueDate", dueDate.format(formatter));

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void shouldNotFindTaskById() throws URISyntaxException {
        long unavailableTaskId = 446L;
        URI urlOfGetById = new URI(createURLWithPort("/task/" + unavailableTaskId + ""));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(urlOfGetById, String.class);

        String actual = responseEntity.getBody();
        String expected = "Task not found with id = " + unavailableTaskId;

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowValueOtherThanNumberWhenGetTaskById() throws URISyntaxException {
        String unsupportedCharacterId = "446L";
        URI urlOfGetById = new URI(createURLWithPort("/task/" + unsupportedCharacterId));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(urlOfGetById, String.class);

        String actual = responseEntity.getBody();
        String expected = "Failed to convert value of type of id";

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldDeleteTaskById() throws URISyntaxException, JsonProcessingException {
        String title = "Delete-by-id-task-title", description = "Delete-by-id-task-description";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);
        //Created three tasks
        String createdTaskResponse = "";
        for (int i = 1; i <= 3; i++) {
            createdTaskResponse = createTask(title, description, dueDate);
        }

        Map<String, Long> responseMap = jsonMapper.readValue(createdTaskResponse, new TypeReference<Map<String, Long>>() {
        });
        long idOfTaskToDelete = responseMap.get("id");

        URI urlOfDelete = new URI(createURLWithPort("/task/" + idOfTaskToDelete + ""));
        ResponseEntity<String> deletedResponse = this.testRestTemplate.exchange(
                urlOfDelete, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        String expected = "{\"id\":" + idOfTaskToDelete + "}";

        assertThat(createdTaskResponse, is(equalTo(expected)));
        assertThat(deletedResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
    }

    @Test
    public void shouldFindNoTaskToDelete() throws URISyntaxException {
        long unavailableTaskId = 446L;
        URI urlOfDelete = new URI(createURLWithPort("/task/" + unavailableTaskId));
        ResponseEntity<String> deletedResponse = this.testRestTemplate.exchange(
                urlOfDelete, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        String actual = deletedResponse.getBody();
        String expected = "Task not found with id = " + unavailableTaskId;

        assertThat(deletedResponse.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowValueOtherThanNumberWhenDeleteTaskById() throws URISyntaxException {
        String unsupportedCharacterId = "446L";
        URI urlOfDelete = new URI(createURLWithPort("/task/" + unsupportedCharacterId));
        ResponseEntity<String> deletedResponse = this.testRestTemplate.exchange(
                urlOfDelete, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        String actual = deletedResponse.getBody();
        String expected = "Failed to convert value of type of id";

        assertThat(deletedResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldFilterTasksByTitle() throws URISyntaxException {
        String title = "Filter-by-title", noFilterTitle = "No-fil-ter-by-title";
        String description = "Description-of-task", noFilterDesc = "No-fil-ter-by-desc";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10), noFilterDueDate = LocalDateTime.now().plusMonths(5);
        //Created six tasks

        for (int i = 1; i <= 3; i++) {
            createTask(title, description, dueDate);
            createTask(noFilterTitle, noFilterDesc, noFilterDueDate);
        }

        URI urlOfFilterByTitle = new URI(createURLWithPort("/tasks/titleordesc?keyword=Filt"));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByTitle, String.class);

        String actual = filterResponse.getBody();

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(containsString("Filter-by-title")));
        assertThat(actual, not(containsString("No-fil-ter-by-title")));
    }

    @Test
    public void shouldIgnoreCaseWhenFilteringTasksByTitle() throws URISyntaxException {
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

        String actual = filterResponse.getBody();

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(containsString(title)));
        assertThat(actual, not(containsString("Not-fil-ter-by-title")));
    }

    @Test
    public void shouldFilterTasksByDescription() throws URISyntaxException {
        String title = "Title-of-Task", noFilterTitle = "No-fil-ter-by-title";
        String description = "Filter-by-description", noFilterDesc = "No-fil-ter-by-desc";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10), noFilterDueDate = LocalDateTime.now().plusMonths(5);
        //Created six tasks
        for (int i = 1; i <= 3; i++) {
            createTask(title, description, dueDate);
            createTask(noFilterTitle, noFilterDesc, noFilterDueDate);
        }

        URI urlOfFilterByDesc = new URI(createURLWithPort("/tasks/titleordesc?keyword=Filt"));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDesc, String.class);

        String actual = filterResponse.getBody();

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(containsString("Filter-by-description")));
        assertThat(actual, not(containsString("Not-fil-ter-by-title")));
    }

    @Test
    public void shouldIgnoreCaseWhenFilteringTasksByDescription() throws URISyntaxException {
        String title = "Title-of-Task", noFilterTitle = "No-fil-ter-by-title";
        String description = "FiLTER-d-e-scriPTioN-ignore-CASE", noFilterDesc = "No-fil-ter-by-desc";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10), noFilterDueDate = LocalDateTime.now().plusMonths(5);
        //Created six tasks
        for (int i = 1; i <= 3; i++) {
            createTask(title, description, dueDate);
            createTask(noFilterTitle, noFilterDesc, noFilterDueDate);
        }

        URI urlOfFilterByDesc = new URI(createURLWithPort("/tasks/titleordesc?keyword=" + description.toLowerCase(Locale.ENGLISH)));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDesc, String.class);

        String actual = filterResponse.getBody();

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(containsString(description)));
        assertThat(actual, not(containsString("No-fil-ter-by-desc")));
    }

    @Test
    public void shouldFindNoTaskWhenFilterByTitleOrDescription() throws URISyntaxException, JsonProcessingException {
        URI urlOfFilterByTitle = new URI(createURLWithPort("/tasks/titleordesc?keyword=Unavailable-title-or-description"));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(urlOfFilterByTitle, String.class);

        String response = responseEntity.getBody();

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(response, TaskDTO[].class));

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(hasSize(0)));
    }

    @Test
    public void shouldFilterByDueDate() throws URISyntaxException {
        String title = "Filter-by-due-date-title", noFilterTitle = "No-filter-by-title";
        String description = "Filter-by-due-date-description", noFilterDesc = "No-filter-by-description";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10), noFilterDueDate = dueDate.plusMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        //Created six tasks
        for (int i = 1; i <= 3; i++) {
            createTask(title, description, dueDate);
            createTask(noFilterTitle, noFilterDesc, noFilterDueDate);
        }

        URI urlOfFilterByDueDate = new URI(createURLWithPort("/tasks/duedate?duedate=" + dueDate.plusMinutes(4).format(formatter)));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDueDate, String.class);

        String actual = filterResponse.getBody();

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(containsString(description)));
        assertThat(actual, not(containsString("No-filter-by-description")));
    }

    @Test
    public void shouldFindNoTaskToFilterByDueDate() throws URISyntaxException, JsonProcessingException {
        LocalDateTime dueDate = LocalDateTime.now().plusSeconds(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        URI urlOfFilterByDueDate = new URI(createURLWithPort("/tasks/duedate?duedate=" + dueDate.plusMinutes(4).format(formatter)));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(urlOfFilterByDueDate, String.class);

        String response = responseEntity.getBody();

        List<TaskDTO> actual = Arrays.asList(jsonMapper.readValue(response, TaskDTO[].class));

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(hasSize(0)));
    }

    @Test
    public void shouldNotAllowIncorrectDateFormatWhenFilterByDueDate() throws URISyntaxException {
        URI urlOfFilterByDueDate = new URI(createURLWithPort("/tasks/duedate?duedate=2090-05-05"));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDueDate, String.class);

        String actual = filterResponse.getBody();
        String expected = "Check date format";

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowEmptyDueDateWhenFilterByDueDate() throws URISyntaxException {
        URI urlOfFilterByDueDate = new URI(createURLWithPort("/tasks/duedate?duedate="));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDueDate, String.class);

        String actual = filterResponse.getBody();
        String expected = "Check date format";

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
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