package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;

public class TodoNotification extends Notification_UserSubCol {

    private String todoId;

    @Exclude
    private Todo todo;

    public TodoNotification() {
        super(NotificationType.TODO);
    }

    public String getTodoId() {
        return todoId;
    }

    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }

    @Exclude
    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }
}
