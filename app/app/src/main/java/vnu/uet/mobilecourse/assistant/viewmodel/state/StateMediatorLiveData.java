package vnu.uet.mobilecourse.assistant.viewmodel.state;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class StateMediatorLiveData<T> extends StateLiveData<T> {

    private MediatorLiveData<T> mediatorListener = new MediatorLiveData<>();

    public StateMediatorLiveData() {
        super();
    }

    public StateMediatorLiveData(StateModel<T> value) {
        super(value);
    }

    public MediatorLiveData<T> getMediatorListener() {
        return mediatorListener;
    }
}
