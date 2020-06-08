package vnu.uet.mobilecourse.assistant.viewmodel;

import android.os.Bundle;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.ViewModel;

import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.model.event.DailyEventList;
import vnu.uet.mobilecourse.assistant.repository.firebase.EventRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.expandable.ExpandableTodoList;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class CalendarViewModel extends ViewModel {
    private TodoRepository mTodoRepo = TodoRepository.getInstance();

    private Bundle mTodoListViewState;
    private Bundle mDailyListViewState;

    public void setTodoListViewState(Bundle mTodoListViewState) {
        this.mTodoListViewState = mTodoListViewState;
    }

    public Bundle getTodoListViewState() {
        return mTodoListViewState;
    }

    public Bundle getDailyListViewState() {
        return mDailyListViewState;
    }

    public void setDailyListViewState(Bundle mDailyListViewState) {
        this.mDailyListViewState = mDailyListViewState;
    }

    public IStateLiveData<DailyEventList> getDailyTodoList(Date date) {
        return EventRepository.getInstance().getDailyEvent(date);
    }

    public IStateLiveData<String> markTodoAsDone(String id) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("completed", true);

        return mTodoRepo.modifyTodo(id, changes);
    }

    public IStateLiveData<String> markTodoAsDoing(String id) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("completed", false);

        return mTodoRepo.modifyTodo(id, changes);
    }

    public IStateLiveData<List<TodoList>> getShallowTodoLists() {
        return mTodoRepo.getShallowTodoLists();
    }

    public IStateLiveData<List<TodoList>> getAllTodoLists() {
        return mTodoRepo.getAllTodoLists();
    }

    public IStateLiveData<String> deleteTodo(String id) {
        return mTodoRepo.deleteTodo(id);
    }

    public IStateLiveData<String> deleteTodoList(String id, List<Todo> todos) {
        return mTodoRepo.deleteTodoList(id, todos);
    }
}