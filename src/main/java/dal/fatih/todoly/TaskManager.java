package dal.fatih.todoly;

import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class TaskManager {
    private final Scanner scn = new Scanner(System.in);
    private JdbcTaskRepository jdbcTaskRepository = new JdbcTaskRepository();

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
            if (jdbcTaskRepository.create(task)) {
                System.out.println("\n" + task.getTaskId() + " Task added");
            }
        }
    }

    private void listAllTasks() {
        List <Task>tasks = jdbcTaskRepository.list();
        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                System.out.println("Task id: " + task.getTaskId());
                System.out.println("Title: " + task.getTitle());
                System.out.println("--------------------------------------");
            }
        } else {
            System.out.println("No task found");
        }
    }

    private void showTaskDetails() {
        System.out.print("Task Id :");
        String taskIdInput = scn.nextLine();
        try {
            Task task = jdbcTaskRepository.get(taskIdInput);
            if (task == null) {
                System.out.println("No task found");
            } else {
                System.out.println(task);
            }
        } catch (Exception e) {
            System.out.println("No task found");
        }
    }

    private void deleteTask() {
        System.out.print("Task Id: ");
        String taskIdInput = scn.nextLine();
        try {
            if (jdbcTaskRepository.delete(taskIdInput)) {
                System.out.println("Task deleted");
            } else {
                System.out.println("No task found");
            }
        } catch (Exception e) {
            System.out.println("No task found");
        }
    }

    private void filterTasks() {
        System.out.print("Last date yyyy-MM-dd (*): ");
        try {
            Date lastDate = Date.valueOf(scn.nextLine());
            if (lastDate.before(Date.from(Instant.now()))) {
                System.out.println("The given date can not be older than now");
                return;
            }
           List<Task> tasks = jdbcTaskRepository.filter(lastDate);

            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    System.out.println("Task id: " + task.getTitle());
                    System.out.println("Due date: " + task.getDueDate());
                    System.out.println("--------------------------------------");
                }
            } else {
                System.out.println("No task found in this date range");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect date format");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void filterTasksbyNameAndDescription() {
        System.out.print("Word to search(min four char) : ");
        String keyword = scn.nextLine();
        if (keyword.length() < 4) {
            System.out.println("PLease enter the word to search!");
        } else {
           List<Task> tasks = jdbcTaskRepository.filterByTitleOrDescription(keyword);
            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    System.out.println("Task ıd: " + task.getTaskId());
                    System.out.println("Title: " + task.getTitle());
                    System.out.println("Description: " + task.getDescription());
                    System.out.println("Due Date: " + task.getDueDate());
                    System.out.println("-------------------------------------------------");
                }
            } else {
                System.out.println("No task found");
            }
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
        jdbcTaskRepository.close();
    }
}