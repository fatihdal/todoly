package dal.fatih.todoly;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBManager {

    Connection connect() throws SQLException;

    void createTable();

    void create(Task task) throws SQLException;

    void listAll();

    void getDetails(String taskId);

    void delete(String taskId);

    void listBetweenTwoDays(String lastDate);

    void filterByTitleOrDescription(String keyword);
}