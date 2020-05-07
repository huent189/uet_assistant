package vnu.uet.mobilecourse.assistant.model;

import java.util.Date;

public class Todo {
    private String title;

    private String todoList;

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

    public String getTodoList() {
        return todoList;
    }

    public void setTodoList(String todoList) {
        this.todoList = todoList;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
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
