package projekt;

import java.time.LocalDateTime;

public class Row {

    private int id;
    private String name;
    private String description;
    private LocalDateTime deadline;
    private int priority;

    public Row(int id, String name, String description, LocalDateTime deadline, int priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return String.format("ID %s, name %s, description %s, deadline %s, priority %s",
                id, name, description, deadline, priority);
    }

    public enum rowName {
        ID,
        NAME,
        DESCRIPTION,
        DEADLINE,
        PRIORITY
    }
}
