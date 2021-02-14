package dal.fatih.todoly;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
public class Task implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String taskId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private Date dueDate;

    public Task(String taskId, String title, String description, Date dueDate) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Task() {
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getTaskId() {
        return taskId;
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


    public String toString() {
        return "\nID : " + taskId +
                "\nTITLE : " + title +
                "\nDESCRIPTION : " + description +
                "\nDUE DATE : " + dueDate;
    }
}