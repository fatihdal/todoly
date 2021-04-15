package dal.fatih.todoly.repo.impl;


import dal.fatih.todoly.DBConnection;
import dal.fatih.todoly.model.Task;
import dal.fatih.todoly.repo.TaskRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcTaskRepositoryImpl implements TaskRepository {

    private final DBConnection dbConnection = new DBConnection();
    private final Connection connection = dbConnection.getConnection();
    private final PreparedStatement createPrepareStatement;
    private final PreparedStatement listPrepareStatement;
    private final PreparedStatement getPreparedStatement;
    private final PreparedStatement deletePreparedStatement;
    private final PreparedStatement filterPreparedStatement;
    private final PreparedStatement getByTitleOrDesPreparedStatement;


    public JdbcTaskRepositoryImpl() throws SQLException {
        createTable();
        createPrepareStatement =
                connection.prepareStatement("insert into task " + "values (?,?,?,?)");
        listPrepareStatement =
                connection.prepareStatement("select * from task");
        getPreparedStatement =
                connection.prepareStatement("select * from task where id=? limit 1");
        deletePreparedStatement =
                connection.prepareStatement("delete from task where id=?");
        filterPreparedStatement =
                connection.prepareStatement("select * from task where due_Date between now() and ?");
        getByTitleOrDesPreparedStatement =
                connection.prepareStatement("select * from task where title like ? "
                        + "or description like ?");
    }

    public void createTable() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("create table if not exists task"
                    + "   (id uuid(36) primary key not null ,"
                    + "   description varchar(250), "
                    + "   dueDate date,"
                    + "   title varchar(35))");
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Task create(Task task) {
        try {
            createPrepareStatement.setLong(1, task.getId());
            createPrepareStatement.setString(2, task.getDescription());
            createPrepareStatement.setString(3, task.getDueDate().toString());
            createPrepareStatement.setString(4, task.getTitle());
            createPrepareStatement.executeUpdate();
            return task;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Task> list() {
        List<Task> tasks = new ArrayList<>();
        try {
            ResultSet resultSet = listPrepareStatement.executeQuery();
            while (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getLong("id"));
                task.setTitle(resultSet.getString("title"));
                tasks.add(task);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }

    @Override
    public Task get(Long idInput) {
        try {
            getPreparedStatement.setLong(1, idInput);
            ResultSet resultSet = getPreparedStatement.executeQuery();
            if (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getLong("id"));
                task.setTitle(resultSet.getString("title"));
                task.setDescription(resultSet.getString("description"));
                task.setDueDate(resultSet.getObject("dueDate", LocalDateTime.class));
                return task;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Task delete(Long idInput) {
        try {
            deletePreparedStatement.setObject(1, idInput);
            deletePreparedStatement.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Task> filterByDueDate(LocalDateTime lastDate) {
        try {
            List<Task> tasks = new ArrayList<>();
            filterPreparedStatement.setObject(1, lastDate);
            ResultSet resultSet = filterPreparedStatement.executeQuery();
            while (resultSet.next()) {
                Task task = new Task();
                task.setTitle(resultSet.getString("title"));
                task.setDueDate(resultSet.getObject("dueDate", LocalDateTime.class));
                tasks.add(task);
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Task> filterByTitleOrDescription(String keyword) {
        try {
            List<Task> tasks = new ArrayList<>();
            getByTitleOrDesPreparedStatement.setObject(1, "%" + keyword.toLowerCase() + "%");
            getByTitleOrDesPreparedStatement.setObject(2, "%" + keyword.toLowerCase() + "%");
            ResultSet resultSet = getByTitleOrDesPreparedStatement.executeQuery();

            while (resultSet.next()) {
                Task task = new Task();
                task.setId(resultSet.getLong("id"));
                task.setTitle(resultSet.getString("title"));
                task.setDescription(resultSet.getString("description"));
                task.setDueDate(resultSet.getObject("dueDate", LocalDateTime.class));
                tasks.add(task);
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}