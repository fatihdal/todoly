package dal.fatih.todoly.controller;

import dal.fatih.todoly.dto.TaskDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
        , properties = "spring.profiles.active=test")
public class TaskControllerTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void shouldCreateTask_201() throws URISyntaxException {
        URI taskCreationUrl = new URI(createURLWithPort("/task"));

        HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                new TaskDTO("Created-title-of-task", "Created-description-of-task"
                        , LocalDateTime.now().plusDays(10))
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreationUrl, request, String.class);
        logger.warn(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    @Test
    public void shouldAllowEmptyDescription_201() throws URISyntaxException {
        URI taskCreationUrl = new URI(createURLWithPort("/task"));

        HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                new TaskDTO("Title-of-task", null
                        , LocalDateTime.now().plusDays(10))
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreationUrl, request, String.class);
        logger.warn(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    @Test
    public void shouldNotAllowEmptyTitle_400() throws URISyntaxException {
        URI taskCreationUrl = new URI(createURLWithPort("/task"));
        HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                new TaskDTO(null, "Description-of-task"
                        , LocalDateTime.now().plusDays(10))
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreationUrl, request, String.class);

        String actual = responseEntity.getBody();
        String expected = "Title must not be empty";
        logger.warn(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowTitleLessThan5Characters_400() throws URISyntaxException {
        URI taskCreationUrl = new URI(createURLWithPort("/task"));
        HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                new TaskDTO("Titl", "Description-of-task"
                        , LocalDateTime.now().plusDays(10))
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreationUrl, request, String.class);

        String actual = responseEntity.getBody();
        String expected = "Title length must be between 5 and 120";
        logger.warn(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowEmptyDueDate_400() throws URISyntaxException {
        URI taskCreationUrl = new URI(createURLWithPort("/task"));

        HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                new TaskDTO("Title-of-task", "Description-of-task"
                        , null)
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreationUrl, request, String.class);

        String actual = responseEntity.getBody();
        String expected = "Due Date must not be empty";
        logger.warn(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowDueDateOlderThanNow_400() throws URISyntaxException {
        URI taskCreationUrl = new URI(createURLWithPort("/task"));

        HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                new TaskDTO("Title-of-task", "Description-of-task"
                        , LocalDateTime.now().plusMinutes(-1))
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreationUrl, request, String.class);

        String actual = responseEntity.getBody();
        String expected = "Due date must be a future date";
        logger.warn(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    public void shouldNotAllowDueDateToBeEqualToNow_400() throws URISyntaxException {
        URI taskCreationUrl = new URI(createURLWithPort("/task"));

        HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                new TaskDTO("Title-of-task", "Description-of-task"
                        , LocalDateTime.now())
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(taskCreationUrl, request, String.class);

        String actual = responseEntity.getBody();
        String expected = "Due date must be a future date";
        logger.warn(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(actual, is(containsString(expected)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void shouldListAllTasks_200() throws URISyntaxException {
        URI taskCreationUrl = new URI(createURLWithPort("/task"));
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        for (int i = 1; i <= 2; i++) {
            HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                    new TaskDTO("Title-of-task", "Description-of-task"
                            , dueDate)
            );
            this.testRestTemplate.postForEntity(taskCreationUrl, request, String.class);
        }

        URI getAllUrl = new URI(createURLWithPort("/tasks"));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(getAllUrl, String.class);
        logger.warn(responseEntity.getBody());

        String expected = responseEntity.getBody();
        String actual = "[{\"id\":1,\"title\":\"Title-of-task\"" +
                ",\"description\":\"Description-of-task\",\"dueDate\":\"" + dueDate.format(formatter) + "\"}" +
                ",{\"id\":2,\"title\":\"Title-of-task\",\"description\"" +
                ":\"Description-of-task\",\"dueDate\":\"" + dueDate.format(formatter) + "\"}]";

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(expected, is(containsString(actual)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void shouldNotFindTaskToList_200() throws URISyntaxException {
        URI getAllUrl = new URI(createURLWithPort("/tasks"));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(getAllUrl, String.class);
        logger.warn(responseEntity.getBody());

        String expected = responseEntity.getBody();
        String actual = "[]";

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(expected, is(containsString(actual)));
    }

    @Test
    public void shouldGetTaskById_200() throws URISyntaxException {
        URI taskCreationUrl = new URI(createURLWithPort("/task"));
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        ResponseEntity<String> createdTaskResponse = null;
        long idOfTaskToGet = 0;
        for (int i = 1; i <= 3; i++) {
            HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                    new TaskDTO("Get-by-id-task-title" + i, "Get-by-id-task-description" + i
                            , dueDate)
            );
            ResponseEntity<String> response = this.testRestTemplate
                    .postForEntity(taskCreationUrl, request, String.class);
            if (i == 2) createdTaskResponse = response;
        }
        //REGEX
        String taskIdPattern = "(/task/)(\\d*)";
        idOfTaskToGet = Long.parseLong(generateRegex(taskIdPattern, createdTaskResponse.toString()));

        URI getByIdUrl = new URI(createURLWithPort("/task/" + idOfTaskToGet + ""));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(getByIdUrl, String.class);
        logger.warn(responseEntity.getBody());

        String expected = responseEntity.getBody();
        String actual = "{\"id\":" + idOfTaskToGet + ",\"title\":\"Get-by-id-task-title2\"" +
                ",\"description\":\"Get-by-id-task-description2\"" +
                ",\"dueDate\":\"" + dueDate.format(formatter) + "\"}";

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(expected, is(equalTo(actual)));
    }

    @Test
    public void shouldNotFindTaskById_404() throws URISyntaxException {
        long unavailableTaskId = 446L;
        URI getByIdUrl = new URI(createURLWithPort("/task/" + unavailableTaskId + ""));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(getByIdUrl, String.class);
        logger.warn(responseEntity.getBody());

        String expected = responseEntity.getBody();
        String actual = "Task not found with id = " + unavailableTaskId;

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
        assertThat(expected, is(containsString(actual)));
    }

    @Test
    public void shouldNotAllowValueOtherThanNumberWhenGetTaskById_400() throws URISyntaxException {
        String unsupportedCharacterId = "446L";
        URI getByIdUrl = new URI(createURLWithPort("/task/" + unsupportedCharacterId + ""));
        ResponseEntity<String> responseEntity =
                this.testRestTemplate.getForEntity(getByIdUrl, String.class);
        logger.warn(responseEntity.getBody());

        String expected = responseEntity.getBody();
        String actual = "Failed to convert value of type of id";

        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(expected, is(containsString(actual)));
    }

    @Test
    public void shouldDeleteTaskById_200() throws URISyntaxException {
        URI taskCreationUrl = new URI(createURLWithPort("/task"));
        LocalDateTime dueDate = LocalDateTime.now().plusMinutes(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        ResponseEntity<String> createdTaskResponse = null;
        long idOfTaskToDelete = 0;

        for (int i = 1; i <= 3; i++) {
            HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                    new TaskDTO("Delete-by-id-task-title" + i, "Delete-by-id-task-description" + i
                            , dueDate)
            );
            ResponseEntity<String> response = this.testRestTemplate
                    .postForEntity(taskCreationUrl, request, String.class);
            if (i == 2) createdTaskResponse = response;
        }
        //REGEX
        String taskIdPattern = "(/task/)(\\d*)";
        idOfTaskToDelete = Long.parseLong(generateRegex(taskIdPattern, createdTaskResponse.toString()));

        URI deleteUrl = new URI(createURLWithPort("/task/" + idOfTaskToDelete + ""));
        ResponseEntity<String> deletedResponse = this.testRestTemplate.exchange(
                deleteUrl, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        String expected = createdTaskResponse.getBody();
        String actual = "/todoly/task/" + idOfTaskToDelete;

        assertThat(expected, is(containsString(actual)));
        assertThat(deletedResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
    }

    @Test
    public void shouldFindNoTaskToDelete_404() throws URISyntaxException {
        long unavailableTaskId = 446L;
        URI deleteUrl = new URI(createURLWithPort("/task/" + unavailableTaskId + ""));
        ResponseEntity<String> deletedResponse = this.testRestTemplate.exchange(
                deleteUrl, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        String expected = deletedResponse.getBody();
        String actual = "Task not found with id = " + unavailableTaskId;
        logger.warn(expected);

        assertThat(deletedResponse.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
        assertThat(expected, is(containsString(actual)));
    }

    @Test
    public void shouldNotAllowValueOtherThanNumberWhenDeleteTaskById_400() throws URISyntaxException {
        String unsupportedCharacterId = "446L";
        URI deleteUrl = new URI(createURLWithPort("/task/" + unsupportedCharacterId + ""));
        ResponseEntity<String> deletedResponse = this.testRestTemplate.exchange(
                deleteUrl, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);

        String expected = deletedResponse.getBody();
        String actual = "Failed to convert value of type of id";
        logger.warn(expected);

        assertThat(deletedResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(expected, is(containsString(actual)));
    }

    private String generateRegex(String inputPattern, String inputArgument) {
        Pattern regex = Pattern.compile(inputPattern);
        Matcher matcher = regex.matcher(inputArgument);
        assertThat(matcher.find(), is(true));
        return matcher.group(2);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + randomServerPort + "/todoly" + uri;
    }
}
