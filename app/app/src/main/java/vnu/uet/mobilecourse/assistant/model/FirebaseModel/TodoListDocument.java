package vnu.uet.mobilecourse.assistant.model.FirebaseModel;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import vnu.uet.mobilecourse.assistant.model.todo.Todo;

public class TodoListDocument {
    private String ownerId;
    private String todoListId;
    private String title;
    private String description;
    private float progress;

    @Exclude
    private List<TodoDocument> todos = new ArrayList<>();

    public void setTodos(List<TodoDocument> todos) {
        this.todos = todos;
    }

    @Exclude
    public List<TodoDocument> getTodos() {
        return todos;
    }

    public boolean contains(TodoDocument todo) {
        return todos.contains(todo);
    }

    public void add(TodoDocument todo) {
        todos.add(todo);
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTodoListId() {
        return todoListId;
    }

    public void setTodoListId(String todoListId) {
        this.todoListId = todoListId;
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

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
