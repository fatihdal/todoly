package dal.fatih.todoly;

import java.sql.*;
import java.util.UUID;
import java.sql.Date;

public class TaskRepository implements DBManager {

    private Connection connection;
    private ResultSet resultSet;
    final DBConnection dbConnection = new DBConnection();
    private PreparedStatement preparedStatement;

    public Connection connect() throws SQLException {
        connection = dbConnection.getConnection();
        return connection;
    }

    public String sqlPrepare(String sql) {
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return sql;
    }

    @Override
    public void createTable() {

        String sql = "CREATE TABLE IF NOT EXISTS tasks"
                + "  (Id INT AUTO_INCREMENT primary key NOT NULL,"
                + "   taskId UUID(36),"
                + "   title VARCHAR(35),"
                + "   description VARCHAR(250),"
                + "   dueDate DATE)";
        try {
            sqlPrepare(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            System.out.println("Check database connections, drivers!!!");
        }
    }

    @Override
    public void create(Task task) {
        String sql = ("INSERT INTO TASKS " + "VALUES (?,?,?,?,?)");
        try {
            sqlPrepare(sql);
            preparedStatement.setString(1, null);
            preparedStatement.setString(2, task.getId().toString());
            preparedStatement.setString(3, task.getTitle());
            preparedStatement.setString(4, task.getDescription());
            preparedStatement.setString(5, task.getDate().toString());
            preparedStatement.executeUpdate();

            System.out.println("\n" + task.getId() + " Task added");
            preparedStatement.close();
        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect date format");

        } catch (SQLDataException e) {
            System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void listAll() {
        int counter = 0;
        try {
            String sql = "select ID,TASKID,TITLE,DESCRIPTION,DUEDATE from TASKS";
            sqlPrepare(sql);
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
            if (counter < 1) {
                System.out.println("------------------");
            }
        } catch (Exception e) {
            System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
        }
    }

    @Override
    public void getDetails(String taskIdInput) {
        int counter = 0;
        try {
            String sql = "SELECT taskId,title,description,duedate FROM tasks WHERE taskId=? limit 1";
            sqlPrepare(sql);
            preparedStatement.setObject(1, taskIdInput);
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

        } catch (NullPointerException e) {
            System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
        } catch (SQLDataException e) {
            System.out.println("No task found");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(String taskIdInput) {
        try {
            String sql = "delete FROM tasks WHERE taskId=?";
            sqlPrepare(sql);
            preparedStatement.setObject(1, taskIdInput);
            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("Task deleted");
            } else {
                System.out.println("No task found");
            }
            preparedStatement.close();
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

    @Override
    public void listBetweenTwoDays(String lastDate) {
        int counter = 0;
        try {
            Date date = Date.valueOf(lastDate);
            String sql = "select * from TASKS where DUEDATE BETWEEN now() and ?";
            sqlPrepare(sql);
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
            if (counter < 1) {
                System.out.println("No task found in this date range");
            }
        } catch (NullPointerException e) {
            System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
        } catch
        (IllegalArgumentException e) {
            System.out.println("Incorrect date format");
        } catch (SQLException e) {
            System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
        }
    }

    @Override
    public void filterByTitleOrDescription(String keyword) {

        int counter = 0;
        try {
            String sql = "select taskId,title,description,dueDate from TASKS where title like ?" + "or description like ?";
            sqlPrepare(sql);
            preparedStatement.setObject(1, "%" + keyword.toLowerCase() + "%");
            preparedStatement.setObject(2, "%" + keyword.toLowerCase() + "%");
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
            if (counter < 1) {
                System.out.println("No task found");
            }
        } catch (SQLException e) {
            System.out.println("Database may be already in use, close all \nother connection please or check database driver!!!");
        }
    }
}