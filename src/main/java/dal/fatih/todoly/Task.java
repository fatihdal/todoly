package dal.fatih.todoly;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Task {

    private String id;
    private String title;
    private String description;
    private Date dueDate;
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    public Task(String title, String description, Date dueDate) {

        id = String.valueOf(idGenerator.getAndIncrement());
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Task(String title, Date dueDate) {
        this.title = title;
        this.dueDate = dueDate;

    }


    public String getId() {
        return id;
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
        return "\nID =" + id +
                "\nTITLE =" + title +
                "\nDESCRIPTION =" + description +
                "\nDATE = " + dueDate;
    }
}