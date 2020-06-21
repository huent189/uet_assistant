package vnu.uet.mobilecourse.assistant.viewmodel.state;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class StateLiveData<T> extends LiveData<StateModel<T>> implements IStateLiveData<T> {
    public StateLiveData() {
    }

    public StateLiveData(StateModel<T> value) {
        super(value);
    }

    @Override
    public void postLoading() {
        postValue(new StateModel<>(StateStatus.LOADING));
    }

    @Override
    public void postSuccess(@NonNull T data) {
        postValue(new StateModel<>(StateStatus.SUCCESS, data));
    }

    @Override
    public void postError(@NonNull Exception e) {
        postValue(new StateModel<>(StateStatus.ERROR, e));
    }

}
