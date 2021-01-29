package dal.fatih.todoly;


import java.io.Closeable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository implements Closeable {

    private final DBConnection dbConnection = new DBConnection();
    private final Connection connection = dbConnection.getConnection();
    private final PreparedStatement createPrepareStatement;
    private final PreparedStatement listPrepareStatement;
    private final PreparedStatement getPreparedStatement;
    private final PreparedStatement deletePreparedStatement;
    private final PreparedStatement filterPreparedStatement;
    private final PreparedStatement getByTitleOrDesPreparedStatement;


    public TaskRepository() throws SQLException {
        createTable();
        createPrepareStatement =
                connection.prepareStatement("insert into tasks " + "values (?,?,?,?,?)");
        listPrepareStatement =
                connection.prepareStatement("select id,taskid,title,description,duedate from tasks");
        getPreparedStatement =
                connection.prepareStatement("select taskid,title,description,duedate from tasks where taskid=? limit 1");
        deletePreparedStatement =
                connection.prepareStatement("delete from tasks where taskid=?");
        filterPreparedStatement =
                connection.prepareStatement("select * from tasks where duedate between now() and ?");
        getByTitleOrDesPreparedStatement =
                connection.prepareStatement("select taskid,title,description,duedate from tasks where title like ? "
                        + "or description like ?");
    }

    public void createTable() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists tasks"
                    + "  (id int auto_increment primary key not null,"
                    + "   taskid uuid(36),"
                    + "   title varchar(35),"
                    + "   description varchar(250),"
                    + "   duedate date)");
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean create(Task task) {
        try {
            createPrepareStatement.setString(1, null);
            createPrepareStatement.setString(2, task.getId().toString());
            createPrepareStatement.setString(3, task.getTitle());
            createPrepareStatement.setString(4, task.getDescription());
            createPrepareStatement.setString(5, task.getDueDate().toString());
            createPrepareStatement.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> list() {
        List<Task> tasks = new ArrayList<>();
        try {
            ResultSet resultSet = listPrepareStatement.executeQuery();
            while (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getObject(2, java.util.UUID.class));
                task.setTitle(resultSet.getString(3));
                tasks.add(task);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }

    public Task get(String taskIdInput) {

        try {
            getPreparedStatement.setObject(1, taskIdInput);
            ResultSet resultSet = getPreparedStatement.executeQuery();
            if (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getObject("taskId", java.util.UUID.class));
                task.setTitle(resultSet.getString("title"));
                task.setDescription(resultSet.getString("description"));
                task.setDueDate(resultSet.getObject("dueDate", java.sql.Date.class));
                return task;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String taskIdInput) {
        try {
            deletePreparedStatement.setObject(1, taskIdInput);
            int deleted = deletePreparedStatement.executeUpdate();
            return deleted > 0;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> filter(Date lastDate) {
        try {
            List<Task> tasks = new ArrayList<>();
            filterPreparedStatement.setObject(1, lastDate);
            ResultSet resultSet = filterPreparedStatement.executeQuery();
            while (resultSet.next()) {
                Task task = new Task();
                task.setTitle(resultSet.getString(3));
                task.setDueDate(resultSet.getObject(5, java.sql.Date.class));
                tasks.add(task);
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Task> filterByTitleOrDescription(String keyword) {
        try {
            List<Task> tasks = new ArrayList<>();
            getByTitleOrDesPreparedStatement.setObject(1, "%" + keyword.toLowerCase() + "%");
            getByTitleOrDesPreparedStatement.setObject(2, "%" + keyword.toLowerCase() + "%");
            ResultSet resultSet = getByTitleOrDesPreparedStatement.executeQuery();

            while (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getObject(1, java.util.UUID.class));
                task.setTitle(resultSet.getString(2));
                task.setDescription(resultSet.getString(3));
                task.setDueDate(resultSet.getObject(4, java.sql.Date.class));
                tasks.add(task);
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
            createPrepareStatement.close();
            getPreparedStatement.close();
            deletePreparedStatement.close();
            filterPreparedStatement.close();
            getByTitleOrDesPreparedStatement.close();
        } catch (SQLException exception) {
            throw new RuntimeException();
        }
    }
}