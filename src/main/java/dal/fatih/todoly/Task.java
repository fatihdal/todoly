package dal.fatih.todoly;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Task implements Serializable {

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

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    /*public void setTitle(String title) {
        this.title = title;
    }*/

    public String getDescription() {
        return description;
    }

    /*public void setDescription(String description) {
        this.description = description;
    }*/

    public Date getDate() {
        return dueDate;
    }

    /*public void setDate(Date date) {
        this.dueDate = date;
    }*/

    @Override
    public String toString() {
        return "\nID : " + id +
                "\nTITLE : " + title +
                "\nDESCRIPTION : " + description +
                "\nDUE DATE : " + dueDate;
    }
}