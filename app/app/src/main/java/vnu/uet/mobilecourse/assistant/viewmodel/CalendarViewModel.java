package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;

public class CalendarViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<TodoList> createdTodoList;

    public CalendarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");

        createdTodoList = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<TodoList> getCreatedTodoList() {
        return createdTodoList;
    }

    public void setCreatedTodoList(TodoList createdTodoList) {
        this.createdTodoList.postValue(createdTodoList);
    }
}