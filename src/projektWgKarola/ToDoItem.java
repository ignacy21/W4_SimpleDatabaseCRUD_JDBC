package projektWgKarola;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ToDoItem {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private String name;
    private String description;
    private LocalDateTime deadline;
    private Integer priority;
    private Status status;

    public ToDoItem() {
        this.status = Status.TODO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", deadline=" + deadline +
                ", priority=" + priority +
                '}';
    }
    public enum Field {
        NAME,
        DESCRIPTION,
        DEADLINE,
        PRIORITY,
        SORT,
        STATUS
    }

    public enum Status {
        COMPLETED,
        TODO
    }
}
