package dal.fatih.todoly;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodolyTest {

    private ByteArrayOutputStream outContent;
    private final Connection connection = new DBConnection().getConnection();

    public TodolyTest() throws SQLException {
    }

    @Before
    public void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    /*@After
    public void tearDown() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("TRUNCATE TABLE tasks");
            connection.close();
            statement.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    @Test
    public void shouldNotAllowWrongSelection() {
        provideInput(Arrays.asList("10", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("Invalid input"));
    }

    @Test
    public void shouldNotAllowEmptyTitle() {
        provideInput(Arrays.asList("1", "", "description-of-task", "2030-05-05", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("Fill required fields"));
    }

    @Test
    public void shouldNotAllowIncorrectDate() {
        provideInput(Arrays.asList("1", "title-of-task", "description-of-task", "21102021", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("Incorrect date format"));
    }

    @Test
    public void shouldNotAllowOldDateFromNow() {
        provideInput(Arrays.asList("1", "title-of-task", "description-of-task", "2020-05-05", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("The given date can not be older than now"));
    }

    @Test
    public void shouldNotAllowEmptyDueDate() {
        provideInput(Arrays.asList("1", "title-of-task", "description-of-task", "", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("Incorrect date format"));
    }

    @Test
    public void shouldAllowEmptyDescription() {
        provideInput(Arrays.asList("1", "title-of-task", "", "2030-05-05", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("Task added"));
    }

    @Test
    public void shouldListAllTasks() {
        addTask("title-of-the-task-to-be-listed", "description-of-task", "2030-05-05");
        addTask("title-of-the-task-to-be-listed-2", "description-of-task", "2030-05-05");
        provideInput(Arrays.asList("2", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("title-of-the-task-to-be-listed"));
        Assert.assertTrue(outContent.toString().contains("title-of-the-task-to-be-listed-2"));
    }

    @Test
    public void shouldFindNoTaskToShowDetails() {
        provideInput(Arrays.asList("3", "46856845672662", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("No task found"));
    }

    @Test
    public void shouldShowTaskDetails() {
        String title = "title-of-the-task";
        String description = "description-of-task";
        addTask(title, description, "2030-05-05");

        String taskIdPattern = "(.+)\\sTask\\sadded";
        Pattern r = Pattern.compile(taskIdPattern, Pattern.MULTILINE);
        Matcher m = r.matcher(outContent.toString());
        Assert.assertTrue(m.find());
        String taskId = m.group(1);
        Assert.assertNotNull(taskId);

        provideInput(Arrays.asList("3", taskId, "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains(taskId));
        Assert.assertTrue(outContent.toString().contains(title));
        Assert.assertTrue(outContent.toString().contains(description));
    }

    @Test
    public void shouldFindNoTaskToDelete() {
        provideInput(Arrays.asList("4", "", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("No task found"));
    }

    @Test
    public void shouldDeleteTask() {
        String title = "title-of-the-task-to-be-deleted";
        addTask(title, "description-of-task", "2030-05-05");

        String taskIdPattern = "^(.+)\\sTask\\sadded";
        Pattern r = Pattern.compile(taskIdPattern, Pattern.MULTILINE);
        Matcher m = r.matcher(outContent.toString());
        Assert.assertTrue(m.find());
        String taskId = m.group(1);
        Assert.assertNotNull(taskId);

        provideInput(Arrays.asList("4", taskId, "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("Task deleted"));
    }

    @Test
    public void shouldShowFilteringTaskByNameAndDescriptionWithMenuIndex6() {
        provideInput(Collections.singletonList("q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("6- Filter tasks by name and description"));
    }

    @Test
    public void shouldFindNoTask() {
        provideInput(Arrays.asList("6", "un-existing-task-name", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("No task found"));
    }

    @Test
    public void shouldFindTasksByTitle() {
        addTask("title-of-task", "description-of-task", "2030-05-05");
        provideInput(Arrays.asList("6", "tle-of", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("title-of-task"));
    }

    @Test
    public void shouldIgnoreCaseWhenFiltertingTasksByTitle() {
        addTask("title-of-task", "description-of-task", "2030-05-05");
        provideInput(Arrays.asList("6", "TLE-OF", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("title-of-task"));
    }

    @Test
    public void shouldFindTasksByDescription() {
        addTask("title-of-task", "description-of-task", "2030-05-05");
        provideInput(Arrays.asList("6", "descr", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("description-of-task"));
    }

    @Test
    public void shouldIgnoreCaseWhenFiltertingTasksByDescription() {
        addTask("title-of-task", "description-of-task", "2030-05-05");
        provideInput(Arrays.asList("6", "DESCR", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("description-of-task"));
    }

    @Test
    public void shouldFilterByDate() {
        addTask("title-of-the-task-to-be-filter", "description-of-task", "2026-05-05");
        addTask("title-of-the-task-to-be-filter-2", "description-of-task", "2030-05-05");
        provideInput(Arrays.asList("5", "2027-05-05", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("title-of-the-task-to-be-filter"));
        Assert.assertTrue(outContent.toString().contains("2026-05-05"));
        Assert.assertFalse(outContent.toString().contains("title-of-the-task-to-be-filter-2"));
        Assert.assertFalse(outContent.toString().contains("2030-05-05"));
    }

    @Test
    public void shouldFindNoTaskBetweenTwoDates() {
        provideInput(Arrays.asList("5", "2023-05-05", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("No task found in this date range"));
    }

    @Test
    public void shouldNotAllowIncorrectDateFormatWhenFilteringByDate() {
        provideInput(Arrays.asList("5", "01/012022", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("Incorrect date format"));
    }

    @Test
    public void shouldNotAllowEmptyDateWhenFilteringByDate() {
        provideInput(Arrays.asList("5", "", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("Incorrect date format"));
    }

    private void provideInput(List<String> inputs) {
        final ByteArrayInputStream in = new ByteArrayInputStream(String.join("\n", inputs).getBytes(StandardCharsets.UTF_8));
        System.setIn(in);
    }

    private void addTask(String title, String description, String dueDate) {
        provideInput(Arrays.asList("1", title, description, dueDate, "q"));
        App.main(new String[]{});
    }
}