package vnu.uet.mobilecourse.assistant.model.todo;

import java.util.Date;

public class Todo {
    private String id;

    private String title;

    private String todoListId;

    private String todoListTitle;

    private String description;

    private Date deadline;

    private Status status = Status.DOING;

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

    public String getTodoListId() {
        return todoListId;
    }

    public void setTodoListId(String todoListId) {
        this.todoListId = todoListId;
    }

    public void setTodoListTitle(String todoListTitle) {
        this.todoListTitle = todoListTitle;
    }

    public String getTodoListTitle() {
        return todoListTitle;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        DOING,
        DONE
    }
}
