package vnu.uet.mobilecourse.assistant.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.model.todo.AbstractTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class TodoRepository implements ITodoRepository {
    private static TodoRepository instance;

//    private Map<String, DailyTodoList> dailyList = new HashMap<>();

    private MutableLiveData<List<TodoList>> todoListsLiveData = new MutableLiveData<>(new ArrayList<>());

    public static TodoRepository getInstance() {
        if (instance == null) {
            instance = new TodoRepository();
        }

        return instance;
    }

    public void setTodoLists(MutableLiveData<List<TodoList>> todoLists) {
        this.todoListsLiveData = todoLists;
    }

    @Override
    public LiveData<DailyTodoList> getTodoListByDate(Date date) {
        MediatorLiveData<DailyTodoList> dailyListLiveData = new MediatorLiveData<>();

        dailyListLiveData.addSource(todoListsLiveData, new Observer<List<TodoList>>() {
            @Override
            public void onChanged(List<TodoList> todoLists) {
                DailyTodoList dailyList = new DailyTodoList(date);

                for (TodoList todoList : todoLists) {
                    for (Todo todo : todoList) {

                        Date deadline = todo.getDeadline();

                        if (DateTimeUtils.isSameDate(date, deadline))
                            dailyList.add(todo);
                    }
                }

                dailyListLiveData.postValue(dailyList);
            }
        });

        return dailyListLiveData;
    }

    @Override
    public LiveData<AbstractTodoList> getTodoListByRange(Date from, Date to) {
        //TODO: implement later
        return null;
    }

    @Override
    public LiveData<List<TodoList>> getAllTodoLists() {
        return todoListsLiveData;
    }

    @Override
    public void addTodo(Todo todo) {
        List<TodoList> todoLists = todoListsLiveData.getValue();

        assert todoLists != null;
        for (TodoList todoList : todoLists) {
            if (todo.getTodoListId().equals(todoList.getId())) {
                todoList.add(todo);
                break;
            }
        }

        todoListsLiveData.postValue(todoLists);
    }

    @Override
    public void addTodoList(TodoList todoList) {
        List<TodoList> todoLists = todoListsLiveData.getValue();

        assert todoLists != null;
        todoLists.add(todoList);

        todoListsLiveData.postValue(todoLists);
    }

    @Override
    public Todo deleteTodo(String id) {
        List<TodoList> todoLists = todoListsLiveData.getValue();

        assert todoLists != null;
        for (TodoList todoList : todoLists) {
            for (Todo todo : todoList) {
                if (todo.getId().equals(id)) {
                    todoList.remove(todo);

                    todoListsLiveData.postValue(todoLists);

                    return todo;
                }
            }
        }

        return null;
    }

    @Override
    public TodoList deleteTodoList(String id) {
        List<TodoList> todoLists = todoListsLiveData.getValue();

        assert todoLists != null;
        for (TodoList todoList : todoLists) {
            if (todoList.getId().equals(id)) {
                todoLists.remove(todoList);

                todoListsLiveData.postValue(todoLists);

                return todoList;
            }
        }

        return null;
    }

    @Override
    public void modifyTodo(String id, Todo newTodo) {

    }

    @Override
    public void modifyTodoList(String id, TodoList newList) {

    }
}
