package dal.fatih.todoly;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    private ResultSet resultSet;
    final DBConnection dbConnection = new DBConnection();
    protected Connection connection = dbConnection.getConnection();
    final PreparedStatement createPrepareStatement;
    final PreparedStatement listPrepareStatement;
    final PreparedStatement getPreparedStatement;
    final PreparedStatement deleteTaskByIdPreparedStatement;
    final PreparedStatement filterPreparedStatement;
    final PreparedStatement getByTitleOrDesPreparedStatement;
    private List<Task> tasks;

    public TaskRepository() throws SQLException {

        createTable();
        createPrepareStatement =
                connection.prepareStatement("insert into tasks " + "values (?,?,?,?,?)");
        listPrepareStatement =
                connection.prepareStatement("select id,taskid,title,description,duedate from tasks");
        getPreparedStatement =
                connection.prepareStatement("select taskid,title,description,duedate from tasks where taskid=? limit 1");
        deleteTaskByIdPreparedStatement =
                connection.prepareStatement("delete from tasks where taskid=?");
        filterPreparedStatement =
                connection.prepareStatement("select * from tasks where duedate between now() and ?");
        getByTitleOrDesPreparedStatement =
                connection.prepareStatement("select taskid,title,description,duedate from tasks where title like ? "
                        + "or description like ?");
    }

    public void clearTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("TRUNCATE TABLE tasks");
            preparedStatement.execute();
            connection.close();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("create table if not exists tasks"
                    + "  (id int auto_increment primary key not null,"
                    + "   taskid uuid(36),"
                    + "   title varchar(35),"
                    + "   description varchar(250),"
                    + "   duedate date)");
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException exception) {
            System.out.println("Check database connections, drivers!!!");
        }
    }

    public void create(Task task) {
        try {
            createPrepareStatement.setString(1, null);
            createPrepareStatement.setString(2, task.getId().toString());
            createPrepareStatement.setString(3, task.getTitle());
            createPrepareStatement.setString(4, task.getDescription());
            createPrepareStatement.setString(5, task.getDueDate().toString());
            createPrepareStatement.executeUpdate();

            System.out.println("\n" + task.getId() + " Task added");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Task> list() {
        Task task;
        tasks = new ArrayList<>();
        try {
            resultSet = listPrepareStatement.executeQuery();
            while (resultSet.next()) {
                task = new Task();
                task.setId(resultSet.getObject(2, java.util.UUID.class));
                task.setTitle(resultSet.getString(3));
                tasks.add(task);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return tasks;
    }

    public Task get(String taskIdInput) {
        Task task = null;
        try {
            getPreparedStatement.setObject(1, taskIdInput);
            resultSet = getPreparedStatement.executeQuery();

            while (resultSet.next()) {
                task = new Task();
                task.setId(resultSet.getObject("taskId", java.util.UUID.class));
                task.setTitle(resultSet.getString("title"));
                task.setDescription(resultSet.getString("description"));
                task.setDueDate(resultSet.getObject("dueDate", java.sql.Date.class));
            }
        } catch (Exception e) {
        }
        return task;
    }

    public int delete(String taskIdInput) {
        int count = 0;
        try {
            deleteTaskByIdPreparedStatement.setObject(1, taskIdInput);
            count = deleteTaskByIdPreparedStatement.executeUpdate();

        } catch (Exception e) {
        }
        return count;
    }

    public List<Task> filter(Date lastDate) {
        Task task;
        tasks = new ArrayList<>();
        try {
            filterPreparedStatement.setObject(1, lastDate);
            resultSet = filterPreparedStatement.executeQuery();
            while (resultSet.next()) {
                task = new Task();
                task.setTitle(resultSet.getString(3));
                task.setDueDate(resultSet.getObject(5, java.sql.Date.class));
                tasks.add(task);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return tasks;
    }

    public List<Task> filterByTitleOrDescription(String keyword) {
        Task task;
        tasks = new ArrayList<>();
        try {
            getByTitleOrDesPreparedStatement.setObject(1, "%" + keyword.toLowerCase() + "%");
            getByTitleOrDesPreparedStatement.setObject(2, "%" + keyword.toLowerCase() + "%");
            resultSet = getByTitleOrDesPreparedStatement.executeQuery();

            while (resultSet.next()) {
                task = new Task();
                task.setId(resultSet.getObject(1, java.util.UUID.class));
                task.setTitle(resultSet.getString(2));
                task.setDescription(resultSet.getString(3));
                task.setDueDate(resultSet.getObject(4, java.sql.Date.class));
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return tasks;
    }
}