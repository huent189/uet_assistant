package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.repository.firebase.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class CalendarViewModel extends ViewModel {
    private TodoRepository mTodoRepo = TodoRepository.getInstance();

    public IStateLiveData<DailyTodoList> getDailyTodoList(Date date) {
        return mTodoRepo.getDailyTodoList(date);
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

    public IStateLiveData<String> deleteTodo(String id) {
        return mTodoRepo.deleteTodo(id);
    }
}