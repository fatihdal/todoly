package dal.fatih.todoly;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dal.fatih.todoly.dto.TaskDTO;
import dal.fatih.todoly.model.Task;
import dal.fatih.todoly.repo.TaskRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = "spring.profiles.active=test")
@FixMethodOrder(MethodSorters.JVM)
public class TodolyTest {

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final ObjectMapper jsonMapper = new ObjectMapper();

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
        TaskDTO taskDto = new TaskDTO("Created-title-of-task", "Created-description-of-task"
                , dueDate);

        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        String response = responseEntity.getBody();

        Map<String, Long> map = jsonMapper.readValue(response, new TypeReference<Map<String, Long>>() {
        });
        long createdTaskId = map.get("id");

        Task actual = taskRepository.get(createdTaskId);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getTitle(), equalTo(taskDto.getTitle()));
        assertThat(actual.getDescription(), equalTo(taskDto.getDescription()));
        assertThat(actual.getDueDate(), equalTo(taskDto.getDueDate()));
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    @Test
    public void shouldAllowEmptyDescription() throws URISyntaxException, JsonProcessingException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        LocalDateTime dueDate = LocalDateTime.parse(LocalDateTime.now().plusDays(10).format(dateTimeFormat));
        TaskDTO taskDto = new TaskDTO("Title of task with empty description", null
                , dueDate);
        HttpEntity<TaskDTO> request = new HttpEntity<>(taskDto);

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        String response = responseEntity.getBody();

        Map<String, Long> map = jsonMapper.readValue(response, new TypeReference<Map<String, Long>>() {
        });
        long createdTaskId = map.get("id");

        Task actual = taskRepository.get(createdTaskId);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getTitle(), equalTo(taskDto.getTitle()));
        assertThat(actual.getDescription(), equalTo(taskDto.getDescription()));
        assertThat(actual.getDueDate(), equalTo(taskDto.getDueDate()));
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void shouldNotAllowEmptyTitle() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        HttpEntity<TaskDTO> request = new HttpEntity<>(
                new TaskDTO(null, "Description-of-task"
                        , LocalDateTime.now().plusDays(10))
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        String actual = responseEntity.getBody();
        String expected = "Title must not be empty";

        assertThat(taskRepository.list().size(), is(equalTo(0)));
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void shouldNotAllowTitleLessThan5Characters() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        HttpEntity<TaskDTO> request = new HttpEntity<>(
                new TaskDTO("Titl", "Description-of-task"
                        , LocalDateTime.now().plusDays(10))
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        String actual = responseEntity.getBody();
        String expected = "Title length must be between 5 and 120";

        assertThat(taskRepository.list().size(), is(equalTo(0)));
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void shouldNotAllowEmptyDueDate() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        HttpEntity<TaskDTO> request = new HttpEntity<>(
                new TaskDTO("Title-of-task", "Description-of-task"
                        , null)
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        String actual = responseEntity.getBody();
        String expected = "Due Date must not be empty";

        assertThat(taskRepository.list().size(), is(equalTo(0)));
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void shouldNotAllowDueDateOlderThanNow() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        HttpEntity<TaskDTO> request = new HttpEntity<>(
                new TaskDTO("Title-of-task", "Description-of-task"
                        , LocalDateTime.now().plusMinutes(-1))
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        String actual = responseEntity.getBody();
        String expected = "Due date must be a future date";

        assertThat(taskRepository.list().size(), is(equalTo(0)));
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void shouldNotAllowDueDateToBeEqualToNow() throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        HttpEntity<TaskDTO> request = new HttpEntity<>(
                new TaskDTO("Title-of-task", "Description-of-task"
                        , LocalDateTime.now())
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreateUrl, request, String.class);

        String actual = responseEntity.getBody();
        String expected = "Due date must be a future date";

        assertThat(taskRepository.list().size(), is(equalTo(0)));
        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void shouldListAllTasks() throws URISyntaxException {
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);
        String title = "Listed-title-of-task", description = "Listed-description-of-task";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        createTask(title, description, dueDate, null, null, null);

        URI urlOfGetAll = new URI(createURLWithPort("/tasks"));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(urlOfGetAll, String.class);

        String actual = responseEntity.getBody();
        String expected = "[{\"id\":1,\"title\":\"Listed-title-of-task1\"" +
                ",\"description\":\"Listed-description-of-task1\",\"dueDate\":\"" + dueDate.format(formatter) + "\"}" +
                ",{\"id\":2,\"title\":\"Listed-title-of-task2\",\"description\"" +
                ":\"Listed-description-of-task2\",\"dueDate\":\"" + dueDate.format(formatter) + "\"}" +
                ",{\"id\":3,\"title\":\"Listed-title-of-task3\",\"description\"" +
                ":\"Listed-description-of-task3\",\"dueDate\":\"" + dueDate.format(formatter) + "\"}]";

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(expected, is(equalTo(actual)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void shouldNotFindTaskToList() throws URISyntaxException {
        URI urlOfGetAll = new URI(createURLWithPort("/tasks"));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(urlOfGetAll, String.class);

        String actual = responseEntity.getBody();
        String expected = "[]";

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldGetTaskById() throws URISyntaxException, JsonProcessingException {
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);
        String title = "Get-by-id-task-title", description = "Get-by-id-task-description";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String createdTaskResponse = createTask(title, description, dueDate
                , null, null, null);

        Map<String, Long> createResponseMap = jsonMapper.readValue(createdTaskResponse, new TypeReference<Map<String, Long>>() {
        });
        long idOfTaskToGet = createResponseMap.get("id");

        URI urlOfGetById = new URI(createURLWithPort("/task/" + idOfTaskToGet));

        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(urlOfGetById, String.class);

        String actual = responseEntity.getBody();
        String expected = "{\"id\":" + idOfTaskToGet + ",\"title\":\"Get-by-id-task-title2\"" +
                ",\"description\":\"Get-by-id-task-description2\"" +
                ",\"dueDate\":\"" + dueDate.format(formatter) + "\"}";

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(expected, is(equalTo(actual)));
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
        String createdTaskResponse = createTask(title, description, dueDate,
                null, null, null);

        Map<String, Long> map = jsonMapper.readValue(createdTaskResponse, new TypeReference<Map<String, Long>>() {
        });
        long idOfTaskToDelete = map.get("id");

        URI urlOfDelete = new URI(createURLWithPort("/task/" + idOfTaskToDelete + ""));
        ResponseEntity<String> deletedResponse = this.testRestTemplate.exchange(
                urlOfDelete, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        String expected = "{\"id\":" + idOfTaskToDelete + "}";

        assertThat(createdTaskResponse, is(containsString(expected)));
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
        createTask(title, description, dueDate, noFilterTitle, noFilterDesc, noFilterDueDate);

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
        createTask(title, description, dueDate, noFilterTitle, noFilterDesc, noFilterDueDate);

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
        createTask(title, description, dueDate, noFilterTitle, noFilterDesc, noFilterDueDate);

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
        createTask(title, description, dueDate, noFilterTitle, noFilterDesc, noFilterDueDate);

        URI urlOfFilterByDesc = new URI(createURLWithPort("/tasks/titleordesc?keyword=" + description.toLowerCase(Locale.ENGLISH)));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDesc, String.class);

        String actual = filterResponse.getBody();

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(containsString(description)));
        assertThat(actual, not(containsString("No-fil-ter-by-desc")));
    }

    @Test
    public void shouldFindNoTaskWhenFilterByTitleOrDescription() throws URISyntaxException {
        URI urlOfFilterByTitle = new URI(createURLWithPort("/tasks/titleordesc?keyword=Unavailable-title-or-description"));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByTitle, String.class);

        String actual = filterResponse.getBody();
        String expected = "[]";

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(expected, is(equalTo(actual)));
    }

    @Test
    public void shouldFilterByDueDate() throws URISyntaxException {
        String title = "Filter-by-due-date-title", noFilterTitle = "No-filter-by-title";
        String description = "Filter-by-due-date-description", noFilterDesc = "No-filter-by-description";
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10), noFilterDueDate = dueDate.plusMinutes(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        createTask(title, description, dueDate, noFilterTitle, noFilterDesc, noFilterDueDate);

        URI urlOfFilterByDueDate = new URI(createURLWithPort("/tasks/duedate?duedate=" + dueDate.plusMinutes(4).format(formatter)));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDueDate, String.class);

        String actual = filterResponse.getBody();

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(actual, is(containsString(description)));
        assertThat(actual, not(containsString("No-filter-by-description")));
    }

    @Test
    public void shouldFindNoTaskToFilterByDueDate() throws URISyntaxException {
        LocalDateTime dueDate = LocalDateTime.now().plusSeconds(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        URI urlOfFilterByDueDate = new URI(createURLWithPort("/tasks/duedate?duedate=" + dueDate.plusMinutes(4).format(formatter)));
        ResponseEntity<String> filterResponse =
                this.testRestTemplate.getForEntity(urlOfFilterByDueDate, String.class);

        String actual = filterResponse.getBody();
        String expected = "[]";

        assertThat(filterResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(expected, is(equalTo(actual)));
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

    public String createTask(String title1, String description1, LocalDateTime dueDate1,
                             String title2, String description2, LocalDateTime dueDate2) throws URISyntaxException {
        URI taskCreateUrl = new URI(createURLWithPort("/task"));
        String responseEntity = null;
        if (title2 == null && description2 == null && dueDate2 == null) {
            for (int i = 1; i <= 3; i++) {
                HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                        new TaskDTO(title1 + i, description1 + i
                                , dueDate1)
                );
                ResponseEntity<String> res = this.testRestTemplate.postForEntity(taskCreateUrl, request, String.class);
                if (i == 2) {
                    responseEntity = res.getBody();
                }
            }
            return responseEntity;
        } else {
            for (int i = 1; i <= 3; i++) {
                HttpEntity<TaskDTO> request1 = new HttpEntity<TaskDTO>(
                        new TaskDTO(title1 + i, description1 + i
                                , dueDate1)
                );
                HttpEntity<TaskDTO> request2 = new HttpEntity<TaskDTO>(
                        new TaskDTO(title2 + i, description2 + i
                                , dueDate2)
                );

                ResponseEntity<String> res = this.testRestTemplate.postForEntity(taskCreateUrl, request1, String.class);
                this.testRestTemplate.postForEntity(taskCreateUrl, request2, String.class);
                if (i == 2) {
                    responseEntity = res.getBody();
                }
            }
        }
        return responseEntity;
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + randomServerPort + "/todoly" + uri;
    }
}