package dal.fatih.todoly;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TaskManager {
    private final Scanner scn = new Scanner(System.in);
    private Map<String, Task> tasks = new HashMap<String, Task>();
    private final DateFormat dueDateParser = new SimpleDateFormat("dd/MM/yyyy");
    private final File file = new File("./output/task.bin");
    public ObjectInputStream inputTask;
    public ObjectOutputStream outputTask;

    private void loadTasksFromFile() {
        try {
            inputTask = new ObjectInputStream(new FileInputStream(file));
            tasks = (HashMap) inputTask.readObject();
            inputTask.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private final void writeTaskFile() {
        try {
            file.getParentFile().mkdirs();
            outputTask = new ObjectOutputStream(new FileOutputStream(file));
            outputTask.writeObject(tasks);
            outputTask.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private Task handleCreateTask() {

        System.out.println("(*)  Can't be empty");
        System.out.print("Title of the task (*) : ");
        String title = scn.nextLine();
        System.out.print("Description(Optional): ");
        String description = scn.nextLine();
        System.out.print("Due Date dd/MM/yyyy (*): ");
        String dueDateInput = scn.nextLine();

        try {
            Date dueDate = dueDateParser.parse(dueDateInput);
            if (title.isEmpty()) {
                System.out.println("Fill required fields");

            } else if (dueDate.before(new Date())) {
                System.out.println("The given date can not be older than now");

            } else {

                UUID uniqId = UUID.randomUUID();
                Task task = new Task(uniqId, title, description, dueDate);
                tasks.put(uniqId.toString(), task);
                System.out.println();
                System.out.println(task.getId() + " Task added");
                writeTaskFile();
                return task;
            }
        } catch (Exception e) {
            System.out.println("Incorrect date format");
        }
        return null;
    }

    private void listAllTasks() {

        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
        } else {
            tasks.entrySet().forEach(stringTaskEntry -> {
                System.out.println("Title : " + stringTaskEntry.getValue().getTitle() +
                        "\n" + "ID :" + stringTaskEntry.getValue().getId());
                System.out.println("----------------------------------");
            });
        }
    }

    private void showTaskDetails() {

        System.out.print("Task Id :");
        String taskId = scn.nextLine();
        Task task = tasks.get(taskId);
        if (task != null) {
            System.out.println(task);
            System.out.println("----------------------------------");
        } else {
            System.out.println("Task not found");
        }
    }

    private void deleteTask() {

        System.out.print("Task Id :");
        String taskId = scn.nextLine();
        Task task = tasks.get(taskId);
        if (task != null) {
            tasks.remove(taskId);
            System.out.println(task.getTitle() + " titled task deleted");
            writeTaskFile();
        } else {
            System.out.println("Task not found");
        }
    }

    private void filterTask() {

        try {
            List<Task> foundTask = new ArrayList<>();
            System.out.println("Last Date");
            String lastDateInput = scn.nextLine();
            Date lastDate = dueDateParser.parse(lastDateInput);

            for (Task task : tasks.values()) {
                if (task.getDate().before(lastDate)) {
                    foundTask.add(task);
                }
            }
            if (foundTask.isEmpty()) {
                System.out.println("No task found in this date range");
            } else {
                System.out.println(foundTask);
            }
        } catch (Exception e) {
            System.out.println("Incorrect date format");
        }
    }

    private void filterTasksbyNameAndDescription() {
        List<Task> foundTasks = new ArrayList<>();
        System.out.println("Word to search");
        String searchingWord = scn.nextLine();

        for (Task task : tasks.values()) {
            if (task.getTitle().toLowerCase().contains(searchingWord.toLowerCase()) || task.getDescription().toLowerCase().contains(searchingWord.toLowerCase())) {
                foundTasks.add(task);
            }
        }
        if (foundTasks.isEmpty()) {
            System.out.println("No tasks found");
        } else {
            System.out.println(foundTasks);
        }
    }

    public void handleInputs() {
        loadTasksFromFile();
        System.out.println("Welcome to todoly");
        System.out.println("------------------------------------");
        String transactions = ("1- Create new task\n" +
                "2- All tasks list\n" +
                "3- Task details\n" +
                "4- Delete Task\n" +
                "5- List between two dates\n" +
                "6- Filter tasks by name and description\n" +
                "Q- Quit from Todoly");
        System.out.println("Transactions : \n" + transactions);
        System.out.println("Please select the action you want to do");
        int loopCounter = 0;
        while (true) {

            if (loopCounter >= 1) {
                System.out.println("To see the actions menu (t) ");
            }
            loopCounter++;
            System.out.print("Choice: ");
            String transaction = scn.nextLine();

            if (transaction.equals("q") || transaction.equals("Q")) {
                System.out.println("Exiting todoly");
                break;
            } else if (transaction.equals("t") || transaction.equals("T")) {
                System.out.println(transactions);
                loopCounter = 0;
            } else if (transaction.equals("1")) {
                handleCreateTask();
            } else if (transaction.equals("2")) {
                listAllTasks();
            } else if (transaction.equals("3")) {
                showTaskDetails();
            } else if (transaction.equals("4")) {
                deleteTask();
            } else if (transaction.equals("5")) {
                filterTask();
            } else if (transaction.equals("6")) {
                filterTasksbyNameAndDescription();
            } else {
                System.out.println("invalid input");
            }
        }
        scn.close();
    }
}
