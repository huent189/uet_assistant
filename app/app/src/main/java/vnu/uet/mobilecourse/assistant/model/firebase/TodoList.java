package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class TodoList implements IFirebaseModel {
    private String ownerId;
    private String id;
    private String title;
    private String description;
//    private int progress;

    @Exclude
    private List<Todo> todos = new ArrayList<>();

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }

    @Exclude
    public List<Todo> getTodos() {
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

//    public void setProgress(int progress) {
//        this.progress = progress;
//    }

    @NonNull
    @Override
    public TodoList clone() {
        TodoList todoList = new TodoList();
        todoList.setOwnerId(ownerId);
        todoList.setId(id);
        todoList.setDescription(description);
        todoList.setTitle(title);
//        todoList.setProgress(progress);

        return todoList;
    }
}
