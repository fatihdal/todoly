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
    private ResultSet resultSet;

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
                + "   title VARCHAR(35),"
                + "   description VARCHAR(250),"
                + "   dueDate DATE)";
        try {
            connection = dbConnection.getConnection();
            statement = connection.createStatement();
            statement.execute(sql);
            connection.close();
            statement.close();
        } catch (SQLException exception) {
            System.out.println("Check database connections, drivers and restart todoly!!!");
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

            } catch (SQLDataException e) {
                System.out.println(e.getMessage().substring(0, 45));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void listAllTasks() {
        int counter = 0;
        try {
            connection = dbConnection.getConnection();

            String sql = "select ID,TASKID,TITLE,DESCRIPTION,DUEDATE from TASKS";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                counter = +1;
                String taskId = resultSet.getString(2);
                String title = resultSet.getString(3);
                System.out.println("Id: " + taskId);
                System.out.println("Title: " + title);
                System.out.println("-------------------------------------------------");
            }
            connection.close();
            preparedStatement.close();
            resultSet.close();
            if (counter < 1) {
                System.out.println("------------------");
            }
        } catch (SQLException e) {
            System.out.println("Check database connections, drivers and restart todoly");
        }
    }

    private void showTaskDetails() {
        System.out.print("Task Id (min first three characters) :");
        String taskIdInput = scn.nextLine();
        if (taskIdInput.length() < 3) {
            System.out.println("Please enter min first three characters!!!");
            return;
        }
        Task task = null;

        try {
            connection = dbConnection.getConnection();


            String sql = "SELECT id,taskId,title,description,duedate FROM tasks WHERE taskId like ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, taskIdInput + "%");
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println(e);
        }
        try {
            while (resultSet.next()) {
                UUID userId = resultSet.getObject("taskId", java.util.UUID.class);
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Date dueDate = resultSet.getObject("dueDate", java.sql.Date.class);
                task = new Task(userId, title, description, dueDate);
            }
            if (task != null) {
                System.out.println(task);
            } else {
                System.out.println("Task not found");
            }
            preparedStatement.close();
            resultSet.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
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