package dal.fatih.todoly;

import java.sql.Date;
import java.util.UUID;

public class Task {

    private UUID id;
    private String title;
    private String description;
    private Date dueDate;

    public Task(UUID id, String title, String description, Date dueDate) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Task() {

    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDate() {
        return dueDate;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }


    public String toString() {
        return "\nID : " + id +
                "\nTITLE : " + title +
                "\nDESCRIPTION : " + description +
                "\nDUE DATE : " + dueDate;
    }
}