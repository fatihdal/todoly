package dal.fatih.todoly;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.util.*;

public class TaskManager {
    private final Scanner scn = new Scanner(System.in);
    private Map<String, Task> tasks = new HashMap<String, Task>();
    private final File file = new File("./output/task.bin");
    private ObjectInputStream inputTask;
    private ObjectOutputStream outputTask;
    private PreparedStatement preparedStatement;
    private Connection connection;
    private DBConnection dbConnection = new DBConnection();

    private void loadTasksFromFile() {
        try {
            inputTask = new ObjectInputStream(new FileInputStream(file));
            tasks = (HashMap) inputTask.readObject();
            inputTask.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void createTable() {
        Statement statement = null;
        String sql = "CREATE TABLE IF NOT EXISTS tasks"
                + "  (Id INT AUTO_INCREMENT primary key NOT NULL,"
                + "   taskId UUID(36),"
                + "   title VARCHAR(25),"
                + "   description VARCHAR(35),"
                + "   dueDate DATE)";
        try {
            connection = dbConnection.getConnection();
            statement = connection.createStatement();
            statement.execute(sql);
            connection.close();
            statement.close();
        } catch (SQLException exception) {
            System.out.println("Database may already be in use,close all other connections and restart todoly!!!");
        }
    }

    private void handleCreateTask() {
        Date dueDate = null;
        System.out.println("(*)  Can't be empty");
        System.out.print("Title of the task (*) : ");
        String title = scn.nextLine();
        System.out.print("Description(Optional): ");
        String description = scn.nextLine();
        System.out.print("Due Date yyyy-MM-dd (*): ");
        String dueDateInput = scn.nextLine();
        try {
            dueDate = Date.valueOf(dueDateInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect date format");
            return;
        }
        if (title.isEmpty()) {
            System.out.println("Fill required fields");

        } else if (dueDate.before(Date.from(Instant.now()))) {

            System.out.println("The given date can not be older than now");

        } else {

            try {
                UUID uniqId = UUID.randomUUID();
                Task task = new Task(uniqId, title, description, dueDate);
                connection = dbConnection.getConnection();
                String sql = ("INSERT INTO TASKS " + "VALUES (?,?,?,?,?)");
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, null);
                preparedStatement.setString(2, uniqId.toString());
                preparedStatement.setString(3, title);
                preparedStatement.setString(4, description);
                preparedStatement.setString(5, dueDate.toString());
                preparedStatement.executeUpdate();

                System.out.println("\n" + task.getId() + " Task added");

                preparedStatement.close();
                connection.close();
            } catch (IllegalArgumentException e) {
                System.out.println("Incorrect date format");

            } catch (Exception e) {
                System.out.println("Database may already be in use,close all other connections and restart todoly!!!");
            }
        }
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
            //writeTaskFile();
        } else {
            System.out.println("Task not found");
        }
    }

    private void filterTask() {

       /* try {
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
        }*/
    }

    private void filterTasksbyNameAndDescription() {
        List<Task> foundTasks = new ArrayList<>();
        System.out.println("Word to search");
        String searchingWord = scn.nextLine();

        for (Task task : tasks.values()) {
            if (task.getTitle().toLowerCase().contains(searchingWord.toLowerCase()) ||
                    task.getDescription().toLowerCase().contains(searchingWord.toLowerCase())) {
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

        if (loopCounter == 0) {
            createTable();
        }
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
                System.out.println("Invalid input");
            }
        }
        scn.close();
    }
}