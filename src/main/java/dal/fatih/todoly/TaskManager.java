package dal.fatih.todoly;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Scanner;
import java.util.UUID;

public class TaskManager {
    private final Scanner scn = new Scanner(System.in);
    TaskRepository taskRepository = new TaskRepository();
    private Connection connection = taskRepository.connection;

    public TaskManager() throws SQLException {
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
            Task task = new Task(UUID.randomUUID(), title, description, dueDate);
            taskRepository.create(task);
        }
    }

    private void listAllTasks() {
        taskRepository.getAll();
    }

    private void showTaskDetails() {
        System.out.print("Task Id (min first three characters) :");
        String taskIdInput = scn.nextLine();
        if (taskIdInput.length() < 3) {
            System.out.println("Please enter min first three characters!!!");
            return;
        }
        taskRepository.getDetails(taskIdInput);
    }

    private void deleteTask() {

        System.out.print("Task Id: ");
        String taskIdInput = scn.nextLine();
        taskRepository.delete(taskIdInput);
    }

    private void filterTasks() {
        System.out.print("Last date yyyy-MM-dd (*): ");
        String lastDate = scn.nextLine();
        taskRepository.filter(lastDate);
    }

    private void filterTasksbyNameAndDescription() {
        System.out.print("Word to search(min four char) : ");
        String keyword = scn.nextLine();
        if (keyword.length() < 4) {
            System.out.println("PLease enter the word to search!");
        } else {

            taskRepository.filterByTitleOrDescription(keyword);
        }
    }

    public void handleInputs() throws SQLException {

        int loopCounter = 0;

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

        while (true) {

            if (loopCounter >= 1) {
                System.out.println("To see the actions menu (T) ");
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
                filterTasks();
            } else if (transaction.equals("6")) {
                filterTasksbyNameAndDescription();
            } else {
                System.out.println("Invalid input");
            }
        }
        scn.close();
        if (connection != null) {
            connection.close();
        }
        System.out.println(connection.isClosed());
    }
}