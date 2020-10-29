package dal.fatih.todoly;

import java.util.Date;

public class Task {

    private String title;
    private String description;
    private Date dueDate;

    public Task(String title, String description, Date dueDate) {

        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Task(String title, Date dueDate) {
        this.title = title;
        this.dueDate = dueDate;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return dueDate;
    }

    public void setDate(Date date) {
        this.dueDate = date;
    }

    @Override
    public String toString() {
        return "\nTITLE = " + title +
                "\nDESCRIPTION =" + description +
                "\nDUE DATE = " + dueDate;
    }
}