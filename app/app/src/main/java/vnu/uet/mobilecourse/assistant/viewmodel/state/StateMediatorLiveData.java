package vnu.uet.mobilecourse.assistant.viewmodel.state;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

public class StateMediatorLiveData<T> extends MediatorLiveData<StateModel<T>> implements IStateLiveData<T> {

    public StateMediatorLiveData() {

    }

    public StateMediatorLiveData(StateModel<T> value) {
        postValue(value);
    }

    @Override
    public void postLoading() {
        setValue(new StateModel<>(StateStatus.LOADING));
    }

    @Override
    public void postSuccess(@NonNull T data) {
        setValue(new StateModel<>(StateStatus.SUCCESS, data));
    }

    @Override
    public void postError(@NonNull Exception e) {
        setValue(new StateModel<>(StateStatus.ERROR, e));
    }
}
