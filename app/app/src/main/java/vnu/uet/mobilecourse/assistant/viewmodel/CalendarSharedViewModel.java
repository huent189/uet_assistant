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

    public StateLiveData<List<TodoListDocument>> getAllTodoLists() {
        return todoRepo.getAllTodoLists();
    }

    public StateLiveData<TodoListDocument> findTodoListById(String id) {
        StateMediatorLiveData<TodoListDocument> liveData = new StateMediatorLiveData<>(new StateModel<>(StateStatus.LOADING));

        liveData.getMediatorListener().addSource(todoRepo.getAllTodoLists(), new Observer<StateModel<List<TodoListDocument>>>() {
            @Override
            public void onChanged(StateModel<List<TodoListDocument>> stateModel) {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        liveData.postLoading();
                        break;

                    case ERROR:
                        liveData.postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        TodoListDocument todoList = stateModel.getData().stream()
                                .filter(todo -> todo.getTodoListId().equals(id))
                                .findFirst()
                                .orElse(null);

                        liveData.postSuccess(todoList);
                }
            }
        });

        return liveData;
    }

    public void addTodo(TodoDocument todo) {
        todoRepo.addTodo(todo);
    }

    public void addTodoList(TodoListDocument todoList) {
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
