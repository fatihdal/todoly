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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
    public void shouldCreateTask() throws URISyntaxException {
        URI createUrl = new URI(createURLWithPort("/task"));

        HttpEntity<TaskDTO> request = new HttpEntity<TaskDTO>(
                new TaskDTO("Created-title-of-task", "Created-description-of-task"
                        , LocalDateTime.now().plusDays(10))
        );

        ResponseEntity<String> responseEntity = this.testRestTemplate
                .postForEntity(createUrl, request, String.class);
        logger.warn(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + randomServerPort + "/todoly" + uri;
    }


}
