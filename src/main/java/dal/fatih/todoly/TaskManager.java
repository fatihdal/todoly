package dal.fatih.todoly;

import java.sql.*;
import java.time.Instant;
import java.util.Scanner;
import java.util.UUID;

public class TaskManager {
    private final Scanner scn = new Scanner(System.in);
    private PreparedStatement preparedStatement;
    private Statement statement;
    private Connection connection;
    private ResultSet resultSet;
    private DBConnection dbConnection = new DBConnection();

    private void createTable() {

        String sql = "CREATE TABLE IF NOT EXISTS tasks"
                + "  (Id INT AUTO_INCREMENT primary key NOT NULL,"
                + "   taskId UUID(36),"
                + "   title VARCHAR(35),"
                + "   description VARCHAR(250),"
                + "   dueDate DATE)";
        try {
            statement = connection.createStatement();
            statement.execute(sql);
            statement.close();
        } catch (SQLException exception) {
            System.out.println("Check database connections, drivers!!!");
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
                String sql = ("INSERT INTO TASKS " + "VALUES (?,?,?,?,?)");
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, null);
                preparedStatement.setString(2, uniqId.toString());
                preparedStatement.setString(3, title);
                preparedStatement.setString(4, description);
                preparedStatement.setString(5, dueDate.toString());
                preparedStatement.executeUpdate();

                System.out.println("\n" + uniqId + " Task added");
                preparedStatement.close();
            } catch (IllegalArgumentException e) {
                System.out.println("Incorrect date format");

            } catch (SQLDataException e) {
                System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void listAllTasks() {
        int counter = 0;
        try {
            String sql = "select ID,TASKID,TITLE,DESCRIPTION,DUEDATE from TASKS";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                counter++;
                String taskId = resultSet.getString(2);
                String title = resultSet.getString(3);
                System.out.println("Id: " + taskId);
                System.out.println("Title: " + title);
                System.out.println("-------------------------------------------------");
            }
            preparedStatement.close();
            resultSet.close();
            if (counter < 1) {
                System.out.println("------------------");
            }
        } catch (Exception e) {
            System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
        }
    }

    private void showTaskDetails() {
        int counter = 0;
        System.out.print("Task Id (min first three characters) :");
        String taskIdInput = scn.nextLine();
        if (taskIdInput.length() < 3) {
            System.out.println("Please enter min first three characters!!!");
            return;
        }
        try {
            String sql = "SELECT taskId,title,description,duedate FROM tasks WHERE taskId like ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, "%" + taskIdInput + "%");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                counter++;
                UUID taskId = resultSet.getObject("taskId", java.util.UUID.class);
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Date dueDate = resultSet.getObject("dueDate", java.sql.Date.class);
                System.out.println("Task id : " + taskId);
                System.out.println("Title : " + title);
                System.out.println("Description : " + description);
                System.out.println("Due date : " + dueDate);
            }
            if (counter < 1) {
                System.out.println("No task found");
            }
            preparedStatement.close();
            resultSet.close();

        } catch (NullPointerException e) {
            System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteTask() {

        System.out.print("Task Id: ");
        String taskIdInput = scn.nextLine();

        try {
            String sql = "delete FROM tasks WHERE taskId=?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, taskIdInput);
            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("Task deleted");
            } else {
                System.out.println("No task found");
            }
            preparedStatement.close();
        } catch (SQLDataException e) {
            System.out.println("No task found");
        } catch (Exception e) {
            System.out.println(e.getMessage().substring(0, 45));
            System.out.println(e.getStackTrace());
        }
    }

    private void filterTasks() {
        System.out.print("Due Date yyyy-MM-dd (*): ");
        String lastDateInput = scn.nextLine();

        int counter = 0;
        try {
            Date date = Date.valueOf(lastDateInput);
            String sql = "select * from TASKS where DUEDATE BETWEEN now() and ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1, date);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                counter++;
                String title = resultSet.getString(3);
                Date duedate = resultSet.getDate(5);
                System.out.println("Title: " + title);
                System.out.println("Due Date: " + duedate);
                System.out.println("-------------------------------------------------");
            }
            preparedStatement.close();
            resultSet.close();
            if (counter < 1) {
                System.out.println("No task found in this date range");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect date format");
        } catch (SQLException e) {
            System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
        }
    }

    private void filterTasksbyNameAndDescription() {
        System.out.print("Word to search : ");
        String searchingWord = scn.nextLine();
        if (searchingWord.length() < 4) {
            System.out.println("PLease enter the word to search!");
        } else {
            int counter = 0;
            try {
                String sql = "select taskId,title,description,dueDate from TASKS where title like ?" + "or description like ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setObject(1, "%" + searchingWord.toLowerCase() + "%");
                preparedStatement.setObject(2, "%" + searchingWord.toLowerCase() + "%");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    counter++;
                    String taskId = resultSet.getString(1);
                    String title = resultSet.getString(2);
                    String description = resultSet.getString(3);
                    String duedate = resultSet.getString(4);

                    System.out.println("Task ıd: " + taskId);
                    System.out.println("Title: " + title);
                    System.out.println("Description: " + description);
                    System.out.println("Due Date: " + duedate);
                    System.out.println("-------------------------------------------------");
                }
                preparedStatement.close();
                resultSet.close();
                if (counter < 1) {
                    System.out.println("No task found");
                }
            } catch (SQLException e) {
                System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
            }
        }
    }

    public void handleInputs() {
        try {
            connection = dbConnection.getConnection();
            createTable();
        } catch (SQLException e) {
            System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
        }
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
            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
}