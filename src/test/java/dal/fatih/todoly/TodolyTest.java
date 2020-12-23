package dal.fatih.todoly;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class TodolyTest {

    private ByteArrayOutputStream outContent;
    private ByteArrayInputStream inContent;
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
    public void shouldNotSupportFilteringTasksByNameAndDescriptionYet() {
        provideInput(Arrays.asList("6", "q"));
        App.main(new String[]{});
        Assert.assertTrue(outContent.toString().contains("ERROR: This option is not supported yet"));
    }

    private void provideInput(List<String> inputs) {
        final ByteArrayInputStream in = new ByteArrayInputStream(String.join("\n", inputs).getBytes(StandardCharsets.UTF_8));
        System.setIn(in);
    }
}
