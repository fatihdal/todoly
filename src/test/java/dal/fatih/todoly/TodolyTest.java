package dal.fatih.todoly;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TodolyTest {

    private ByteArrayOutputStream outContent;

    @Before
    public void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
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
        Assert.assertTrue(outContent.toString().contains("No tasks found"));
    }

    @Test
    public void shouldFindTasksByTitle() {
        addTask("title-of-task", "description-of-task", "21/12/2021");
        provideInput(Arrays.asList("6", "tle-of", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("title-of-task"));
    }

    @Test
    public void shouldIgnoreCaseWhenFiltertingTasksByTitle() {
        addTask("title-of-task", "description-of-task", "21/12/2021");
        provideInput(Arrays.asList("6", "TLE-OF", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("title-of-task"));
    }

    @Test
    public void shouldFindTasksByDescription() {
        addTask("title-of-task", "description-of-task", "21/12/2021");
        provideInput(Arrays.asList("6", "descr", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("description-of-task"));
    }

    @Test
    public void shouldIgnoreCaseWhenFiltertingTasksByDescription() {
        addTask("title-of-task", "description-of-task", "21/12/2021");
        provideInput(Arrays.asList("6", "DESCR", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("description-of-task"));
    }

    @Test
    public void shouldNotAllowEmptyTitle() {
        provideInput(Arrays.asList("1", "", "description-of-task", "21/10/2021", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("fill required fields"));
    }

    @Test
    public void shouldNotAllowIncorrectDate() {
        provideInput(Arrays.asList("1", "title-of-task", "description-of-task", "21102021", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("Incorrect date format"));
    }

    @Test
    public void shouldNotAllowOldDateFromNow() {
        provideInput(Arrays.asList("1", "title-of-task", "description-of-task", "01/01/2020", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("The given date can not be older than now"));
    }

    @Test
    public void shouldAllowEmptyDescription() {
        provideInput(Arrays.asList("1", "title-of-task", "", "21/10/2021", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("task added"));
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
