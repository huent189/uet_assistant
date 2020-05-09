package vnu.uet.mobilecourse.assistant.viewmodel.state;

import androidx.lifecycle.LiveData;

public class StateLiveData<T> extends LiveData<StateModel<T>> {
    public StateLiveData() {
        super();
    }

    public StateLiveData(StateModel<T> value) {
        super(value);
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
