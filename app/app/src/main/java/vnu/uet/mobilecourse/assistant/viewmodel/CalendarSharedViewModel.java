package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;
import java.util.Map;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class CalendarSharedViewModel extends ViewModel {

    private TodoRepository mTodoRepo = TodoRepository.getInstance();

    /**
     * use live data in shared view model
     * in order to save state between fragments
     */
    private MutableLiveData<String> mTodoListTitle = new MutableLiveData<>();
    private MutableLiveData<String> mTodoTitle = new MutableLiveData<>();
    private MutableLiveData<String> mTodoDesc = new MutableLiveData<>();
    private MutableLiveData<String> mTodoDate = new MutableLiveData<>();
    private MutableLiveData<String> mTodoTime = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsCardExpand = new MutableLiveData<>();

    private String mTodoListId;

    public void setTodoListId(String mTodoListId) {
        this.mTodoListId = mTodoListId;
    }

    public String getTodoListId() {
        return mTodoListId;
    }

    public IStateLiveData<List<TodoList>> getShallowTodoLists() {
        return mTodoRepo.getShallowTodoLists();
    }

    public void setDate(String date) {
        mTodoDate.postValue(date);
    }

    public void clearHistory() {
        mTodoListTitle.postValue(null);
        mTodoTitle.postValue(null);
        mTodoDesc.postValue(null);
        mTodoDate.postValue(null);
        mTodoTime.postValue(null);
        mIsCardExpand.postValue(false);
        mTodoListId = null;
    }

    public IStateLiveData<Todo> addTodo(Todo todo) {
        return mTodoRepo.addTodo(todo);
    }

    public IStateLiveData<TodoList> addTodoList(TodoList todoList) {
        return mTodoRepo.addTodoList(todoList);
    }

    public IStateLiveData<String> updateTodo(String id, Map<String, Object> changes) {
        return mTodoRepo.modifyTodo(id, changes);
    }

    public MutableLiveData<Boolean> isCardExpand() {
        return mIsCardExpand;
    }

    public MutableLiveData<String> getTodoListTitle() {
        return mTodoListTitle;
    }

    public MutableLiveData<String> getTodoTitle() {
        return mTodoTitle;
    }

    public MutableLiveData<String> getTodoDesc() {
        return mTodoDesc;
    }

    public MutableLiveData<String> getTodoDate() {
        return mTodoDate;
    }

    public MutableLiveData<String> getTodoTime() {
        return mTodoTime;
    }
}
