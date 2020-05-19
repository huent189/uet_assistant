package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.Todo;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoList;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class CalendarSharedViewModel extends ViewModel {
    private MutableLiveData<String> todoListTitle = new MutableLiveData<>();

    private MutableLiveData<String> todoTitle = new MutableLiveData<>();

    private MutableLiveData<String> todoDesc = new MutableLiveData<>();

    private MutableLiveData<String> todoDate = new MutableLiveData<>();

    private MutableLiveData<String> todoTime = new MutableLiveData<>();

    private MutableLiveData<Boolean> isCardExpand = new MutableLiveData<>();

    private TodoRepository todoRepo = TodoRepository.getInstance();

    public IStateLiveData<List<TodoList>> getShallowTodoLists() {
        return todoRepo.getShallowTodoLists();
    }

    public IStateLiveData<Todo> addTodo(Todo todo) {
        return todoRepo.addTodo(todo);
    }

    public IStateLiveData<TodoList> addTodoList(TodoList todoList) {
        return todoRepo.addTodoList(todoList);
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
