package dal.fatih.todoly;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

@Entity(name = "Task")
@Table(name = "Tasks")
public class Task  implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Type(type = "uuid-char")
    @Column(name = "taskid", length = 36, nullable = false)
    private UUID taskId;

    @Column(name = "title", length = 35, nullable = false)
    private String title;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "duedate", nullable = false)
    private Date dueDate;

    public Task(UUID taskId, String title, String description, Date dueDate) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Task() {
    }

    public void setTaskId(UUID taskId) {
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

    public UUID getTaskId() {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "\nID : " + taskId +
                "\nTITLE : " + title +
                "\nDESCRIPTION : " + description +
                "\nDUE DATE : " + dueDate;
    }
}