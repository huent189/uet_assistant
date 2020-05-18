package vnu.uet.mobilecourse.assistant.viewmodel.state;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class StateMediatorLiveData<T> extends MediatorLiveData<StateModel<T>> {

    public StateMediatorLiveData() {
    }

    public StateMediatorLiveData(StateModel<T> value) {
        postValue(value);
    }

    public void postLoading(){
        setValue(new StateModel<T>(StateStatus.LOADING));
    }
    public void postSuccess(T data){
        setValue(new StateModel<T>(StateStatus.SUCCESS, data));
    }
    public void postError(Exception e){
        setValue(new StateModel<T>(StateStatus.ERROR, e));
    }
}
