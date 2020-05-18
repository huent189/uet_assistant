package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoListDocument;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class CalendarSharedViewModel extends ViewModel {
    private MutableLiveData<String> todoListTitle = new MutableLiveData<>();

    private MutableLiveData<String> todoTitle = new MutableLiveData<>();

    private MutableLiveData<String> todoDesc = new MutableLiveData<>();

    private MutableLiveData<String> todoDate = new MutableLiveData<>();

    private MutableLiveData<String> todoTime = new MutableLiveData<>();

    private MutableLiveData<Boolean> isCardExpand = new MutableLiveData<>();

    private TodoRepository todoRepo = TodoRepository.getInstance();

    public IStateLiveData<List<TodoListDocument>> getShallowTodoLists() {
        return todoRepo.getShallowTodoLists();
    }

    public IStateLiveData<TodoDocument> addTodo(TodoDocument todo) {
        return todoRepo.addTodo(todo);
    }

    public IStateLiveData<TodoListDocument> addTodoList(TodoListDocument todoList) {
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
