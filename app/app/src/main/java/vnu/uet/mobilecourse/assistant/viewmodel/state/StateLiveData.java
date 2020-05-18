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
