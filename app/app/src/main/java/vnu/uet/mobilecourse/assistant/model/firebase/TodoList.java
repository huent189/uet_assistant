package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;

import vnu.uet.mobilecourse.assistant.model.event.PriorityTodoList;

public class TodoList implements IFirebaseModel {

    private String ownerId;
    private String id;
    private String title;
    private String description;

    @Exclude
    private PriorityTodoList todos = new PriorityTodoList();

    @Exclude
    public PriorityTodoList getTodos() {
        return todos;
    }

    public boolean contains(Todo todo) {
        return todos.contains(todo);
    }

    public void clear() {
        todos.clear();
    }

    public void add(Todo todo) {
        todos.add(todo);
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        int size = todos.size();

        long complete = todos.stream().filter(Todo::isCompleted).count();

        return (float) complete / (float) size;
    }

    @NonNull
    @Override
    public TodoList clone() {
        TodoList todoList = new TodoList();
        todoList.setOwnerId(ownerId);
        todoList.setId(id);
        todoList.setDescription(description);
        todoList.setTitle(title);

        return todoList;
    }
}
