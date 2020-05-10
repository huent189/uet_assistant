package vnu.uet.mobilecourse.assistant.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import vnu.uet.mobilecourse.assistant.model.todo.AbstractTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;

public class TodoRepository implements ITodoRepository {
    private static TodoRepository instance;

    private Map<String, DailyTodoList> dailyList = new HashMap<>();

    public static TodoRepository getInstance() {
        if (instance == null)
            instance = new TodoRepository();

        return instance;
    }

    public Map<String, DailyTodoList> getDailyLists() {
        return dailyList;
    }

    @Override
    public LiveData<DailyTodoList> getTodoListByDate(Date date) {
        return null;
    }

    @Override
    public LiveData<AbstractTodoList> getTodoListByRange(Date from, Date to) {
        return null;
    }

    @Override
    public LiveData<List<TodoList>> getAllTodoLists() {
        return null;
    }

    @Override
    public void addTodo(Todo todo) {

    }

    @Override
    public Todo deleteTodo(String id) {
        return null;
    }

    @Override
    public TodoList deleteTodoList(String id) {
        return null;
    }

    @Override
    public void modifyTodo(String id, Todo newTodo) {

    }

    @Override
    public void modifyTodoList(String id, TodoList newList) {

    }
}
