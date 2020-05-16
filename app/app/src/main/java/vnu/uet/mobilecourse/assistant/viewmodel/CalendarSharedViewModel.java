package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;

public class CalendarSharedViewModel extends ViewModel {
    private MutableLiveData<String> todoListTitle = new MutableLiveData<>();

    private MutableLiveData<String> todoTitle = new MutableLiveData<>();

    private MutableLiveData<String> todoDesc = new MutableLiveData<>();

    private MutableLiveData<String> todoDate = new MutableLiveData<>();

    private MutableLiveData<String> todoTime = new MutableLiveData<>();

    private MutableLiveData<Boolean> isCardExpand = new MutableLiveData<>();

    private TodoRepository todoRepo = TodoRepository.getInstance();

    public LiveData<List<TodoList>> getAllTodoLists() {
        return todoRepo.getAllTodoLists();
    }

    public TodoList findTodoListById(int id) {
        List<TodoList> todoLists = getAllTodoLists().getValue();

        if (todoLists != null) {
            for (TodoList list : todoLists) {
                if (list.getId().equals(String.valueOf(id)))
                    return list;
            }
        }

        return null;
    }

    public void addTodo(Todo todo) {
        todoRepo.addTodo(todo);
    }

    public void addTodoList(TodoList todoList) {
        todoRepo.addTodoList(todoList);
    }

    public MutableLiveData<Boolean> isCardExpand() {
        return isCardExpand;
    }

    public MutableLiveData<String> getTodoListTitle() {
        return todoListTitle;
    }

    public MutableLiveData<String> getTodoTitle() {
        return todoTitle;
    }

    public MutableLiveData<String> getTodoDesc() {
        return todoDesc;
    }

    public MutableLiveData<String> getTodoDate() {
        return todoDate;
    }

    public MutableLiveData<String> getTodoTime() {
        return todoTime;
    }
}
