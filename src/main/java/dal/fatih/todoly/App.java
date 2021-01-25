package dal.fatih.todoly;


import java.sql.SQLException;

public class App {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        try {
            taskManager.handleInputs();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}