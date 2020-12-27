package dal.fatih.todoly;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TodolyTest {

    private ByteArrayOutputStream outContent;
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @Before
    public void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
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
        addTask("title-of-task", "", "21/12/2021");
        provideInput(Arrays.asList("6", "tle-of", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("title-of-task"));
    }

    @Test
    public void shouldFindTasksByTitleNotCaseSensitive() {
        addTask("title-of-task", "", "21/12/2021");
        provideInput(Arrays.asList("6", "<TLE-OF>", "q"));
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
    public void shouldFindTasksByDescriptionNotCaseSensitive() {
        addTask("title-of-task", "description-of-task", "21/12/2021");
        provideInput(Arrays.asList("6", "DESCR", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("description-of-task"));
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
