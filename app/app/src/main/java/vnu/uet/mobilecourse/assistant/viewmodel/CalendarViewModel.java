package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.Date;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;

public class CalendarViewModel extends ViewModel {
    private TodoRepository todoRepo = TodoRepository.getInstance();

    public StateMediatorLiveData<DailyTodoList> getDailyTodoList(Date date) {
        return todoRepo.getDailyTodoList(date);
    }
}