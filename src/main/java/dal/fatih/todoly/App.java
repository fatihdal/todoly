package dal.fatih.todoly;


import java.sql.SQLException;

public class App {

    public static void main(String[] args) {
        TaskManager taskManager;
        try {
            taskManager = new TaskManager();
            taskManager.handleInputs();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}