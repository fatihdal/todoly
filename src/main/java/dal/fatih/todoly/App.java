package dal.fatih.todoly;


import java.sql.SQLException;

public class App {

    public static void main(String[] args) {
        TaskManager taskManager = null;
        try {
            taskManager = new TaskManager();
            taskManager.handleInputs();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}