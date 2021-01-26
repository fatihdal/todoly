package dal.fatih.todoly;

import java.sql.*;
import java.util.UUID;
import java.sql.Date;

public class TaskRepository {

    private ResultSet resultSet;
    final DBConnection dbConnection = new DBConnection();
    protected Connection connection = dbConnection.getConnection();
    final PreparedStatement createTablePreparedStatement;
    final PreparedStatement createTaskPrepareStatement;
    final PreparedStatement listAllTaskPrepareStatement;
    final PreparedStatement getDetailsPreparedStatement;
    final PreparedStatement deleteTaskByIdPreparedStatement;
    final PreparedStatement listBetweenPreparedStatement;
    final PreparedStatement filterByTitlePreparedStatement;

    public TaskRepository() throws SQLException {
        createTablePreparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS tasks"
                + "  (Id INT AUTO_INCREMENT primary key NOT NULL,"
                + "   taskId UUID(36),"
                + "   title VARCHAR(35),"
                + "   description VARCHAR(250),"
                + "   dueDate DATE)");
        createTable();
        createTaskPrepareStatement =
                connection.prepareStatement("INSERT INTO TASKS " + "VALUES (?,?,?,?,?)");
        listAllTaskPrepareStatement =
                connection.prepareStatement("select ID,TASKID,TITLE,DESCRIPTION,DUEDATE from TASKS");
        getDetailsPreparedStatement =
                connection.prepareStatement("SELECT taskId,title,description,duedate FROM tasks WHERE taskId=? limit 1");
        deleteTaskByIdPreparedStatement =
                connection.prepareStatement("delete FROM tasks WHERE taskId=?");
        listBetweenPreparedStatement =
                connection.prepareStatement("select * from TASKS where DUEDATE BETWEEN now() and ?");
        filterByTitlePreparedStatement =
                connection.prepareStatement("select taskId,title,description,dueDate from TASKS where title like ? "
                        + "or description like ?");
    }

    public void createTable() {
        try {
            createTablePreparedStatement.executeUpdate();
        } catch (SQLException exception) {
            System.out.println("Check database connections, drivers!!!");
        }
    }

    public void create(Task task) {
        try {
            createTaskPrepareStatement.setString(1, null);
            createTaskPrepareStatement.setString(2, task.getId().toString());
            createTaskPrepareStatement.setString(3, task.getTitle());
            createTaskPrepareStatement.setString(4, task.getDescription());
            createTaskPrepareStatement.setString(5, task.getDate().toString());
            createTaskPrepareStatement.executeUpdate();

            System.out.println("\n" + task.getId() + " Task added");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void listAll() {
        int counter = 0;
        try {
            resultSet = listAllTaskPrepareStatement.executeQuery();
            while (resultSet.next()) {
                counter++;
                String taskId = resultSet.getString(2);
                String title = resultSet.getString(3);
                System.out.println("Id: " + taskId);
                System.out.println("Title: " + title);
                System.out.println("-------------------------------------------------");
            }
            if (counter < 1) {
                System.out.println("------------------");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void getDetails(String taskIdInput) {
        int counter = 0;
        try {
            getDetailsPreparedStatement.setObject(1, taskIdInput);
            resultSet = getDetailsPreparedStatement.executeQuery();
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

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(String taskIdInput) {
        try {
            deleteTaskByIdPreparedStatement.setObject(1, taskIdInput);
            int rows = deleteTaskByIdPreparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("Task deleted");
            } else {
                System.out.println("No task found");
            }
        } catch (NullPointerException e) {
            System.out.println("null pon");
        } catch
        (SQLDataException e) {
            System.out.println("No task found");
        } catch (Exception e) {
            System.out.println(e.getMessage().substring(0, 45));
            System.out.println(e.getStackTrace());
        }
    }

    public void listBetweenTwoDays(String lastDate) {
        int counter = 0;
        try {
            Date date = Date.valueOf(lastDate);
            listBetweenPreparedStatement.setObject(1, date);
            resultSet = listBetweenPreparedStatement.executeQuery();
            while (resultSet.next()) {
                counter++;
                String title = resultSet.getString(3);
                Date duedate = resultSet.getDate(5);
                System.out.println("Title: " + title);
                System.out.println("Due Date: " + duedate);
                System.out.println("-------------------------------------------------");
            }
            if (counter < 1) {
                System.out.println("No task found in this date range");
            }
        } catch (SQLDataException e) {
            System.out.println("Incorrect date format");
        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect date format");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void filterByTitleOrDescription(String keyword) {

        int counter = 0;
        try {
            filterByTitlePreparedStatement.setObject(1, "%" + keyword.toLowerCase() + "%");
            filterByTitlePreparedStatement.setObject(2, "%" + keyword.toLowerCase() + "%");
            resultSet = filterByTitlePreparedStatement.executeQuery();

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
            if (counter < 1) {
                System.out.println("No task found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}